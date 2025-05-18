package com.ahmed.ui.enrollment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.CourseDTO
import com.ahmed.model.EnrollmentDTO
import com.ahmed.ui.components.SplitterHandle
import com.ahmed.ui.components.SplitterVisiblePart
import com.ahmed.ui.enrollment.form.EnrollmentForm
import com.ahmed.ui.enrollment.list.EnrollmentList
import com.ahmed.util.Logger
import com.ahmed.viewModel.EnrollmentViewModel
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun EnrollmentTab(
    snackbarHostState: SnackbarHostState,
    viewModel: EnrollmentViewModel,
    courseDto: CourseDTO?,
) {
    val splitterState = rememberSplitPaneState()
    var selectedEnrollment by remember { mutableStateOf<EnrollmentDTO?>(null) }
    val unenrolledStudents by viewModel.unenrolledStudents.collectAsState()

    val enrollments by viewModel.enrollments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Load unenrolled students when the tab is first opened
    LaunchedEffect(courseDto) {
        viewModel.loadUnenrolledStudents()
    }
    LaunchedEffect(unenrolledStudents) {
        Logger.info(unenrolledStudents.toString())
    }

    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
        }
    }

    if (courseDto == null) {
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(
                message = "Course not found",
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
        }
        return
    }

    HorizontalSplitPane(
        splitPaneState = splitterState
    ) {
        first(700.dp) {
            EnrollmentList(
                enrollments = enrollments,
                selectedEnrollment = selectedEnrollment,
                onEnrollmentSelect = { enrollment -> selectedEnrollment = enrollment },
                onSearch = viewModel::searchByStudentName,
                onRefresh = viewModel::loadEnrollments,
                onDelete = { id ->
                    viewModel.deleteEnrollment(id)
                    if (selectedEnrollment?.id == id) {
                        selectedEnrollment = null
                    }
                },
                isLoading = isLoading,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
        second(300.dp) {
            EnrollmentForm(
                selectedEnrollment = selectedEnrollment,
                onSave = { enrollmentDto ->
                    selectedEnrollment?.let {
                        viewModel.updateEnrollment(enrollmentDto)
                    } ?: viewModel.createEnrollment(enrollmentDto)
                    selectedEnrollment = null
                },
                onCancel = { selectedEnrollment = null },
                courseDto = courseDto,
                availableStudents = unenrolledStudents,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
        splitter {
            visiblePart {
                SplitterVisiblePart()
            }
            handle {
                SplitterHandle(this)
            }
        }
    }
}
