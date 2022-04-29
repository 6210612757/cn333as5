package com.wanchana.phonebook.screens

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.text.isDigitsOnly
import com.wanchana.phonebook.R
import com.wanchana.phonebook.domain.model.BookModel
import com.wanchana.phonebook.domain.model.ColorModel
import com.wanchana.phonebook.domain.model.NEW_BOOK_ID
import com.wanchana.phonebook.routing.PhoneBookRouter
import com.wanchana.phonebook.routing.Screen
import com.wanchana.phonebook.ui.components.BookIcon
import com.wanchana.phonebook.utils.fromHex
import com.wanchana.phonebook.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun SaveBookScreen(viewModel: MainViewModel) {
    val bookEntry by viewModel.bookEntry.observeAsState(BookModel())

    val colors: List<ColorModel> by viewModel.colors.observeAsState(listOf())

    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val moveBookToTrashDialogShownState = rememberSaveable { mutableStateOf(false) }

//    val snackBarHostState = remember { SnackbarHostState() }

//    val mExpanded: MutableState<Boolean> = remember { mutableStateOf(false)}

//    val groupToSelect = listOf("Home","Mobile","Work")

//    val selectedGroup: MutableState<String> = remember { mutableStateOf(bookEntry.tag)}

//    val selectTextFieldSize: MutableState<Size> = remember { mutableStateOf(Size.Zero)}

    val openDialog = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    BackHandler {
        if (bottomDrawerState.isOpen) {
            coroutineScope.launch { bottomDrawerState.close() }
        } else {
            PhoneBookRouter.navigateTo(Screen.Book)
        }
    }

    Scaffold(
        topBar = {
            val isEditingMode: Boolean = bookEntry.id != NEW_BOOK_ID
            SaveBookTopAppBar(
                isEditingMode = isEditingMode,
                onBackClick = { PhoneBookRouter.navigateTo(Screen.Book) },
                onSaveBookClick = {
                    if (bookEntry.name == "" || bookEntry.pNumber == "" || bookEntry.tag == ""){
                        alertText = "Please provide data (Data can't be empty)"
                        openDialog.value = true
                    }else if(!bookEntry.pNumber.isDigitsOnly()){
                        alertText = "Phone number should contains only digits"
                        openDialog.value = true
                    }else{
                    viewModel.saveBook(bookEntry) }
                                  },
                onOpenColorPickerClick = {
                    coroutineScope.launch { bottomDrawerState.open() }
                },
                onDeleteBookClick = {
                    moveBookToTrashDialogShownState.value = true
                }
            )
        }
    ) {
        BottomDrawer(
            drawerState = bottomDrawerState,
            drawerContent = {
                ColorPicker(
                    colors = colors,
                    onColorSelect = { color ->
                        viewModel.onBookEntryChange(bookEntry.copy(color = color))
                    }
                )
            }
        ) {
            SaveBookContent(
                book = bookEntry,
//                expand = mExpanded,
//                groupToSelect = groupToSelect,
//                selectedGroup = selectedGroup,
//                selectTextFieldSize = selectTextFieldSize,
                onBookChange = { updateBookEntry ->
                    viewModel.onBookEntryChange(updateBookEntry)
                }
            )
        }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                title = {
                    Text(text = "Invalid input")
                },
                text = {
                    Column() {
                        Text(alertText)
                    }
                },
                buttons = {
                    Row(
                        modifier = Modifier.padding(all = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { openDialog.value = false }
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            )
        }

        if (moveBookToTrashDialogShownState.value) {
            AlertDialog(
                onDismissRequest = {
                    moveBookToTrashDialogShownState.value = false
                },
                title = {
                    Text("Move this Book to the trash?")
                },
                text = {
                    Text(
                        "Are you sure you want to " +
                                "move this Book to the trash?"
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.moveBookToTrash(bookEntry)
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        moveBookToTrashDialogShownState.value = false
                    }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}

@Composable
fun SaveBookTopAppBar(
    isEditingMode: Boolean,
    onBackClick: () -> Unit,
    onSaveBookClick: () -> Unit,
    onOpenColorPickerClick: () -> Unit,
    onDeleteBookClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Save Book",
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onSaveBookClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save Book Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            IconButton(onClick = onOpenColorPickerClick) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_color_lens_24),
                    contentDescription = "Open Color Picker Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            if (isEditingMode) {
                IconButton(onClick = onDeleteBookClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Book Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
private fun SaveBookContent(
//    expand: MutableState<Boolean>,
//    groupToSelect: List<String>,
//    selectedGroup: MutableState<String>,
//    selectTextFieldSize: MutableState<Size>,
    book: BookModel,
    onBookChange: (BookModel) -> Unit
) {
//    val icon = if (expand.value)
//        Icons.Filled.KeyboardArrowUp
//    else
//        Icons.Filled.KeyboardArrowDown


    Column(modifier = Modifier.fillMaxSize()) {
        ContentTextField(
            label = "Name",
            text = book.name,
            onTextChange = { newName ->
                onBookChange.invoke(book.copy(name = newName))
            }
        )

        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Phone number",
            text = book.pNumber,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            onTextChange = { newPNumber ->
                onBookChange.invoke(book.copy(pNumber = newPNumber))
            }
        )

//        OutlinedTextField(
//            readOnly = true,
//            value = selectedGroup.value,
//            onValueChange = {
//                selectedGroup.value = it
//                onBookChange.invoke(book.copy(tag = it))
//                            },
//            modifier = Modifier
//                .heightIn(max = 240.dp)
//                .padding(top = 16.dp)
//                .onGloballyPositioned { coordinates ->
//                    // This value is used to assign to
//                    // the DropDown the same width
//                    selectTextFieldSize.value = coordinates.size.toSize()
//                },
//
//            label = {Text("Type of contact")},
//            trailingIcon = {
//                Icon(icon,"contentDescription",
//                    Modifier.clickable { expand.value = !expand.value })
//            }
//        )
//
//        DropdownMenu(expanded = expand.value
//            ,onDismissRequest = { expand.value = false }
//            ,modifier = Modifier
//            .heightIn(max = 240.dp)
//            .padding(top = 16.dp)
//            .width(with(LocalDensity.current){selectTextFieldSize.value.width.toDp()}),
//            //properties = true,
//        ) {
//            groupToSelect.forEach{label -> DropdownMenuItem(onClick = { selectedGroup.value = label; expand.value=false })
//                { Text(text = label)
//                }
//            }
//        }
        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Tag",
            text = book.tag,
            onTextChange = { newTag ->
                onBookChange.invoke(book.copy(tag = newTag))
            }
        )

        val canBeCheckedOff: Boolean = book.isCheckedOff != null

        BookCheckOption(
            isChecked = canBeCheckedOff,
            onCheckedChange = { canBeCheckedOffNewValue ->
                val isCheckedOff: Boolean? = if (canBeCheckedOffNewValue) false else null

                onBookChange.invoke(book.copy(isCheckedOff = isCheckedOff))
            }
        )

        PickedColor(color = book.color)
    }
}

@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text,autoCorrect = false),
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(label) },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}


@Composable
private fun BookCheckOption(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Can book be checked off?",
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun PickedColor(color: ColorModel) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Picked color",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        BookIcon(
            color = Color.fromHex(color.hex),
            size = 40.dp,
            border = 1.dp,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
private fun ColorPicker(
    colors: List<ColorModel>,
    onColorSelect: (ColorModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Color picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(colors.size) { itemIndex ->
                val color = colors[itemIndex]
                ColorItem(
                    color = color,
                    onColorSelect = onColorSelect
                )
            }
        }
    }
}

@Composable
fun ColorItem(
    color: ColorModel,
    onColorSelect: (ColorModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onColorSelect(color)
                }
            )
    ) {
        BookIcon(
            modifier = Modifier.padding(10.dp),
            color = Color.fromHex(color.hex),
            size = 80.dp,
            border = 2.dp
        )
        Text(
            text = color.name,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview
@Composable
fun ColorItemPreview() {
    ColorItem(ColorModel.DEFAULT) {}
}

@Preview
@Composable
fun ColorPickerPreview() {
    ColorPicker(
        colors = listOf(
            ColorModel.DEFAULT,
            ColorModel.DEFAULT,
            ColorModel.DEFAULT
        )
    ) { }
}

@Preview
@Composable
fun PickedColorPreview() {
    PickedColor(ColorModel.DEFAULT)
}