package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.myapplication.composables.WorkoutItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    viewModel: LandingViewModel,
    onLogout: () -> Unit,
    onNavigateToAddWorkout: () -> Unit,
    onNavigateToViewProfile: () -> Unit,
    onNavigateToWorkoutDetail: (Int) -> Unit,
    userId: Int
) {
    var showMenu by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadWorkouts(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToAddWorkout) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                    }
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("View Profile") },
                            onClick = onNavigateToViewProfile
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = onLogout
                        )
                    }
                }
            )
        }
    ) {
        if (uiState.workouts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No entries was made.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(it)) {
                items(uiState.workouts) { workout ->
                    WorkoutItem(
                        workoutTypeImage = getWorkoutCategoryIcon(workout.category),
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(workout.dateTime)),
                        duration = "${workout.duration ?: 0} mins",
                        rating = workout.enjoymentRating,
                        onClick = { onNavigateToWorkoutDetail(workout.id) }
                    )
                }
            }
        }
    }
}

private fun getWorkoutCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Cardio" -> Icons.Default.DirectionsRun
        "Strength" -> Icons.Default.FitnessCenter
        "Flexibility & Mobility" -> Icons.Default.Spa
        "Mind-Body recovery" -> Icons.Default.SelfImprovement
        else -> Icons.Default.FitnessCenter
    }
}
