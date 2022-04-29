package com.wanchana.phonebook.routing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


sealed class Screen {
    object Book: Screen()
    object SaveBook: Screen()
    object Trash: Screen()
}

object PhoneBookRouter {
    var currentScreen: Screen by mutableStateOf(Screen.Book)

    fun navigateTo(destination: Screen) {
        currentScreen = destination
    }
}