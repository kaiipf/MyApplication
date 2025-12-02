package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Workout
import com.example.myapplication.data.WorkoutsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class WorkoutDetailUiState(
    val workout: Workout? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isWorkoutDeleted: Boolean = false
)

class WorkoutDetailViewModel(private val workoutsRepository: WorkoutsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutDetailUiState())
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

    fun loadWorkout(workoutId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            workoutsRepository.getWorkoutById(workoutId).collect { workout ->
                _uiState.value = _uiState.value.copy(workout = workout, isLoading = false)
            }
        }
    }

    fun deleteWorkout(workoutId: Int) {
        viewModelScope.launch {
            workoutsRepository.deleteWorkoutById(workoutId)
            _uiState.value = _uiState.value.copy(isWorkoutDeleted = true)
        }
    }
}