package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val accountsRepository: AccountsRepository,
    private val userId: Int
) : ViewModel() {

    private val _account = MutableStateFlow<Account?>(null)
    val account: StateFlow<Account?> = _account.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _mobileNumber = MutableStateFlow("")
    val mobileNumber: StateFlow<String> = _mobileNumber.asStateFlow()

    private val _receiveUpdates = MutableStateFlow(false)
    val receiveUpdates: StateFlow<Boolean> = _receiveUpdates.asStateFlow()

    private val _passwordMismatchError = MutableStateFlow(false)
    val passwordMismatchError: StateFlow<Boolean> = _passwordMismatchError.asStateFlow()

    init {
        viewModelScope.launch {
            val account = accountsRepository.getAccountByIdStream(userId).first()
            _account.value = account
            _mobileNumber.value = account?.mobileNumber ?: ""
            _receiveUpdates.value = account?.receiveUpdates ?: false
        }
    }

    fun onPasswordChange(password: String) {
        _password.value = password
        if (_passwordMismatchError.value) {
            _passwordMismatchError.value = false
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
        if (_passwordMismatchError.value) {
            _passwordMismatchError.value = false
        }
    }

    fun onMobileNumberChange(mobileNumber: String) {
        _mobileNumber.value = mobileNumber
    }

    fun onReceiveUpdatesChange(receiveUpdates: Boolean) {
        _receiveUpdates.value = receiveUpdates
    }

    fun onSaveChanges(onSuccess: () -> Unit) {
        if (password.value.isNotEmpty() || confirmPassword.value.isNotEmpty()) {
            if (password.value != confirmPassword.value) {
                _passwordMismatchError.value = true
                return
            }
        }

        viewModelScope.launch {
            val currentAccount = account.value ?: return@launch

            val updatedAccount = currentAccount.copy(
                password = if (password.value.isNotEmpty()) password.value else currentAccount.password,
                mobileNumber = mobileNumber.value,
                receiveUpdates = receiveUpdates.value
            )
            accountsRepository.updateAccount(updatedAccount)
            onSuccess()
        }
    }
}