package com.ahmed.ui.student.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.StudentDTO
import com.ahmed.model.StudentStatus
import com.ahmed.ui.CenteredDarkPreview
import com.ahmed.ui.components.DatePickerField
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.ui.tooling.preview.Preview

val timeZone = TimeZone.currentSystemDefault()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentForm(
    selectedStudent: StudentDTO?,
    onSave: (studentDto: StudentDTO) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var firstName by remember(selectedStudent) { mutableStateOf(selectedStudent?.firstName ?: "") }
    var lastName by remember(selectedStudent) { mutableStateOf(selectedStudent?.lastName ?: "") }
    var email by remember(selectedStudent) { mutableStateOf(selectedStudent?.email ?: "") }
    var dateOfBirth by remember(selectedStudent) {
        mutableStateOf(selectedStudent?.dateOfBirth ?: LocalDate(2000, 1, 1))
    }
    var enrollmentDate by remember(selectedStudent) {
        mutableStateOf(selectedStudent?.enrollmentDate ?: Clock.System.todayIn(timeZone))
    }
    var status by remember(selectedStudent) { mutableStateOf(selectedStudent?.status ?: StudentStatus.ACTIVE) }
    var isStatusExpanded by remember { mutableStateOf(false) }

    // Validation states
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var dateOfBirthError by remember { mutableStateOf<String?>(null) }
    var enrollmentDateError by remember { mutableStateOf<String?>(null) }

    // Validation functions
    fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            !name.matches("[A-Za-z-]+".toRegex()) -> "Name can only contain letters and hyphens"
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()) -> "Invalid email format"
            else -> null
        }
    }

    fun validateDateOfBirth(date: LocalDate): String? {
        val currentDate = Clock.System.todayIn(timeZone)
        val age = currentDate.year - date.year
        return when {
            age < 16 -> "Student must be at least 16 years old"
            age > 100 -> "Student cannot be over 100 years old"
            else -> null
        }
    }

    fun validateEnrollmentDate(date: LocalDate): String? {
        val currentDate = Clock.System.todayIn(timeZone)
        return when {
            date > currentDate -> "Enrollment date cannot be in the future"
            else -> null
        }
    }

    fun isFormValid(): Boolean {
        return firstNameError == null && lastNameError == null && 
               emailError == null && dateOfBirthError == null && 
               enrollmentDateError == null &&
               firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank()
    }

    // Initial validation
    LaunchedEffect(selectedStudent) {
        firstNameError = validateName(firstName)
        lastNameError = validateName(lastName)
        emailError = validateEmail(email)
        dateOfBirthError = validateDateOfBirth(dateOfBirth)
        enrollmentDateError = validateEnrollmentDate(enrollmentDate)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (selectedStudent != null) "Edit Student" else "Create Student",
            style = MaterialTheme.typography.titleMedium,
        )

        // StudentInputFields
        Column(
            modifier = modifier.fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { 
                    firstName = it
                    firstNameError = validateName(it)
                },
                label = { Text("First Name") },
                isError = firstNameError != null,
                supportingText = { firstNameError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { 
                    lastName = it
                    lastNameError = validateName(it)
                },
                label = { Text("Last Name") },
                isError = lastNameError != null,
                supportingText = { lastNameError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailError = validateEmail(it)
                },
                label = { Text("Email") },
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            DatePickerField(
                value = dateOfBirth,
                onValueChange = { 
                    dateOfBirth = it
                    dateOfBirthError = validateDateOfBirth(it)
                },
                label = "Date of Birth",
                isError = dateOfBirthError != null,
                errorText = dateOfBirthError,
                modifier = Modifier.fillMaxWidth()
            )

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
                    StudentStatus.entries.forEach { studentStatus ->
                        DropdownMenuItem(
                            text = { Text(studentStatus.value) },
                            onClick = {
                                status = studentStatus
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
                    onSave(StudentDTO(
                        id = selectedStudent?.id,
                        firstName,
                        lastName,
                        email,
                        dateOfBirth,
                        enrollmentDate,
                        status)
                    )
                },
                enabled = isFormValid(),
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(if (selectedStudent != null) "Update" else "Create")
            }
        }
    }
}

@Preview
@Composable
fun StudentFormPreview() {
    CenteredDarkPreview {
        StudentForm(
            selectedStudent = StudentDTO.demoStudent,
            onSave = { _ -> },
            onCancel = {},
        )
    }
}