package com.szabist.zabapp1.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabapp1.data.model.User
import com.szabist.zabapp1.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()
    private val _currentUserRole = MutableStateFlow<String?>(null)
    val currentUserRole: StateFlow<String?> = _currentUserRole.asStateFlow()
    private val _logoutEvent = MutableStateFlow<Boolean?>(null)
    val logoutEvent: StateFlow<Boolean?> = _logoutEvent.asStateFlow()


    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val users = userRepository.getUsers()
                _users.value = users
                Log.d("UserViewModel", "Fetched users: $users")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching users", e)
            }
        }
    }

    fun addUser(user: User, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.addUser(user)
            fetchUsers()
            onComplete()
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userRepository.updateUser(user)
                fetchUsers()
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating user", e)
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userRepository.deleteUser(userId)
                fetchUsers()
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error deleting user", e)
            }
        }
    }

    fun fetchUserById(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = userRepository.getUserById(userId)
                _currentUserRole.value = user?.role
                Log.d("UserViewModel", "Fetched user: $user with role: ${user?.role}")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching user by ID: $userId", e)
                _currentUserRole.value = null // Ensure the role is set to null in case of error
            }
        }
    }
    fun fetchUserDetails(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = userRepository.getUserById(userId)
                _currentUser.value = user // Assuming _currentUser is MutableStateFlow<User?>
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching user details", e)
            }
        }
    }
}

