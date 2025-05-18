package com.ahmed.ui.enrollment.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.CourseDTO
import com.ahmed.model.EnrollmentDTO
import com.ahmed.model.EnrollmentStatus
import com.ahmed.model.StudentDTO
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.DatePickerField
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.math.BigDecimal
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentForm(
    selectedEnrollment: EnrollmentDTO?,
    onSave: (enrollmentDto: EnrollmentDTO) -> Unit,
    onCancel: () -> Unit,
    courseDto: CourseDTO,
    availableStudents: List<StudentDTO>,
    modifier: Modifier = Modifier
) {
    var selectedStudent by remember(selectedEnrollment) {
        mutableStateOf(availableStudents.find { it.id == selectedEnrollment?.student?.id })
    }
    var isStudentDropdownExpanded by remember { mutableStateOf(false) }

    var enrollmentDate by remember(selectedEnrollment) {
        mutableStateOf(selectedEnrollment?.enrollmentDate ?: Clock.System.todayIn(TimeZone.currentSystemDefault()))
    }
    var grade by remember(selectedEnrollment) {
        mutableStateOf(selectedEnrollment?.grade?.toString() ?: "")
    }
    var status by remember(selectedEnrollment) {
        mutableStateOf(selectedEnrollment?.status ?: EnrollmentStatus.ENROLLED)
    }
    var isStatusExpanded by remember { mutableStateOf(false) }

    // Validation states
    var enrollmentDateError by remember { mutableStateOf<String?>(null) }
    var gradeError by remember { mutableStateOf<String?>(null) }

    // Validation functions
    fun validateEnrollmentDate(date: LocalDate): String? {
        val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return when {
            date > currentDate -> "Enrollment date cannot be in the future"
            else -> null
        }
    }

    fun validateGrade(grade: String): String? {
        if (grade.isBlank()) return null
        return when {
            grade.toBigDecimalOrNull() == null -> "Grade must be a number"
            grade.toBigDecimal() < BigDecimal("0.00") -> "Grade must be greater than or equal to 0.00"
            grade.toBigDecimal() > BigDecimal("4.00") -> "Grade must be less than or equal to 4.00"
            else -> null
        }
    }

    fun isFormValid(): Boolean {
        return (selectedEnrollment != null || selectedStudent != null) &&
                enrollmentDateError == null &&
                (grade.isEmpty() || gradeError == null)
    }

    // Initial validation
    LaunchedEffect(selectedEnrollment) {
        enrollmentDateError = validateEnrollmentDate(enrollmentDate)
        gradeError = validateGrade(grade)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (selectedEnrollment != null) "Edit Enrollment" else "Create Enrollment",
            style = MaterialTheme.typography.titleMedium
        )

        // Form Fields
        Column(
            modifier = modifier.fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedEnrollment == null) {
                ExposedDropdownMenuBox(
                    expanded = isStudentDropdownExpanded,
                    onExpandedChange = { isStudentDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedStudent?.let { "${it.firstName} ${it.lastName}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Student") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStudentDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryEditable),
                        supportingText = if (availableStudents.isEmpty()) {
                            { Text("No unenrolled students available") }
                        } else null
                    )
                    ExposedDropdownMenu(
                        expanded = isStudentDropdownExpanded && availableStudents.isNotEmpty(),
                        onDismissRequest = { isStudentDropdownExpanded = false }
                    ) {
                        availableStudents.forEach { student ->
                            DropdownMenuItem(
                                text = { Text("${student.firstName} ${student.lastName}") },
                                onClick = {
                                    selectedStudent = student
                                    isStudentDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            } else {
                // Display selected student info when editing
                OutlinedTextField(
                    value = selectedStudent?.let { "${it.firstName} ${it.lastName}" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Student") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
            }

            DatePickerField(
                value = enrollmentDate,
                onValueChange = {
                    enrollmentDate = it
                    enrollmentDateError = validateEnrollmentDate(it)
                },
                label = "Enrollment Date",
                isError = enrollmentDateError != null,
                errorText = enrollmentDateError,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = grade,
                onValueChange = {
                    grade = it
                    gradeError = validateGrade(it)
                },
                label = { Text("Grade (Optional)") },
                isError = gradeError != null,
                supportingText = { gradeError?.let { Text(it) } },
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
                    EnrollmentStatus.entries.forEach { enrollmentStatus ->
                        DropdownMenuItem(
                            text = { Text(enrollmentStatus.value) },
                            onClick = {
                                status = enrollmentStatus
                                isStatusExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        EnrollmentDTO(
                            id = selectedEnrollment?.id,
                            student = if(selectedEnrollment != null) selectedEnrollment.student else selectedStudent!!,
                            course = courseDto,
                            enrollmentDate = enrollmentDate,
                            grade = grade.takeIf { it.isNotEmpty() }?.toBigDecimalOrNull(),
                            status = status
                        )
                    )
                    selectedStudent = null
                },
                enabled = isFormValid(),
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(if (selectedEnrollment != null) "Update" else "Create")
            }
        }
    }
}

@Preview
@Composable
fun EnrollmentFormPreview() {
    CenteredDarkPreview {
        EnrollmentForm(
            selectedEnrollment = EnrollmentDTO.demoEnrollment,
            onSave = { },
            onCancel = { },
            courseDto = CourseDTO.demoCourse,
            availableStudents = listOf(
                StudentDTO.demoStudent
            )
        )
    }
}
