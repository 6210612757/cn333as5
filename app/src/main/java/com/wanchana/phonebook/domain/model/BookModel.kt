package com.wanchana.phonebook.domain.model


const val NEW_BOOK_ID = -1L

data class BookModel(
    val id: Long = NEW_BOOK_ID,
    val name: String = "",
    val pNumber: String = "",
    val tag: String = "",
    val isCheckedOff: Boolean? = null,
    val color: ColorModel = ColorModel.DEFAULT
)