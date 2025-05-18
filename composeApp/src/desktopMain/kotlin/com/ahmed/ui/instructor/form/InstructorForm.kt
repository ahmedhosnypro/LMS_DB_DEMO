package com.ahmed.ui.instructor.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.InstructorDTO
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
fun InstructorForm(
    selectedInstructor: InstructorDTO?,
    onSave: (InstructorDTO) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var firstName by remember(selectedInstructor) { mutableStateOf(selectedInstructor?.firstName ?: "") }
    var lastName by remember(selectedInstructor) { mutableStateOf(selectedInstructor?.lastName ?: "") }
    var email by remember(selectedInstructor) { mutableStateOf(selectedInstructor?.email ?: "") }
    var department by remember(selectedInstructor) { mutableStateOf(selectedInstructor?.department ?: "") }
    var hireDate by remember(selectedInstructor) { 
        mutableStateOf(selectedInstructor?.hireDate ?: Clock.System.todayIn(timeZone))
    }

    // Validation states
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var departmentError by remember { mutableStateOf<String?>(null) }
    var hireDateError by remember { mutableStateOf<String?>(null) }

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

    fun validateDepartment(department: String): String? {
        return when {
            department.isBlank() -> "Department is required"
            !department.matches("[A-Za-z\\s&-]+".toRegex()) -> "Department can only contain letters, spaces, hyphens, and &"
            else -> null
        }
    }

    fun validateHireDate(date: LocalDate): String? {
        val currentDate = Clock.System.todayIn(timeZone)
        return when {
            date > currentDate -> "Hire date cannot be in the future"
            else -> null
        }
    }

    fun isFormValid(): Boolean {
        return firstNameError == null && lastNameError == null && 
               emailError == null && departmentError == null && 
               hireDateError == null &&
               firstName.isNotBlank() && lastName.isNotBlank() && 
               email.isNotBlank() && department.isNotBlank()
    }

    // Initial validation
    LaunchedEffect(selectedInstructor) {
        firstNameError = validateName(firstName)
        lastNameError = validateName(lastName)
        emailError = validateEmail(email)
        departmentError = validateDepartment(department)
        hireDateError = validateHireDate(hireDate)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (selectedInstructor != null) "Edit Instructor" else "Create Instructor",
            style = MaterialTheme.typography.titleMedium,
        )

        // InstructorInputFields
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

            OutlinedTextField(
                value = department,
                onValueChange = { 
                    department = it
                    departmentError = validateDepartment(it)
                },
                label = { Text("Department") },
                isError = departmentError != null,
                supportingText = { departmentError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            DatePickerField(
                value = hireDate,
                onValueChange = { 
                    hireDate = it
                    hireDateError = validateHireDate(it)
                },
                label = "Hire Date",
                isError = hireDateError != null,
                errorText = hireDateError,
                modifier = Modifier.fillMaxWidth()
            )
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
                    onSave(InstructorDTO(
                        id = selectedInstructor?.id,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        department = department,
                        hireDate = hireDate
                    ))
                },
                enabled = isFormValid(),
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(if (selectedInstructor != null) "Update" else "Create")
            }
        }
    }
}

@Preview
@Composable
fun InstructorFormPreview() {
    CenteredDarkPreview {
        InstructorForm(
            selectedInstructor = null,
            onSave = { _ -> },
            onCancel = {},
        )
    }
}
