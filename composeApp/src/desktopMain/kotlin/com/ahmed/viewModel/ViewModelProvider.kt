package com.ahmed.viewModel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

object ViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            StudentViewModel()
        }
    }

    val studentViewModel: StudentViewModel = Factory.create(StudentViewModel::class, MutableCreationExtras())
}