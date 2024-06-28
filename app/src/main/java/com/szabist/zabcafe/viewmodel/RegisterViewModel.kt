package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabcafe.model.User
import com.szabist.zabcafe.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun registerUser(username: String, password: String, email: String, role: String, contactNumber: String) {
        if (username.isBlank() || password.isBlank() || email.isBlank() || role.isBlank() || contactNumber.isBlank()) {
            _registerState.value = RegisterState.Error("All fields must be filled")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            val success = userRepository.addUser(User(username, password, email, role, contactNumber))
            _registerState.value = if (success) RegisterState.Success else RegisterState.Error("Registration failed")
        }
    }

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}