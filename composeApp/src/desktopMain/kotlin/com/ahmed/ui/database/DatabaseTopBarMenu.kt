package com.ahmed.ui.database

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ahmed.model.DatabaseManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
 fun DatabaseTopBarMenu(snackbarHostState: SnackbarHostState) {
    var showMenu by remember { mutableStateOf(false) }
    IconButton(onClick = { showMenu = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More options"
        )
    }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        InitializeDatabaseMenuItem(snackbarHostState) {
            showMenu = false
        }
        ResetDatabaseMenuItem(snackbarHostState) {
            showMenu = false
        }
        DemoDataMenuItem(snackbarHostState) {
            showMenu = false
        }
    }
}

@Composable
fun ConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    text: String
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(text) },
            confirmButton = {
                TextButton(
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.error,
                        disabledContentColor = MaterialTheme.colorScheme.onError
                    ),
                    onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun InitializeDatabaseMenuItem(
    snackbarHostState: SnackbarHostState,
    closeMenu: () -> Unit,
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    DropdownMenuItem(
        text = { Text("Initialize Database") },
        onClick = {
            showConfirmDialog = true
        }
    )

    ConfirmationDialog(
        showDialog = showConfirmDialog,
        onDismiss = { showConfirmDialog = false },
        onConfirm = {
            closeMenu()
            GlobalScope.launch {
                snackbarHostState.showSnackbar("Initializing database...")

                val (success, message) = DatabaseManager.initializeDatabase()
                if (success) {
                    snackbarHostState.showSnackbar(
                        message = "✅ $message",
                        actionLabel = "OK",
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                } else {
                    snackbarHostState.showSnackbar(
                        "❌ $message",
                        actionLabel = "OK",
                        withDismissAction = true,
                        duration = SnackbarDuration.Indefinite
                    )
                }
            }
        },
        title = "Confirm Initialization",
        text = "Are you sure you want to initialize the database? This action cannot be undone."
    )
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ResetDatabaseMenuItem(
    snackbarHostState: SnackbarHostState,
    closeMenu: () -> Unit,
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    DropdownMenuItem(
        text = { Text("Reset Database") },
        onClick = {
            showConfirmDialog = true
        }
    )

    ConfirmationDialog(
        showDialog = showConfirmDialog,
        onDismiss = { showConfirmDialog = false },
        onConfirm = {
            closeMenu()
            GlobalScope.launch {
                snackbarHostState.showSnackbar("Resetting database...")

                val (success, message) = DatabaseManager.resetDatabase()
                if (success) {
                    snackbarHostState.showSnackbar(
                        message = "✅ $message",
                        actionLabel = "OK",
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                } else {
                    snackbarHostState.showSnackbar(
                        "❌ $message",
                        actionLabel = "OK",
                        withDismissAction = true,
                        duration = SnackbarDuration.Indefinite
                    )
                }
            }
        },
        title = "Confirm Reset",
        text = "Are you sure you want to reset the database? This will delete all data."
    )
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun DemoDataMenuItem(
    snackbarHostState: SnackbarHostState,
    closeMenu: () -> Unit,
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    DropdownMenuItem(
        text = { Text("Load Demo Data") },
        onClick = {
            showConfirmDialog = true
        }
    )

    ConfirmationDialog(
        showDialog = showConfirmDialog,
        onDismiss = { showConfirmDialog = false },
        onConfirm = {
            closeMenu()
            GlobalScope.launch {
                snackbarHostState.showSnackbar("Loading demo data...")

                val (success, message) = DatabaseManager.loadDemoData()
                if (success) {
                    snackbarHostState.showSnackbar(
                        message = "✅ $message",
                        actionLabel = "OK",
                        withDismissAction = true,
                        duration = SnackbarDuration.Short
                    )
                } else {
                    snackbarHostState.showSnackbar(
                        "❌ $message",
                        actionLabel = "OK",
                        withDismissAction = true,
                        duration = SnackbarDuration.Indefinite
                    )
                }
            }
        },
        title = "Confirm Load Demo Data",
        text = "Are you sure you want to load demo data? This may overwrite existing data."
    )
}