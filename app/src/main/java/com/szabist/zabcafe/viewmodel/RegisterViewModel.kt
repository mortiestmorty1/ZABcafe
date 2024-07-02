package com.szabist.zabcafe.viewmodel

import android.util.Log
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

        val newUser = User(
            username = username,
            password = password,
            email = email,
            role = role,
            contactNumber = contactNumber
        )

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val success = userRepository.addUser(newUser)
                if (success) {
                    _registerState.value = RegisterState.Success
                } else {
                    _registerState.value = RegisterState.Error("Failed to register user")
                }
            } catch (e: Exception) {
                _registerState.value =
                    RegisterState.Error("An error occurred: ${e.localizedMessage}")
                Log.e("RegisterError", "Error registering user: ${e.message}", e)
            }
        }
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}