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

data class LandingUiState(
    val workouts: List<Workout> = emptyList()
)

class LandingViewModel(private val workoutsRepository: WorkoutsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LandingUiState())
    val uiState: StateFlow<LandingUiState> = _uiState.asStateFlow()

    fun loadWorkouts(userId: Int) {
        viewModelScope.launch {
            workoutsRepository.getAllWorkouts(userId).collect { workouts ->
                _uiState.value = LandingUiState(workouts)
            }
        }
    }
}
