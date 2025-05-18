package com.ahmed.ui.courses.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.model.CourseDTO
import com.ahmed.model.CourseStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseForm(
    selectedCourse: CourseDTO?,
    onSave: (CourseDTO) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var courseCode by remember(selectedCourse) { mutableStateOf(selectedCourse?.courseCode ?: "") }
    var title by remember(selectedCourse) { mutableStateOf(selectedCourse?.title ?: "") }
    var description by remember(selectedCourse) { mutableStateOf(selectedCourse?.description ?: "") }
    var credits by remember(selectedCourse) { mutableStateOf(selectedCourse?.credits?.toString() ?: "3") }
    var maxStudents by remember(selectedCourse) { mutableStateOf(selectedCourse?.maxStudents?.toString() ?: "") }
    var status by remember(selectedCourse) { mutableStateOf(selectedCourse?.status ?: CourseStatus.ACTIVE.value) }
    var isStatusExpanded by remember { mutableStateOf(false) }

    // Validation states
    var courseCodeError by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf<String?>(null) }
    var creditsError by remember { mutableStateOf<String?>(null) }
    var maxStudentsError by remember { mutableStateOf<String?>(null) }

    // Validation functions
    fun validateCourseCode(code: String): String? {
        return when {
            code.isBlank() -> "Course code is required"
            !code.matches("[A-Z]{2,4}[0-9]{3,4}".toRegex()) -> 
                "Course code must be 2-4 capital letters followed by 3-4 numbers (e.g., CS101)"
            else -> null
        }
    }

    fun validateTitle(value: String): String? {
        return when {
            value.isBlank() -> "Title is required"
            value.length < 4 -> "Title must be at least 4 characters long"
            else -> null
        }
    }

    fun validateCredits(value: String): String? {
        val creditsInt = value.toIntOrNull()
        return when {
            value.isBlank() -> "Credits are required"
            creditsInt == null -> "Credits must be a number"
            creditsInt !in 1..6 -> "Credits must be between 1 and 6"
            else -> null
        }
    }

    fun validateMaxStudents(value: String): String? {
        if (value.isBlank()) return null // Optional field
        val maxStudentsInt = value.toIntOrNull()
        return when {
            maxStudentsInt == null -> "Maximum students must be a number"
            maxStudentsInt !in 1..200 -> "Maximum students must be between 1 and 200"
            else -> null
        }
    }

    fun isFormValid(): Boolean {
        return courseCodeError == null && titleError == null && 
               creditsError == null && maxStudentsError == null &&
               courseCode.isNotBlank() && title.isNotBlank() && credits.isNotBlank()
    }

    // Initial validation
    LaunchedEffect(selectedCourse) {
        courseCodeError = validateCourseCode(courseCode)
        titleError = validateTitle(title)
        creditsError = validateCredits(credits)
        maxStudentsError = validateMaxStudents(maxStudents)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (selectedCourse != null) "Edit Course" else "Add New Course",
            style = MaterialTheme.typography.titleMedium
        )

        // CourseInputFields
        Column(
            modifier = modifier.fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = courseCode,
                onValueChange = { 
                    courseCode = it
                    courseCodeError = validateCourseCode(it)
                },
                label = { Text("Course Code") },
                isError = courseCodeError != null,
                supportingText = { courseCodeError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = title,
                onValueChange = { 
                    title = it
                    titleError = validateTitle(it)
                },
                label = { Text("Title") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = credits,
                onValueChange = { 
                    credits = it
                    creditsError = validateCredits(it)
                },
                label = { Text("Credits") },
                isError = creditsError != null,
                supportingText = { creditsError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = maxStudents,
                onValueChange = { 
                    maxStudents = it
                    maxStudentsError = validateMaxStudents(it)
                },
                label = { Text("Maximum Students (Optional)") },
                isError = maxStudentsError != null,
                supportingText = { maxStudentsError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = isStatusExpanded,
                onExpandedChange = { isStatusExpanded = it }
            ) {
                OutlinedTextField(
                    value = status,
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
                    CourseStatus.entries.forEach { courseStatus ->
                        DropdownMenuItem(
                            text = { Text(courseStatus.value) },
                            onClick = {
                                status = courseStatus.value
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
                    val creditsInt = credits.toIntOrNull() ?: 0
                    val maxStudentsInt = maxStudents.takeIf { it.isNotBlank() }?.toIntOrNull()
                    
                    val courseDTO = CourseDTO(
                        id = selectedCourse?.id ?: 0,
                        courseCode = courseCode,
                        title = title,
                        description = description.takeIf { it.isNotBlank() },
                        credits = creditsInt,
                        instructorId = selectedCourse?.instructorId,
                        maxStudents = maxStudentsInt,
                        status = status
                    )
                    
                    onSave(courseDTO)
                },
                enabled = isFormValid(),
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(if (selectedCourse != null) "Update" else "Create")
            }
        }
    }
}
