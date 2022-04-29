package com.wanchana.phonebook.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wanchana.phonebook.domain.model.BookModel
import com.wanchana.phonebook.routing.Screen
import com.wanchana.phonebook.ui.components.AppDrawer
import com.wanchana.phonebook.ui.components.SearchBar
import com.wanchana.phonebook.ui.components.Book
import com.wanchana.phonebook.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun BookScreen(viewModel: MainViewModel) {
    val book by viewModel.bookNotInTrash.observeAsState(listOf())
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val searchBy = remember { mutableStateOf("") }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Phone book",
                        color = MaterialTheme.colors.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch { scaffoldState.drawerState.open() }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Drawer Button"
                        )
                    }
                }
            )
        },
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Book,
                closeDrawerAction = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onCreateNewBookClick() },
                contentColor = MaterialTheme.colors.background,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Book Button"
                    )
                }
            )
        }
    )
    {
        if (book.isNotEmpty()) {
            Column(){
                SearchBar(searchBy = searchBy)
                BookList(
                    book = searchBook(searchBy.value,book),
                    onBookCheckedChange = {
                        viewModel.onBookCheckedChange(it)
                    },
                    onBookClick = { viewModel.onBookClick(it) }
                )
            }
        }
    }

}
fun searchBook(searchBy:String,bookList : List<BookModel>):List<BookModel>{
    val searched : ArrayList<BookModel> = ArrayList()
    for(aBook in bookList){
        val searchKey = aBook.name + aBook.tag +aBook.pNumber
        if(searchKey.contains(searchBy,ignoreCase = true)){
            searched.add(aBook)
        }
    }
    return searched.toList()
}

@ExperimentalMaterialApi
@Composable
private fun BookList(
    book: List<BookModel>,
    onBookCheckedChange: (BookModel) -> Unit,
    onBookClick: (BookModel) -> Unit
) {
    LazyColumn {
        items(count = book.size) { bookIndex ->
            val aBook = book[bookIndex]
            Book(
                book = aBook,
                onBookClick = onBookClick,
                onBookCheckedChange = onBookCheckedChange,
                isSelected = false
            )
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun BookListPreview() {
    BookList(
        book = listOf(
            BookModel(1, "Johny Dep", "0895555555","Mobile", null),
            BookModel(2, "Wave Moon", "0871234569","Home", false),
            BookModel(3, "Ember Heard", "0866666666","Work", true)
        ),
        onBookCheckedChange = {},
        onBookClick = {}
    )
}