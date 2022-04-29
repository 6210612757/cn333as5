package com.wanchana.phonebook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wanchana.phonebook.domain.model.BookModel
import com.wanchana.phonebook.utils.fromHex

@ExperimentalMaterialApi
@Composable
fun Book(
    modifier: Modifier = Modifier,
    book : BookModel,
    onBookClick: (BookModel) -> Unit = {},
    onBookCheckedChange: (BookModel) -> Unit = {},
    isSelected: Boolean
) {
    val background = if (isSelected)
        Color.LightGray
    else
        MaterialTheme.colors.surface

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        backgroundColor = background
    ) {
        ListItem(
            text = { Text(text = book.name + " ("+book.tag+")", maxLines = 1) },
            secondaryText = {
                Text(text = book.pNumber, maxLines = 1)
            },
            icon = {
                BookIcon(
                    color = Color.fromHex(book.color.hex),
                    size = 40.dp,
                    border = 1.dp
                )
            },
            trailing = {
                if (book.isCheckedOff != null) {
                    Checkbox(
                        checked = book.isCheckedOff,
                        onCheckedChange = { isChecked ->
                            val newBook = book.copy(isCheckedOff = isChecked)
                            onBookCheckedChange.invoke(newBook)
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            },
            modifier = Modifier.clickable {
                onBookClick.invoke(book)
            }
        )
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun BookPreview() {
    Book(book = BookModel(1, "Wanchana Moon", "0871234569", "Mobile"), isSelected = true)
}