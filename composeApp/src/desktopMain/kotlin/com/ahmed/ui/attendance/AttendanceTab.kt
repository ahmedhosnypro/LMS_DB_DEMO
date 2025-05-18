package com.ahmed.ui.attendance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.AttendanceDTO
import com.ahmed.ui.attendance.form.AttendanceForm
import com.ahmed.ui.attendance.list.AttendanceList
import com.ahmed.ui.components.SplitterHandle
import com.ahmed.ui.components.SplitterVisiblePart
import com.ahmed.viewModel.AttendanceViewModel
import com.ahmed.viewModel.ViewModelProvider
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun AttendanceTab(
    snackbarHostState: SnackbarHostState,
    splitterState: SplitPaneState,
    viewModel: AttendanceViewModel = ViewModelProvider.attendanceViewModel
) {
    var selectedAttendance by remember { mutableStateOf<AttendanceDTO?>(null) }

    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
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
            AttendanceList(
                attendanceRecords = attendanceRecords,
                selectedAttendance = selectedAttendance,
                onAttendanceSelect = { attendance -> selectedAttendance = attendance },
                onSearch = viewModel::searchAttendance,
                onRefresh = viewModel::loadAttendance,
                onDelete = { id ->
                    viewModel.deleteAttendance(id)
                    if (selectedAttendance?.id == id) {
                        selectedAttendance = null
                    }
                },
                isLoading = isLoading,
                modifier = Modifier.fillMaxSize()
            )
        }
        second(300.dp) {
            AttendanceForm(
                selectedAttendance = selectedAttendance,
                onSave = { attendanceDto ->
                    selectedAttendance?.let {
                        viewModel.updateAttendance(attendanceDto)
                    } ?: viewModel.createAttendance(attendanceDto)

                    selectedAttendance = null
                },
                onCancel = { selectedAttendance = null },
                modifier = Modifier.fillMaxSize()
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
