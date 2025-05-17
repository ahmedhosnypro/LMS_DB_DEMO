package com.ahmed.util

object Email {
    private val emailRegex = Regex("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
    private val validEmailCharRegex = Regex("^[a-zA-Z0-9._@-]$")

    fun isValidEmailChar(char: Char): Boolean {
        return validEmailCharRegex.matches(char.toString())
    }

    fun isValidEmail(email: String): Boolean {
        return emailRegex.matches(email)
    }
}