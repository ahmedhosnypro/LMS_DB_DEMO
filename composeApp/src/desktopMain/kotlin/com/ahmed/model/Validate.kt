package com.ahmed.model

fun validatePersonFields(
    firstName: String,
    lastName: String,
    email: String
): MutableList<String> {
    val errors = mutableListOf<String>()

    if (!firstName.matches(Regex("[A-Za-z-]+"))) {
        errors.add("First name must contain only letters and hyphens")
    }
    if (firstName.length < 2) {
        errors.add("First name must be at least 2 characters long")
    }

    if (!lastName.matches(Regex("[A-Za-z-]+"))) {
        errors.add("Last name must contain only letters and hyphens")
    }
    if (lastName.length < 2) {
        errors.add("Last name must be at least 2 characters long")
    }

    if (!email.matches(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))) {
        errors.add("Invalid email format")
    }
    if (email.length < 5) {
        errors.add("Email must be at least 5 characters long")
    }

    return errors
}