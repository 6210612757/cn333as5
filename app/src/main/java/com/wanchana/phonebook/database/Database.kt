package com.wanchana.phonebook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BookDbModel::class, ColorDbModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun colorDao(): ColorDao

    companion object {
        private const val DATABASE_NAME = "phone-book-database"
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            var instance = INSTANCE
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()

                INSTANCE = instance
            }

            return instance
        }
    }
}