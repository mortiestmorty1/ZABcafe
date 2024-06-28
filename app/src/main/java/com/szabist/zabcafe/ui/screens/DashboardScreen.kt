package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabcafe.model.User
import com.szabist.zabcafe.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> = _userInfo

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            // Replace "currentUserId" with the actual user ID logic
            val user = userRepository.fetchUserById("currentUserId")
            _userInfo.value = user
            _userRole.value = user?.role
        }
    }
}