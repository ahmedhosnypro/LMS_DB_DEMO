package com.ahmed.viewModel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ahmed.model.CourseDTO

object ViewModelProvider {
    val ManageCourseTab_Course_KEY = object : CreationExtras.Key<CourseDTO?> {}
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            StudentViewModel()
        }
        initializer {
            CourseViewModel()
        }
        initializer {
            val courseDto = this[ManageCourseTab_Course_KEY]
            EnrollmentViewModel(courseDto)
        }
        initializer {
            AttendanceViewModel()
        }
        initializer{
            InstructorViewModel()
        }
    }

    val studentViewModel: StudentViewModel = Factory.create(StudentViewModel::class, MutableCreationExtras())
    val courseViewModel: CourseViewModel = Factory.create(CourseViewModel::class, MutableCreationExtras())

    val attendanceViewModel: AttendanceViewModel = Factory.create(AttendanceViewModel::class, MutableCreationExtras())
    val instructorViewModel: InstructorViewModel = Factory.create(InstructorViewModel::class, MutableCreationExtras())
}