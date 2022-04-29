package com.wanchana.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "pNumber") val pNumber: String,
    @ColumnInfo(name = "tag") val tag: String,
    @ColumnInfo(name = "can_be_checked_off") val canBeCheckedOff: Boolean,
    @ColumnInfo(name = "is_checked_off") val isCheckedOff: Boolean,
    @ColumnInfo(name = "color_id") val colorId: Long,
    @ColumnInfo(name = "in_trash") val isInTrash: Boolean
) {
    companion object {
        val DEFAULT_BOOK = listOf(
            BookDbModel(1, "Bob DoSomething", "0871234567","Mobile", false, false, 1, false),
            BookDbModel(2, "Bills Gate", "1871234567","Home", false, false, 2, false),
            BookDbModel(3, "Pancake Kem", "2871234567","Work", false, false, 3, false),
            BookDbModel(4, "Work tilldie", "3871234567","Mobile", false, false, 4, false),
            BookDbModel(5, "Tom Cruise", "4871234567","Home", false, false, 5, false),
            BookDbModel(6, "Josh Wdish", "5871234567","Work", true, false, 12, false)
        )
    }
}