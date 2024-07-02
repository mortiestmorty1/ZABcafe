package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import com.szabist.zabcafe.repository.OrderRepository
import com.szabist.zabcafe.repository.UserRepository

class AdminDashboardViewModel(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    // Functions to interact with the repository will be added here
}