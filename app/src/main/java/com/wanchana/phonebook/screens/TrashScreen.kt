package com.wanchana.phonebook.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import com.wanchana.phonebook.R
import com.wanchana.phonebook.domain.model.BookModel
import com.wanchana.phonebook.routing.Screen
import com.wanchana.phonebook.ui.components.AppDrawer
import com.wanchana.phonebook.ui.components.Book
import com.wanchana.phonebook.viewmodel.MainViewModel
import kotlinx.coroutines.launch

private const val NO_DIALOG = 1
private const val RESTORE_BOOK_DIALOG = 2
private const val PERMANENTLY_DELETE_DIALOG = 3

@Composable
@ExperimentalMaterialApi
fun TrashScreen(viewModel: MainViewModel) {

    val bookInThrash: List<BookModel> by viewModel.bookInTrash
        .observeAsState(listOf())

    val selectedBook: List<BookModel> by viewModel.selectedBook
        .observeAsState(listOf())

    val dialogState = rememberSaveable { mutableStateOf(NO_DIALOG) }

    val scaffoldState = rememberScaffoldState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val areActionsVisible = selectedBook.isNotEmpty()
            TrashTopAppBar(
                onNavigationIconClick = {
                    coroutineScope.launch { scaffoldState.drawerState.open() }
                },
                onRestoreBookClick = { dialogState.value = RESTORE_BOOK_DIALOG },
                onDeleteBookClick = { dialogState.value = PERMANENTLY_DELETE_DIALOG },
                areActionsVisible = areActionsVisible
            )
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Trash,
                closeDrawerAction = {
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }
            )
        },
        content = {
            Content(
                book = bookInThrash,
                onBookClick = { viewModel.onBookSelected(it) },
                selectedBook = selectedBook
            )

            val dialog = dialogState.value
            if (dialog != NO_DIALOG) {
                val confirmAction: () -> Unit = when (dialog) {
                    RESTORE_BOOK_DIALOG -> {
                        {
                            viewModel.restoreBook(selectedBook)
                            dialogState.value = NO_DIALOG
                        }
                    }
                    PERMANENTLY_DELETE_DIALOG -> {
                        {
                            viewModel.permanentlyDeleteBook(selectedBook)
                            dialogState.value = NO_DIALOG
                        }
                    }
                    else -> {
                        {
                            dialogState.value = NO_DIALOG
                        }
                    }
                }

                AlertDialog(
                    onDismissRequest = { dialogState.value = NO_DIALOG },
                    title = { Text(mapDialogTitle(dialog)) },
                    text = { Text(mapDialogText(dialog)) },
                    confirmButton = {
                        TextButton(onClick = confirmAction) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dialogState.value = NO_DIALOG }) {
                            Text("Dismiss")
                        }
                    }
                )
            }
        }
    )
}

@Composable
private fun TrashTopAppBar(
    onNavigationIconClick: () -> Unit,
    onRestoreBookClick: () -> Unit,
    onDeleteBookClick: () -> Unit,
    areActionsVisible: Boolean
) {
    TopAppBar(
        title = { Text(text = "Trash", color = MaterialTheme.colors.onPrimary) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Drawer Button"
                )
            }
        },
        actions = {
            if (areActionsVisible) {
                IconButton(onClick = onRestoreBookClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_restore_from_trash_24),
                        contentDescription = "Restore Book Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
                IconButton(onClick = onDeleteBookClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                        contentDescription = "Delete Book Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
@ExperimentalMaterialApi
private fun Content(
    book: List<BookModel>,
    onBookClick: (BookModel) -> Unit,
    selectedBook: List<BookModel>,
) {
    val tabs = listOf("REGULAR", "CHECKABLE")

    // Init state for selected tab
    var selectedTab by remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        val filteredBook = when (selectedTab) {
            0 -> {
                book.filter { it.isCheckedOff == null }
            }
            1 -> {
                book.filter { it.isCheckedOff != null }
            }
            else -> throw IllegalStateException("Tab not supported - index: $selectedTab")
        }

        LazyColumn {
            items(count = filteredBook.size) { bookIndex ->
                val abook = filteredBook[bookIndex]
                val isBookSelected = selectedBook.contains(abook)
                Book(
                    book = abook,
                    onBookClick = onBookClick,
                    isSelected = isBookSelected
                )
            }
        }
    }
}

private fun mapDialogTitle(dialog: Int): String = when (dialog) {
    RESTORE_BOOK_DIALOG -> "Restore book"
    PERMANENTLY_DELETE_DIALOG -> "Delete book forever"
    else -> throw RuntimeException("Dialog not supported: $dialog")
}

private fun mapDialogText(dialog: Int): String = when (dialog) {
    RESTORE_BOOK_DIALOG -> "Are you sure you want to restore selected book?"
    PERMANENTLY_DELETE_DIALOG -> "Are you sure you want to delete selected book permanently?"
    else -> throw RuntimeException("Dialog not supported: $dialog")
}