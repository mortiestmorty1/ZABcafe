package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szabist.zabcafe.repository.OrderRepository
import com.szabist.zabcafe.repository.UserRepository

class AdminDashboardViewModelFactory(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminDashboardViewModel::class.java)) {
            return AdminDashboardViewModel(orderRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}