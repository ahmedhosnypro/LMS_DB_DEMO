package com.ahmed.ui.database

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.DatabaseManager
import com.ahmed.store.DbConfig
import com.ahmed.store.dbConfigStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseSettingsDialog(onDismissRequest: () -> Unit) {
    val dbConfig = remember { mutableStateOf(DbConfig()) }

    LaunchedEffect(Unit) {
        dbConfigStore.get()?.let {
            dbConfig.value = it
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Database Settings") },
        text = { Form(dbConfig) },
        confirmButton = {
            Actions(
                onDismissRequest = onDismissRequest,
                dbConfig = dbConfig
            )
        },
    )
}

@Composable
fun Form(
    dbConfig: MutableState<DbConfig>
) {

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = dbConfig.value.databaseName,
            onValueChange = { dbConfig.value = dbConfig.value.copy(databaseName = it) },
            label = { Text("Database Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = dbConfig.value.host,
            onValueChange = { dbConfig.value = dbConfig.value.copy(host = it) },
            label = { Text("Host") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = dbConfig.value.port.toString(),
            onValueChange = { portString ->
                dbConfig.value = dbConfig.value.copy(port = portString.toIntOrNull() ?: dbConfig.value.port)
            },
            label = { Text("Port") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = dbConfig.value.username,
            onValueChange = { dbConfig.value = dbConfig.value.copy(username = it) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = dbConfig.value.password,
            onValueChange = { dbConfig.value = dbConfig.value.copy(password = it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Actions(
    onDismissRequest: () -> Unit,
    dbConfig: MutableState<DbConfig>
) {
    val coroutineScope = rememberCoroutineScope()
    var testConnectionResult by remember { mutableStateOf<Boolean?>(null) }
    var testingConnection by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cancel Button
        Button(
            onClick = onDismissRequest,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel"
            )
            Spacer(Modifier.width(8.dp))
            Text("Cancel")
        }

        // Test Connection Button
        Button(
            onClick = {
                testingConnection = true
                coroutineScope.launch {
                    testConnectionResult = DatabaseManager.testConnection(dbConfig.value)
                    testingConnection = false
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            if (!testingConnection) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Test Connection"
                )
                Spacer(Modifier.width(8.dp))
            }
            Text("Test Connection")
            if (testingConnection) {
                Spacer(Modifier.width(8.dp))
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                testConnectionResult?.let {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = if (it) Icons.Default.Done else Icons.Default.CloudOff,
                        contentDescription = if (it) "Connection Successful" else "Connection Failed",
                        tint = if (it) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Save Button
        Button(
            onClick = {
                coroutineScope.launch {
                    dbConfigStore.set(dbConfig.value)
                    DatabaseManager.init() // Re-initialize with new settings
                    onDismissRequest()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Save"
            )
            Spacer(Modifier.width(8.dp))
            Text("Save")
        }
    }
}
