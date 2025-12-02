package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.WorkoutsRepository

class ViewModelFactory(
    private val accountsRepository: AccountsRepository,
    private val workoutsRepository: WorkoutsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthenticationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthenticationViewModel(accountsRepository) as T
        }
        if (modelClass.isAssignableFrom(AddWorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddWorkoutViewModel(workoutsRepository) as T
        }
        if (modelClass.isAssignableFrom(LandingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LandingViewModel(workoutsRepository) as T
        }
        if (modelClass.isAssignableFrom(WorkoutDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutDetailViewModel(workoutsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}