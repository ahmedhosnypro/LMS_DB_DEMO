package com.ahmed.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.ahmed.model.StudentDTO
import com.ahmed.ui.modifier.cursorForHorizontalResize
import com.ahmed.ui.student.form.StudentForm
import com.ahmed.ui.student.list.StudentList
import com.ahmed.viewModel.StudentViewModel
import com.ahmed.viewModel.ViewModelProvider
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import java.awt.Cursor

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun StudentTab(
    snackbarHostState: SnackbarHostState,
    viewModel: StudentViewModel = ViewModelProvider.studentViewModel
) {
    val splitterState = rememberSplitPaneState()
    var selectedStudent by remember { mutableStateOf<StudentDTO?>(null) }

    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
        }
    }

    HorizontalSplitPane(
        splitPaneState = splitterState
    ) {
        first(700.dp) {
            StudentList(
                students = students,
                selectedStudent = selectedStudent,
                onStudentSelect = { student -> selectedStudent = student },
                onSearch = viewModel::searchStudents,
                onRefresh = viewModel::loadStudents,
                onDelete = { id ->
                    viewModel.deleteStudent(id)
                    if (selectedStudent?.id == id) {
                        selectedStudent = null
                    }
                },
                isLoading = isLoading,
                modifier = Modifier.fillMaxSize()
            )
        }
        second(300.dp) {
            StudentForm(
                selectedStudent = selectedStudent,
                onSave = { studentDto ->
                    selectedStudent?.let {
                        viewModel.updateStudent(studentDto)
                    } ?: viewModel.createStudent(studentDto)

                    selectedStudent = null
                },
                onCancel = { selectedStudent = null },
                modifier = Modifier.fillMaxSize()
            )
        }
        splitter {
            visiblePart {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
            handle {
                Box(
                    Modifier
                        .markAsHandle()
                        .cursorForHorizontalResize()
                        .background(SolidColor(Color.Gray), alpha = 0.50f)
                        .width(9.dp)
                        .fillMaxHeight()
                )
            }
        }
    }
}

