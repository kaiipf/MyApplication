package com.example.myapplication

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.composables.DatePickerField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(
    viewModel: AddWorkoutViewModel,
    onNavigateUp: () -> Unit,
    userId: Int
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isWorkoutSaved) {
        if (uiState.isWorkoutSaved) {
            onNavigateUp()
        }
    }

    BackHandler {
        showCancelDialog = true
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel?") },
            text = { Text("Are you sure you want to cancel? Any unsaved changes will be lost.") },
            confirmButton = {
                TextButton(onClick = { onNavigateUp() }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New workout") },
                navigationIcon = {
                    IconButton(onClick = { showCancelDialog = true }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.FitnessCenter, contentDescription = "Workout icon", modifier = Modifier.align(Alignment.CenterHorizontally))
            TextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter work out name") }
            )

            var expanded by remember { mutableStateOf(false) }
            val categories = listOf("Cardio", "Strength", "Flexibility & Mobility", "Mind-Body recovery")

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                TextField(
                    value = uiState.category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Work out category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                viewModel.onCategoryChange(category)
                                expanded = false
                            }
                        )
                    }
                }
            }

            TextField(
                value = uiState.duration,
                onValueChange = viewModel::onDurationChange,
                label = { Text("Duration (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            DatePickerField(
                label = "Date",
                value = uiState.dateTime,
                onValueChange = viewModel::onDateTimeChange
            )

            Text("Enjoyment Rating: ${uiState.enjoymentRating}")
            val enjoymentLevels = listOf("Energizing", "Calming", "Stress-relieving", "Motivating", "Exhausting", "Frustrating")
            Slider(
                value = enjoymentLevels.indexOf(uiState.enjoymentRating).toFloat(),
                onValueChange = { viewModel.onEnjoymentRatingChange(enjoymentLevels[it.toInt()]) },
                valueRange = 0f..(enjoymentLevels.size - 1).toFloat(),
                steps = enjoymentLevels.size - 2
            )

            TextField(
                value = uiState.comments,
                onValueChange = viewModel::onCommentsChange,
                label = { Text("Comments") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter comments") }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Add workout", style = MaterialTheme.typography.labelLarge)
            Button(
                onClick = { viewModel.saveWorkout(userId) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text("Submit")
            }
        }
    }
}
