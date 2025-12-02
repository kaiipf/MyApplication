package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Workout
import com.example.myapplication.data.WorkoutsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddWorkoutUiState(
    val name: String = "",
    val category: String = "Cardio",
    val duration: String = "",
    val dateTime: Long = System.currentTimeMillis(),
    val enjoymentRating: String = "Energizing",
    val comments: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isWorkoutSaved: Boolean = false
)

class AddWorkoutViewModel(private val workoutRepository: WorkoutsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddWorkoutUiState())
    val uiState: StateFlow<AddWorkoutUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onCategoryChange(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun onDurationChange(duration: String) {
        _uiState.update { it.copy(duration = duration) }
    }

    fun onDateTimeChange(dateTime: Long?) {
        _uiState.update { it.copy(dateTime = dateTime ?: System.currentTimeMillis()) }
    }

    fun onEnjoymentRatingChange(rating: String) {
        _uiState.update { it.copy(enjoymentRating = rating) }
    }

    fun onCommentsChange(comments: String) {
        _uiState.update { it.copy(comments = comments) }
    }

    fun saveWorkout(userId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val state = _uiState.value
                if (state.name.isBlank()) {
                    throw IllegalArgumentException("Workout name cannot be blank.")
                }
                workoutRepository.insertWorkout(
                    Workout(
                        userId = userId,
                        name = state.name,
                        category = state.category,
                        duration = state.duration.toIntOrNull(),
                        dateTime = state.dateTime,
                        enjoymentRating = state.enjoymentRating,
                        comments = state.comments
                    )
                )
                _uiState.update { it.copy(isLoading = false, isWorkoutSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
