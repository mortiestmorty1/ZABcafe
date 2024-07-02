package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabcafe.model.User
import com.szabist.zabcafe.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Username and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val user = userRepository.authenticateUser(username, password)
            if (user != null) {
                if (userRepository.checkAdminStatus(user.userId)) {
                    _loginState.value = LoginState.SuccessAdmin(user)
                } else {
                    _loginState.value = LoginState.Success(user)
                }
            } else {
                _loginState.value = LoginState.Error("Invalid username or password")
            }
        }
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: User) : LoginState()  // Now carries user data
        data class SuccessAdmin(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()

    }
}