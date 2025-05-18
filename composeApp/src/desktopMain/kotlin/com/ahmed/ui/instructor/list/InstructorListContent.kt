package com.ahmed.ui.instructor.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import com.ahmed.model.InstructorDTO
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.EmptyListPlaceholder
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun InstructorListContent(
    instructors: List<InstructorDTO>,
    selectedInstructor: InstructorDTO?,
    onInstructorSelect: (InstructorDTO) -> Unit,
    onDelete: (Int) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            instructors.isEmpty() -> EmptyListPlaceholder("No instructors found")
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(all = 16.dp)
            ) {
                items(instructors) { instructor ->
                    InstructorListItem(
                        instructor = instructor,
                        onSelect = onInstructorSelect,
                        onDelete = { instructor.id?.let { onDelete(it) } },
                        isSelected = instructor == selectedInstructor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun InstructorListContentPreview() {
    CenteredDarkPreview {
        InstructorListContent(
            instructors = listOf(InstructorDTO.demoInstructor, InstructorDTO.demoInstructor.copy(id = 2)),
            selectedInstructor = InstructorDTO.demoInstructor,
            onInstructorSelect = {},
            onDelete = {},
            isLoading = false
        )
    }
}

@Preview
@Composable
fun InstructorListContentLoadingPreview() {
    CenteredDarkPreview {
        InstructorListContent(
            instructors = emptyList(),
            selectedInstructor = null,
            onInstructorSelect = {},
            onDelete = {},
            isLoading = true
        )
    }
}

@Preview
@Composable
fun InstructorListContentEmptyPreview() {
    CenteredDarkPreview {
        InstructorListContent(
            instructors = emptyList(),
            selectedInstructor = null,
            onInstructorSelect = {},
            onDelete = {},
            isLoading = false
        )
    }
}
