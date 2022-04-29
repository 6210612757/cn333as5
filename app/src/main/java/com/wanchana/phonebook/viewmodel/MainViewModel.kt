package com.wanchana.phonebook.viewmodel

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanchana.phonebook.MainActivity
import com.wanchana.phonebook.database.AppDatabase
import com.wanchana.phonebook.database.DbMapper
import com.wanchana.phonebook.database.Repository
import com.wanchana.phonebook.domain.model.BookModel
import com.wanchana.phonebook.domain.model.ColorModel
import com.wanchana.phonebook.routing.PhoneBookRouter
import com.wanchana.phonebook.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(application: Application) : ViewModel() {
    val bookNotInTrash: LiveData<List<BookModel>> by lazy {
        repository.getAllBookNotInTrash()
    }

    private var _bookEntry = MutableLiveData(BookModel())

    val bookEntry: LiveData<BookModel> = _bookEntry

    val colors: LiveData<List<ColorModel>> by lazy {
        repository.getAllColors()
    }

    val bookInTrash by lazy { repository.getAllBookInTrash() }

    private var _selectedBook = MutableLiveData<List<BookModel>>(listOf())

    val selectedBook: LiveData<List<BookModel>> = _selectedBook

    private val repository: Repository

    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository(db.bookDao(), db.colorDao(), DbMapper())
    }

    fun onCreateNewBookClick() {
        _bookEntry.value = BookModel()
        PhoneBookRouter.navigateTo(Screen.SaveBook)
    }

    fun onBookClick(book: BookModel) {
        _bookEntry.value = book
        PhoneBookRouter.navigateTo(Screen.SaveBook)
    }

    fun onBookCheckedChange(book: BookModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertBook(book)
        }
    }

    fun onBookSelected(book: BookModel) {
        _selectedBook.value = _selectedBook.value!!.toMutableList().apply {
            if (contains(book)) {
                remove(book)
            } else {
                add(book)
            }
        }
    }

    fun restoreBook(book: List<BookModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.restoreBookFromTrash(book.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedBook.value = listOf()
            }
        }
    }

    fun permanentlyDeleteBook(book: List<BookModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteBook(book.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedBook.value = listOf()
            }
        }
    }

    fun onBookEntryChange(book: BookModel) {
        _bookEntry.value = book
    }

    fun saveBook(book: BookModel) {
        viewModelScope.launch(Dispatchers.Default) {
            if(book.name == "" || book.pNumber == "" || book.tag == ""){

                println("Invalid input found")

                PhoneBookRouter.navigateTo(Screen.SaveBook)
//                _bookEntry.value = BookModel()

            }else {
                repository.insertBook(book)
                withContext(Dispatchers.Main) {
                    PhoneBookRouter.navigateTo(Screen.Book)

                    _bookEntry.value = BookModel()
                }
            }

        }
    }


    fun moveBookToTrash(book: BookModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.moveBookToTrash(book.id)

            withContext(Dispatchers.Main) {
                PhoneBookRouter.navigateTo(Screen.Book)
            }
        }
    }
}