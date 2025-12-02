package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.WorkoutsRepository
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getDatabase(application)
        val accountsRepository = AccountsRepository(database.accountDao())
        val workoutsRepository = WorkoutsRepository(database.workoutDao())
        val factory = ViewModelFactory(accountsRepository, workoutsRepository)
        val authViewModel = ViewModelProvider(this, factory)[AuthenticationViewModel::class.java]
        val addWorkoutViewModel = ViewModelProvider(this, factory)[AddWorkoutViewModel::class.java]
        val landingViewModel = ViewModelProvider(this, factory)[LandingViewModel::class.java]
        val workoutDetailViewModel = ViewModelProvider(this, factory)[WorkoutDetailViewModel::class.java]

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    authViewModel.navigationEvent.onEach { event ->
                        when (event) {
                            is NavigationEvent.NavigateToLanding -> navController.navigate("landing/${event.userId}")
                            is NavigationEvent.NavigateToLogin -> navController.navigate("login")
                        }
                    }.launchIn(this)
                }

                Scaffold {
                    NavHost(navController = navController, startDestination = "login", modifier = Modifier.padding(it)) {
                        composable("login") {
                            val uiState by authViewModel.loginUiState.collectAsState()
                            LoginScreen(
                                uiState = uiState,
                                onUsernameChange = authViewModel::onLoginUsernameChange,
                                onPasswordChange = authViewModel::onLoginPasswordChange,
                                onLoginClick = authViewModel::login,
                                onNavigateToRegistration = { navController.navigate("registration") }
                            )
                        }
                        composable("registration") {
                            val uiState by authViewModel.registrationUiState.collectAsState()
                            RegistrationScreen(
                                uiState = uiState,
                                onUsernameChange = authViewModel::onRegistrationUsernameChange,
                                onPasswordChange = authViewModel::onRegistrationPasswordChange,
                                onConfirmPasswordChange = authViewModel::onConfirmPasswordChange,
                                onEmailChange = authViewModel::onEmailChange,
                                onGenderChange = authViewModel::onGenderChange,
                                onMobileNumberChange = authViewModel::onMobileNumberChange,
                                onReceiveUpdatesChange = authViewModel::onReceiveUpdatesChange,
                                onYearOfBirthChange = authViewModel::onYearOfBirthChange,
                                onRegisterClick = authViewModel::register,
                                onCancelClick = authViewModel::cancel,
                            )
                        }
                        composable("landing/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: -1
                            LandingScreen(
                                viewModel = landingViewModel,
                                onLogout = { navController.navigate("login") { popUpTo("login") { inclusive = true } } },
                                onNavigateToAddWorkout = { navController.navigate("add_workout/$userId") },
                                onNavigateToViewProfile = { navController.navigate("view_profile") },
                                onNavigateToWorkoutDetail = { workoutId -> navController.navigate("workout_detail/$workoutId") },
                                userId = userId
                            )
                        }
                        composable("add_workout/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: -1
                            AddWorkoutScreen(
                                viewModel = addWorkoutViewModel,
                                onNavigateUp = { navController.navigateUp() },
                                userId = userId
                            )
                        }
                        composable("view_profile") {
                            ViewProfileScreen()
                        }
                        composable("workout_detail/{workoutId}") { backStackEntry ->
                            val workoutId = backStackEntry.arguments?.getString("workoutId")?.toIntOrNull() ?: -1
                            WorkoutDetailScreen(
                                viewModel = workoutDetailViewModel,
                                workoutId = workoutId,
                                onNavigateUp = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}
