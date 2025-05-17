package com.ahmed.ui.database

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ahmed.model.DatabaseManager

private val SuccessColor = Color(0xFF4CAF50) // Material Design Success Green

@Composable
fun ConnectionStatus() {
    val isConnected = DatabaseManager.connectionStatus.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
        Icon(
            imageVector = if (isConnected.value) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = if (isConnected.value) "Connected" else "Disconnected",
            tint = if (isConnected.value) SuccessColor else MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (isConnected.value) "Connected" else "Disconnected",
            color = if (isConnected.value) SuccessColor else MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = { showSettingsDialog = true }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Database Settings",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Settings", color = MaterialTheme.colorScheme.primary)
        }
    }

    if (showSettingsDialog) {
        DatabaseSettingsDialog(onDismissRequest = { showSettingsDialog = false })
    }
}
