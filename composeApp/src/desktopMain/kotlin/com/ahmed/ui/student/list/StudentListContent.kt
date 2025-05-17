package com.ahmed.ui.student.list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import com.ahmed.model.StudentDTO
import com.ahmed.ui.CenteredDarkPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StudentListContent(
    students: List<StudentDTO>,
    selectedStudent: StudentDTO?,
    onStudentSelect: (StudentDTO) -> Unit,
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
            students.isEmpty() -> EmptyListPlaceholder()
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(students) { student ->
                    StudentListItem(
                        student = student,
                        onSelect = onStudentSelect,
                        onDelete = { student.id?.let { onDelete(it) } },
                        isSelected = student == selectedStudent,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyListPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("No students found")
    }
}

@Preview
@Composable
fun StudentListContentPreview() {
    CenteredDarkPreview {
        StudentListContent(
            students = listOf(StudentDTO.demoStudent, StudentDTO.demoStudent.copy(id = 2)),
            selectedStudent = StudentDTO.demoStudent,
            onStudentSelect = {},
            onDelete = {},
            isLoading = false
        )
    }
}

@Preview
@Composable
fun StudentListContentLoadingPreview() {
    CenteredDarkPreview {
        StudentListContent(
            students = emptyList(),
            selectedStudent = null,
            onStudentSelect = {},
            onDelete = {},
            isLoading = true
        )
    }
}

@Preview
@Composable
fun StudentListContentEmptyPreview() {
    CenteredDarkPreview {
        StudentListContent(
            students = emptyList(),
            selectedStudent = null,
            onStudentSelect = {},
            onDelete = {},
            isLoading = false
        )
    }
}
