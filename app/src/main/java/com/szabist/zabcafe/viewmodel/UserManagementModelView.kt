package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabcafe.model.User
import com.szabist.zabcafe.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _userDetails = MutableStateFlow<User?>(null)
    val userDetails: StateFlow<User?> = _userDetails

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            val fetchedUsers = userRepository.fetchAllUsers()
            _users.value = fetchedUsers
        }
    }

    fun fetchUser(userId: String) {
        viewModelScope.launch {
            val user = userRepository.fetchUserById(userId)
            _userDetails.value = user
        }
    }

    fun updateUser(userId: String, updatedUser: User) {
        viewModelScope.launch {
            val success = userRepository.updateUser(userId, updatedUser)
            if (success) {
                _userDetails.value = updatedUser  // Update live data to reflect the changes
                loadUsers()  // Reload users to update UI
            }
            // Handle the error scenario if needed
        }
    }

    fun updateUserRole(userId: String, newRole: String) {
        viewModelScope.launch {
            val user = _users.value.find { it.userId == userId }
            if (user != null) {
                val updatedUser = user.copy(role = newRole)
                val success = userRepository.updateUser(userId, updatedUser)
                if (success) {
                    loadUsers()  // Reload users to update UI
                }
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            val success = userRepository.deleteUser(userId)
            if (success) {
                loadUsers()  // Reload users to reflect changes
            }
            // Handle the error scenario if needed
        }
    }
}