package com.wanchana.phonebook.database

import com.wanchana.phonebook.domain.model.BookModel
import com.wanchana.phonebook.domain.model.ColorModel
import com.wanchana.phonebook.domain.model.NEW_BOOK_ID

class DbMapper {
    // Create list of NoteModels by pairing each note with a color
    fun mapBook(
        noteDbModels: List<BookDbModel>,
        colorDbModels: Map<Long, ColorDbModel>
    ): List<BookModel> = noteDbModels.map {
        val colorDbModel = colorDbModels[it.colorId]
            ?: throw RuntimeException("Color for colorId: ${it.colorId} was not found. Make sure that all colors are passed to this method")

        mapBook(it, colorDbModel)
    }

    // convert BookDbModel to BookModel
    fun mapBook(bookDbModel: BookDbModel, colorDbModel: ColorDbModel): BookModel {
        val color = mapColor(colorDbModel)
        val isCheckedOff = with(bookDbModel) { if (canBeCheckedOff) isCheckedOff else null }
        return with(bookDbModel) { BookModel(id, name, pNumber,tag, isCheckedOff, color) }
    }

    // convert list of ColorDdModels to list of ColorModels
    fun mapColors(colorDbModels: List<ColorDbModel>): List<ColorModel> =
        colorDbModels.map { mapColor(it) }

    // convert ColorDbModel to ColorModel
    fun mapColor(colorDbModel: ColorDbModel): ColorModel =
        with(colorDbModel) { ColorModel(id, name, hex) }

    // convert NoteModel back to NoteDbModel
    fun mapDbBook(note: BookModel): BookDbModel =
        with(note) {
            val canBeCheckedOff = isCheckedOff != null
            val isCheckedOff = isCheckedOff ?: false
            if (id == NEW_BOOK_ID)
                BookDbModel(
                    name = name,
                    pNumber = pNumber,
                    tag = tag,
                    canBeCheckedOff = canBeCheckedOff,
                    isCheckedOff = isCheckedOff,
                    colorId = color.id,
                    isInTrash = false
                )
            else
                BookDbModel(id, name, pNumber,tag, canBeCheckedOff, isCheckedOff, color.id, false)
        }
}