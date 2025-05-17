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
import com.ahmed.ui.components.EmptyListPlaceholder
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
            students.isEmpty() -> EmptyListPlaceholder("No students found")
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(all = 16.dp)
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
