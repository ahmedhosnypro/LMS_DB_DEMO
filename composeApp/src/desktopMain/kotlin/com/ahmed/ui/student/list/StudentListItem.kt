package com.ahmed.ui.student.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ahmed.model.StudentDTO
import com.ahmed.model.StudentStatus
import com.ahmed.ui.CenteredDarkPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

private val KotlinRose = Color(0xffba203d)
private val KotlinPurple = Color(0xff8613b6)
private val KotlinPurpleBlue = Color(0xff5234ab)

private val StatusColors = mapOf(
    StudentStatus.ACTIVE to Color(0xFF4CAF50),
    StudentStatus.SUSPENDED to Color(0xFFF44336),
    StudentStatus.GRADUATED to Color(0xFF2196F3),
    StudentStatus.ON_LEAVE to Color(0xFFFF9800),
    StudentStatus.WITHDRAWN to Color(0xFF9E9E9E)
)

fun Modifier.studentListItemBackground(
    isSelected: Boolean
) : Modifier =
    background(
        brush = Brush.linearGradient(
            colors = if (isSelected) {
                listOf(KotlinRose, KotlinPurple, KotlinPurple, KotlinPurpleBlue)
            } else {
                listOf(
                    KotlinRose.copy(alpha = 0.8f),
                    KotlinPurple.copy(alpha = 0.8f),
                    KotlinPurple.copy(alpha = 0.8f),
                    KotlinPurpleBlue.copy(alpha = 0.8f)
                )
            }
        )
    )

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudentListItem(
    student: StudentDTO,
    onSelect: (StudentDTO) -> Unit,
    onDelete: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onDismiss = { showDeleteDialog = false },
            onDelete = {
                onDelete()
                showDeleteDialog = false
            },
            student = student
        )
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect(student) }
            .padding(8.dp),
        elevation = if (isSelected) CardDefaults.outlinedCardElevation() else CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .studentListItemBackground(isSelected)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${student.firstName} ${student.lastName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = student.email,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.8f
                        )
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Chip(
                        onClick = { },
                        colors = ChipDefaults.chipColors(
                            backgroundColor = StatusColors[student.status] ?: Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text(student.status.value)
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete student",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    student: StudentDTO
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Delete") },
        text = { Text("Are you sure you want to delete ${student.firstName} ${student.lastName}?") },
        confirmButton = {
            TextButton(
                onClick = onDelete,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Preview
@Composable
fun StudentListItemPreview() {
    CenteredDarkPreview {
        StudentListItem(
            student = StudentDTO.demoStudent,
            onSelect = {},
            onDelete = {},
            isSelected = false
        )
    }
}
