package com.szabist.zabcafe.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabcafe.model.User
import com.szabist.zabcafe.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    var isAdmin = MutableLiveData<Boolean>(false)
    fun addUser(user: User, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.addUser(user)
            if (result) {
                onSuccess()
            } else {
                onError("Failed to add user")
            }
        }
    }
    fun updateUser(userId: String, updatedUser: User, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.updateUser(userId, updatedUser)
            if (result) {
                onSuccess()
            } else {
                onError("Failed to update user")
            }
        }
    }

    fun deleteUser(userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = userRepository.deleteUser(userId)
                if (result) {
                    onSuccess()
                    Log.d("DeleteUser", "User deleted successfully")
                } else {
                    onError("Failed to delete user")
                    Log.e("DeleteUser", "Failed to delete user")
                }
            } catch (e: Exception) {
                onError("Error: ${e.message}")
                Log.e("DeleteUser", "Exception when trying to delete: ${e.message}")
            }
        }
    }

    fun fetchUserById(userId: String, onSuccess: (User?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = userRepository.fetchUserById(userId)
                if (user?.role != "admin") {
                    Log.d("UserViewModel", "Fetched user: $user")
                    onSuccess(user)
                } else {
                    Log.d("UserViewModel", "Admin user fetch attempted")
                    onSuccess(null)
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching user: ${e.message}")
                onSuccess(null)
            }
        }
    }
    fun fetchAllUsers(onResult: (List<User>) -> Unit) {
        viewModelScope.launch {
            val users = userRepository.fetchAllUsers()
            onResult(users)
        }
    }

}