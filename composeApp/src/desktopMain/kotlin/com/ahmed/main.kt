package com.ahmed

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.ahmed.model.DatabaseManager
import com.ahmed.store.initAppStorage

fun main() = application {
    val state = rememberWindowState(placement = WindowPlacement.Maximized)

    Window(
        onCloseRequest = {
            DatabaseManager.dispose()
            exitApplication()
        },

        state = state,
        undecorated = true // Disable default window decorations

    ) {
        initAppStorage()
        DatabaseManager.init()
        App(
            windowState = state,
            onCloseRequest = {
                DatabaseManager.dispose()
                exitApplication()
            }
        )
    }
}