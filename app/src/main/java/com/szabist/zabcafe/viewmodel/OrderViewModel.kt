package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabcafe.model.Order
import com.szabist.zabcafe.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            val fetchedOrders = orderRepository.fetchOrders()
            _orders.value = fetchedOrders
        }
    }

    fun createOrder(order: Order) {
        viewModelScope.launch {
            val success = orderRepository.createOrder(order)
            if (success) {
                loadOrders() // Reload orders to update UI
            }
            // Additional handling for failure scenario can be implemented here
        }
    }

    fun updateOrder(orderId: String, order: Order) {
        viewModelScope.launch {
            val success = orderRepository.updateOrder(orderId, order)
            if (success) {
                loadOrders() // Reload orders to update UI
            }
            // Additional handling for failure scenario can be implemented here
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            val success = orderRepository.deleteOrder(orderId)
            if (success) {
                loadOrders() // Reload orders to reflect changes
            }
            // Additional handling for failure scenario can be implemented here
        }
    }
}