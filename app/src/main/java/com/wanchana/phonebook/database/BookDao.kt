package com.wanchana.phonebook.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookDao {

    @Query("SELECT * FROM BookDbModel")
    fun getAllSync(): List<BookDbModel>

    @Query("SELECT * FROM BookDbModel WHERE id IN (:bookIds)")
    fun getBookByIdsSync(bookIds: List<Long>): List<BookDbModel>

    @Query("SELECT * FROM BookDbModel WHERE id LIKE :id")
    fun findByIdSync(id: Long):BookDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bookDbModel: BookDbModel)

    @Insert
    fun insertAll(vararg bookDbModel: BookDbModel)

    @Query("DELETE FROM BookDbModel WHERE id LIKE :id")
    fun delete(id: Long)

    @Query("DELETE FROM BookDbModel WHERE id IN (:bookIds)")
    fun delete(bookIds: List<Long>)
}