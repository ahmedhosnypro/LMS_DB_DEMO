package com.ahmed

import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowState
import com.ahmed.ui.theme.AppTheme
import com.ahmed.ui.AppScaffold
import com.ahmed.ui.TabNavigation

@Composable
fun App(
    windowState: WindowState,
    onCloseRequest: () -> Unit
) {
    var isDarkTheme by remember { mutableStateOf(true) }

    AppTheme(isDarkTheme) {
        Surface {
            AppScaffold(
                isDarkTheme = isDarkTheme,
                onThemeToggle = { isDarkTheme = !isDarkTheme },
                windowState = windowState,
                onCloseRequest = onCloseRequest
            ) { snackbarHostState ->
                TabNavigation(snackbarHostState)
            }
        }
    }
}