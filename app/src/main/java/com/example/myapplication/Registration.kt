package com.example.myapplication

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.composables.DatePickerField
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class RegistrationFormState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val email: String = "",
    val gender: String = "",
    val mobileNumber: String = "",
    val receiveUpdates: Boolean = false,
    val yearOfBirth: Long? = null
)

data class RegistrationUiState(
    val formState: RegistrationFormState = RegistrationFormState(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistrationSuccess: Boolean = false
)

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccess: Boolean = false,
    val loggedInUserId: Int? = null
)

sealed class NavigationEvent {
    data class NavigateToLanding(val userId: Int) : NavigationEvent()
    object NavigateToLogin : NavigationEvent()
}

@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    uiState: RegistrationUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onMobileNumberChange: (String) -> Unit,
    onReceiveUpdatesChange: (Boolean) -> Unit,
    onYearOfBirthChange: (Long?) -> Unit,
    onRegisterClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = uiState.formState.username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter user name") }
        )
        TextField(
            value = uiState.formState.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            placeholder = { Text("Enter password") }
        )
        TextField(
            value = uiState.formState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            placeholder = { Text("Enter password") }
        )
        TextField(
            value = uiState.formState.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            placeholder = { Text("Enter email") }
        )

        val genderOptions = listOf("Male", "Female", "Non-Binary", "Prefer not to say")
        Column(Modifier.fillMaxWidth()) {
            Text("Gender")
            genderOptions.forEach { gender ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (gender == uiState.formState.gender),
                            onClick = { onGenderChange(gender) }
                        )
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (gender == uiState.formState.gender),
                        onClick = { onGenderChange(gender) }
                    )
                    Text(text = gender, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        TextField(
            value = uiState.formState.mobileNumber,
            onValueChange = onMobileNumberChange,
            label = { Text("Mobile Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text("Enter mobile number") }
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = uiState.formState.receiveUpdates,
                onCheckedChange = onReceiveUpdatesChange
            )
            Text(text = "To receive updates via email")
        }

        DatePickerField(
            label = "Select Year of birth",
            value = uiState.formState.yearOfBirth,
            onValueChange = onYearOfBirthChange
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = onCancelClick, enabled = !uiState.isLoading) {
                Text("Cancel")
            }
            Button(onClick = onRegisterClick, enabled = !uiState.isLoading) {
                Text("Submit Registration")
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        if (uiState.isRegistrationSuccess) {
            Text("Registration Successful!")
        }
    }
}

class AuthenticationViewModel(private val accountsRepository: AccountsRepository) : ViewModel() {

    private val _registrationUiState = MutableStateFlow(RegistrationUiState())
    val registrationUiState: StateFlow<RegistrationUiState> = _registrationUiState.asStateFlow()

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onRegistrationUsernameChange(username: String) {
        _registrationUiState.update { it.copy(formState = it.formState.copy(username = username), error = null) }
    }

    fun onRegistrationPasswordChange(password: String) {
        _registrationUiState.update { it.copy(formState = it.formState.copy(password = password), error = null) }
    }

    fun onConfirmPasswordChange(password: String) {
        _registrationUiState.update { it.copy(formState = it.formState.copy(confirmPassword = password), error = null) }
    }

    fun onEmailChange(email: String) {
        _registrationUiState.update { it.copy(formState = it.formState.copy(email = email), error = null) }
    }

    fun onGenderChange(gender: String) {
        _registrationUiState.update { it.copy(formState = it.formState.copy(gender = gender), error = null) }
    }

    fun onMobileNumberChange(mobileNumber: String) {
        _registrationUiState.update { it.copy(formState = it.formState.copy(mobileNumber = mobileNumber), error = null) }
    }

    fun onReceiveUpdatesChange(receiveUpdates: Boolean) {
        _registrationUiState.update { it.copy(formState = it.formState.copy(receiveUpdates = receiveUpdates), error = null) }
    }

    fun onYearOfBirthChange(yearOfBirth: Long?) {
        _registrationUiState.update { it.copy(formState = it.formState.copy(yearOfBirth = yearOfBirth), error = null) }
    }

    fun onLoginUsernameChange(username: String) {
        _loginUiState.update { it.copy(username = username, error = null) }
    }

    fun onLoginPasswordChange(password: String) {
        _loginUiState.update { it.copy(password = password, error = null) }
    }

    fun register() {
        viewModelScope.launch {
            _registrationUiState.update { it.copy(isLoading = true, error = null) }
            try {
                val form = _registrationUiState.value.formState
                if (
                    form.username.isBlank() ||
                    form.password.isBlank() ||
                    form.email.isBlank() ||
                    form.gender.isBlank() ||
                    form.mobileNumber.isBlank() ||
                    form.yearOfBirth == null
                ) {
                    throw IllegalArgumentException("All fields must be filled.")
                }
                if (form.password != form.confirmPassword) {
                    throw IllegalArgumentException("Passwords do not match.")
                }
                if (accountsRepository.getAccountByUsername(form.username) != null) {
                    throw IllegalArgumentException("Username already exists.")
                }
                if (getAge(form.yearOfBirth) < 13) {
                    throw IllegalArgumentException("You must be at least 13 years old to register.")
                }

                accountsRepository.insertAccount(
                    Account(
                        username = form.username,
                        password = form.password,
                        email = form.email,
                        gender = form.gender,
                        mobileNumber = form.mobileNumber,
                        receiveUpdates = form.receiveUpdates,
                        yearOfBirth = form.yearOfBirth?.toInt() ?: 0
                    )
                )
                _registrationUiState.update { it.copy(isLoading = false, isRegistrationSuccess = true) }
                val account = accountsRepository.getAccountByUsername(form.username)
                if (account != null) {
                    _navigationEvent.emit(NavigationEvent.NavigateToLanding(account.id))
                }
            } catch (e: Exception) {
                _registrationUiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            _loginUiState.update { it.copy(isLoading = true, error = null) }
            try {
                val state = _loginUiState.value
                val account = accountsRepository.getAccountByCredentials(state.username, state.password).firstOrNull()
                if (account != null) {
                    _loginUiState.update { it.copy(isLoading = false, isLoginSuccess = true, loggedInUserId = account.id) }
                    _navigationEvent.emit(NavigationEvent.NavigateToLanding(account.id))
                } else {
                    throw IllegalArgumentException("Invalid username or password.")
                }
            } catch (e: Exception) {
                _loginUiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun cancel() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToLogin)
        }
    }

    private fun getAge(birthDate: Long?): Int {
        if (birthDate == null) return 0
        val dob = Calendar.getInstance().apply { timeInMillis = birthDate }
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    MyApplicationTheme {
        RegistrationScreen(
            uiState = RegistrationUiState(),
            onUsernameChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onEmailChange = {},
            onGenderChange = {},
            onMobileNumberChange = {},
            onReceiveUpdatesChange = {},
            onYearOfBirthChange = {},
            onRegisterClick = {},
            onCancelClick = {}
        )
    }
}
