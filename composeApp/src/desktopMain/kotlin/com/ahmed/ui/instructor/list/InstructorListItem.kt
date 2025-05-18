package com.ahmed.ui.instructor.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ahmed.model.InstructorDTO
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.ConfirmDeleteDialog
import org.jetbrains.compose.ui.tooling.preview.Preview

private val SageGreen = Color(0xFF2E7D32)      // Dark green representing wisdom
private val RoyalBlue = Color(0xFF1565C0)     // Deep blue for academia
private val DeepPurple = Color(0xFF4527A0)    // Purple for dignity and knowledge
private val NavyBlue = Color(0xFF0D47A1)      // Navy for professionalism

fun Modifier.instructorListItemBackground(
    isSelected: Boolean
) : Modifier =
    background(
        brush = Brush.linearGradient(
            colors = if (isSelected) {
                listOf(SageGreen, RoyalBlue, DeepPurple, NavyBlue)
            } else {
                listOf(
                    SageGreen.copy(alpha = 0.7f),
                    RoyalBlue.copy(alpha = 0.7f),
                    DeepPurple.copy(alpha = 0.7f),
                    NavyBlue.copy(alpha = 0.7f)
                )
            }
        )
    )

@Composable
fun InstructorListItem(
    instructor: InstructorDTO,
    onSelect: (InstructorDTO) -> Unit,
    onDelete: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onDismiss = { showDeleteDialog = false },
            onDelete = onDelete,
            title = "Confirm Delete",
            text = "Are you sure you want to delete ${instructor.firstName} ${instructor.lastName}?"
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect(instructor) }
            .padding(8.dp),
        elevation = if (isSelected) CardDefaults.outlinedCardElevation() else CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .instructorListItemBackground(isSelected)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${instructor.firstName} ${instructor.lastName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = instructor.email,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.8f
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = instructor.department,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.7f
                        )
                    )
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
                        contentDescription = "Delete instructor",
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun InstructorListItemPreview() {
    CenteredDarkPreview {
        InstructorListItem(
            instructor = InstructorDTO.demoInstructor,
            onSelect = {},
            onDelete = {},
            isSelected = false
        )
    }
}
