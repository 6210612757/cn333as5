package com.wanchana.phonebook.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.wanchana.phonebook.domain.model.BookModel
import com.wanchana.phonebook.domain.model.ColorModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Repository(
    private val bookDao: BookDao,
    private val colorDao: ColorDao,
    private val dbMapper: DbMapper
) {

    // Working Notes
    private val bookNotInTrashLiveData: MutableLiveData<List<BookModel>> by lazy {
        MutableLiveData<List<BookModel>>()
    }

    fun getAllBookNotInTrash(): LiveData<List<BookModel>> = bookNotInTrashLiveData

    // Deleted Notes
    private val bookInTrashLiveData: MutableLiveData<List<BookModel>> by lazy {
        MutableLiveData<List<BookModel>>()
    }

    fun getAllBookInTrash(): LiveData<List<BookModel>> = bookInTrashLiveData

    init {
        initDatabase(this::updateBookLiveData)
    }

    /**
     * Populates database with colors if it is empty.
     */
    private fun initDatabase(postInitAction: () -> Unit) {
        GlobalScope.launch {
            // Prepopulate colors
            val colors = ColorDbModel.DEFAULT_COLORS.toTypedArray()
            val dbColors = colorDao.getAllSync()
            if (dbColors.isNullOrEmpty()) {
                colorDao.insertAll(*colors)
            }

            // Prepopulate notes
            val book = BookDbModel.DEFAULT_BOOK.toTypedArray()
            val dbBook = bookDao.getAllSync()
            if (dbBook.isNullOrEmpty()) {
                bookDao.insertAll(*book)
            }

            postInitAction.invoke()
        }
    }

    // get list of working Book or deleted Book
    private fun getAllBookDependingOnTrashStateSync(inTrash: Boolean): List<BookModel> {
        val colorDbModels: Map<Long, ColorDbModel> = colorDao.getAllSync().map { it.id to it }.toMap()
        val dbBook: List<BookDbModel> =
            bookDao.getAllSync().filter { it.isInTrash == inTrash }.sortedBy { it.name }
        return dbMapper.mapBook(dbBook, colorDbModels)
    }

    fun insertBook(book: BookModel) {
        bookDao.insert(dbMapper.mapDbBook(book))
        updateBookLiveData()
    }

    fun deleteBook(bookIds: List<Long>) {
        bookDao.delete(bookIds)
        updateBookLiveData()
    }

    fun moveBookToTrash(bookId: Long) {
        val dbBook = bookDao.findByIdSync(bookId)
        val newDbBook = dbBook.copy(isInTrash = true)
        bookDao.insert(newDbBook)
        updateBookLiveData()
    }

    fun restoreBookFromTrash(bookIds: List<Long>) {
        val dbBookInTrash = bookDao.getBookByIdsSync(bookIds)
        dbBookInTrash.forEach {
            val newDbBook = it.copy(isInTrash = false)
            bookDao.insert(newDbBook)
        }
        updateBookLiveData()
    }

    fun getAllColors(): LiveData<List<ColorModel>> =
        Transformations.map(colorDao.getAll()) { dbMapper.mapColors(it) }

    private fun updateBookLiveData() {
        bookNotInTrashLiveData.postValue(getAllBookDependingOnTrashStateSync(false))
        bookInTrashLiveData.postValue(getAllBookDependingOnTrashStateSync(true))
    }
}