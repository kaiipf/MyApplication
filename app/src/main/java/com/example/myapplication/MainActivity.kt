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
import androidx.lifecycle.viewmodel.compose.viewModel
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

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                // The ViewModel is now created once and scoped to the Activity,
                // ensuring it's shared across all composables in the NavHost.
                val authViewModel = ViewModelProvider(this, factory)[AuthenticationViewModel::class.java]

                // This LaunchedEffect is now at a higher level, so it's always active
                // and can receive navigation events from the shared authViewModel.
                LaunchedEffect(Unit) {
                    authViewModel.navigationEvent.onEach { event ->
                        when (event) {
                            is NavigationEvent.NavigateToLanding -> {
                                navController.navigate("landing/${event.userId}") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                            is NavigationEvent.NavigateToLogin -> {
                                navController.popBackStack()
                            }
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
                                // This now correctly calls the method on the shared ViewModel.
                                onCancelClick = authViewModel::cancel,
                            )
                        }
                        composable("landing/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: -1
                            val landingViewModel = ViewModelProvider(this@MainActivity, factory)[LandingViewModel::class.java]
                            LandingScreen(
                                viewModel = landingViewModel,
                                onLogout = { navController.navigate("login") { popUpTo("login") { inclusive = true } } },
                                onNavigateToAddWorkout = { navController.navigate("add_workout/$userId") },
                                onNavigateToViewProfile = { navController.navigate("view_profile/$userId") },
                                onNavigateToWorkoutDetail = { workoutId -> navController.navigate("workout_detail/$workoutId") },
                                userId = userId
                            )
                        }
                        composable("add_workout/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: -1
                            // By using the viewModel() composable, a new instance of AddWorkoutViewModel
                            // will be created and scoped to this navigation destination.
                            val addWorkoutViewModel: AddWorkoutViewModel = viewModel(factory = factory)
                            AddWorkoutScreen(
                                viewModel = addWorkoutViewModel,
                                onNavigateUp = { navController.navigateUp() },
                                userId = userId
                            )
                        }
                        composable("view_profile/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: -1
                            val viewProfileFactory = ViewModelFactory(accountsRepository, workoutsRepository, userId)
                            val viewProfileViewModel = ViewModelProvider(this@MainActivity, viewProfileFactory)[ViewProfileViewModel::class.java]
                            ViewProfileScreen(
                                viewModel = viewProfileViewModel,
                                onNavigateUp = { navController.navigateUp() },
                                onNavigateToEditProfile = { navController.navigate("edit_profile/$userId") }
                            )
                        }
                        composable("edit_profile/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: -1
                            val editProfileFactory = ViewModelFactory(accountsRepository, workoutsRepository, userId)
                            val editProfileViewModel = ViewModelProvider(this@MainActivity, editProfileFactory)[EditProfileViewModel::class.java]
                            EditProfileScreen(
                                viewModel = editProfileViewModel,
                                onNavigateUp = { navController.navigateUp() }
                            )
                        }
                        composable("workout_detail/{workoutId}") { backStackEntry ->
                            val workoutId = backStackEntry.arguments?.getString("workoutId")?.toIntOrNull() ?: -1
                            val workoutDetailViewModel = ViewModelProvider(this@MainActivity, factory)[WorkoutDetailViewModel::class.java]
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