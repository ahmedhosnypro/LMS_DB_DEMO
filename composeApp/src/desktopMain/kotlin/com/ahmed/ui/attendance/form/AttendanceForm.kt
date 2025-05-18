package com.ahmed.ui.attendance.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.AttendanceDTO
import com.ahmed.model.AttendanceStatus
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.DatePickerField
import kotlinx.datetime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

val timeZone = TimeZone.currentSystemDefault()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceForm(
    selectedAttendance: AttendanceDTO?,
    onSave: (AttendanceDTO) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enrollmentId by remember(selectedAttendance) { mutableStateOf(selectedAttendance?.enrollmentId?.toString() ?: "") }
    var date by remember(selectedAttendance) { mutableStateOf(selectedAttendance?.date ?: Clock.System.todayIn(timeZone)) }
    var status by remember(selectedAttendance) { mutableStateOf(selectedAttendance?.status ?: AttendanceStatus.PRESENT) }
    var isStatusExpanded by remember { mutableStateOf(false) }

    // Validation states
    var enrollmentIdError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    // Validation functions
    fun validateEnrollmentId(id: String): String? {
        return when {
            id.isBlank() -> "Enrollment ID is required"
            id.toIntOrNull() == null -> "Enrollment ID must be a number"
            else -> null
        }
    }

    fun validateDate(attendanceDate: LocalDate): String? {
        val currentDate = Clock.System.todayIn(timeZone)
        return when {
            attendanceDate > currentDate -> "Attendance date cannot be in the future"
            else -> null
        }
    }

    fun isFormValid(): Boolean {
        return enrollmentIdError == null && dateError == null && 
               enrollmentId.isNotBlank()
    }

    // Initial validation
    LaunchedEffect(selectedAttendance) {
        enrollmentIdError = validateEnrollmentId(enrollmentId)
        dateError = validateDate(date)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (selectedAttendance != null) "Edit Attendance Record" else "Create Attendance Record",
            style = MaterialTheme.typography.titleMedium,
        )

        // Attendance Input Fields
        Column(
            modifier = modifier.fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = enrollmentId,
                onValueChange = { 
                    enrollmentId = it
                    enrollmentIdError = validateEnrollmentId(it)
                },
                label = { Text("Enrollment ID") },
                isError = enrollmentIdError != null,
                supportingText = { enrollmentIdError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            DatePickerField(
                value = date,
                onValueChange = { 
                    date = it
                    dateError = validateDate(it)
                },
                label = "Attendance Date",
                isError = dateError != null,
                errorText = dateError,
                modifier = Modifier.fillMaxWidth()
            )

            // Status Dropdown
            ExposedDropdownMenuBox(
                expanded = isStatusExpanded,
                onExpandedChange = { isStatusExpanded = it }
            ) {
                OutlinedTextField(
                    value = status.value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable)
                )
                ExposedDropdownMenu(
                    expanded = isStatusExpanded,
                    onDismissRequest = { isStatusExpanded = false }
                ) {
                    AttendanceStatus.entries.forEach { attendanceStatus ->
                        DropdownMenuItem(
                            text = { Text(attendanceStatus.value) },
                            onClick = {
                                status = attendanceStatus
                                isStatusExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = modifier.fillMaxWidth().weight(.1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    onSave(
                        AttendanceDTO(
                            id = selectedAttendance?.id,
                            enrollmentId = enrollmentId.toIntOrNull() ?: 0,
                            date = date,
                            status = status
                        )
                    )
                },
                enabled = isFormValid(),
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(if (selectedAttendance != null) "Update" else "Create")
            }
        }
    }
}

@Preview
@Composable
fun AttendanceFormPreview() {
    val demoAttendance = AttendanceDTO(
        id = 1,
        enrollmentId = 1,
        date = Clock.System.todayIn(timeZone),
        status = AttendanceStatus.PRESENT
    )
    CenteredDarkPreview {
        AttendanceForm(
            selectedAttendance = demoAttendance,
            onSave = { _ -> },
            onCancel = {},
        )
    }
}
