@file:OptIn(ExperimentalMaterial3Api::class)

package com.ahmed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.WindowPlacement
import com.ahmed.ui.database.ConnectionStatus
import com.ahmed.ui.database.DatabaseTopBarMenu
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.rememberWindowState
import com.ahmed.ui.modifier.bottomBorder
import com.ahmed.ui.modifier.topBorder
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppScaffold(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    windowState: WindowState,
    onCloseRequest: () -> Unit,
    content: @Composable (snackbarHostState: SnackbarHostState) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                snackbarHostState,
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.background),
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    )
                }
            )
        },
        topBar = { AppTopBar(snackbarHostState, isDarkTheme, onThemeToggle, windowState, onCloseRequest) },
        bottomBar = { AppBottomBar() },
    ) { paddingValues ->
        Box(
            Modifier.padding(paddingValues)
        ) {
            content(snackbarHostState)
        }
    }
}


@Composable
fun AppTopBar(
    snackbarHostState: SnackbarHostState,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    windowState: WindowState,
    onCloseRequest: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App title on the left
                Text("LMS DB Demo")
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DatabaseTopBarMenu(snackbarHostState)

                // Theme Switcher
                IconButton(onClick = onThemeToggle) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                        contentDescription = "Toggle Theme"
                    )
                }

                // Window Controls
                IconButton(
                    onClick = { windowState.isMinimized = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Minimize Window"
                    )
                }

                IconButton(
                    onClick = {
                        windowState.placement = if (windowState.placement == WindowPlacement.Maximized) {
                            WindowPlacement.Floating
                        } else {
                            WindowPlacement.Maximized
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (windowState.placement == WindowPlacement.Maximized)
                            Icons.Default.FilterNone else Icons.Default.Fullscreen,
                        contentDescription = "Maximize/Restore Window"
                    )
                }

                IconButton(
                    onClick = onCloseRequest
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Window"
                    )
                }
            }
        },
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            scrolledContainerColor = Color.Unspecified,
            navigationIconContentColor = Color.Unspecified,
            titleContentColor = Color.White,
            actionIconContentColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier
            .bottomBorder(color = MaterialTheme.colorScheme.secondary, height = 0.5f)
    )
}

@Composable
fun AppBottomBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .topBorder(color = MaterialTheme.colorScheme.secondary, height = 0.5f)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        ConnectionStatus()
    }
}

@Preview
@Composable
fun AppScaffoldPreview() {
    CenteredDarkPreview {
        AppScaffold(
            isDarkTheme = true,
            onThemeToggle = {},
            windowState = rememberWindowState(),
            onCloseRequest = {}
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Content Area")
            }
        }
    }
}

@Preview
@Composable
fun AppScaffoldWithSnackbarPreview() {
    CenteredDarkPreview {
        AppScaffold(
            isDarkTheme = true,
            onThemeToggle = {},
            windowState = rememberWindowState(),
            onCloseRequest = {}
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Content Area")
            }
        }
    }
}

