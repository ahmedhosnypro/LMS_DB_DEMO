package com.ahmed.ui.student.list

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.StudentDTO
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.ListHeader
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StudentList(
    students: List<StudentDTO>,
    selectedStudent: StudentDTO?,
    onStudentSelect: (StudentDTO?) -> Unit,
    onSearch: (String) -> Unit,
    onRefresh: () -> Unit,
    onDelete: (Int) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ListHeader(
            onSearch = onSearch,
            onRefresh = onRefresh,
            onAdd = {onStudentSelect(null)},
            searchPlaceHolder = "Search by student name or email...",
            refreshTooltipText = "Refresh student list",
            addTooltipText = "Add new student",
            modifier = Modifier.requiredHeightIn(max = 128.dp)
        )
        StudentListContent(
            students = students,
            selectedStudent = selectedStudent,
            onStudentSelect = onStudentSelect,
            onDelete = onDelete,
            isLoading = isLoading,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun StudentListPreview() {
    CenteredDarkPreview {
        StudentList(
            students = listOf(StudentDTO.demoStudent, StudentDTO.demoStudent.copy(id = 2)),
            selectedStudent = StudentDTO.demoStudent,
            onStudentSelect = {},
            onSearch = {},
            onRefresh = {},
            onDelete = {},
            isLoading = false
        )
    }
}

@Preview
@Composable
fun StudentListEmptyPreview() {
    CenteredDarkPreview {
        StudentList(
            students = emptyList(),
            selectedStudent = null,
            onStudentSelect = {},
            onSearch = {},
            onRefresh = {},
            onDelete = {},
            isLoading = false
        )
    }
}
