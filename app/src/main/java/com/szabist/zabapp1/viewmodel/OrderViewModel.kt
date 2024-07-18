package com.szabist.zabapp1.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.data.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val orderRepository = OrderRepository()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _pastOrders = MutableStateFlow<List<Order>>(emptyList())
    val pastOrders: StateFlow<List<Order>> = _pastOrders

    fun addOrder(order: Order, onSuccess: (Order) -> Unit) {
        orderRepository.addOrder(order, onSuccess)
    }

    fun loadOrders(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getOrders(userId) { orders ->
                _orders.value = orders
                Log.d("OrderViewModel", "Orders loaded: $orders")
            }
        }
    }
    fun loadPastOrders(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getPastOrders(userId) { orders ->
                _pastOrders.value = orders
            }
        }
    }

    fun updateOrder(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.updateOrder(order)
            loadOrders(order.userId)  // Re-load orders to refresh the list
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.updateOrderStatus(orderId, newStatus)
            // Optionally refresh the orders list here if needed
        }
    }

    fun deleteOrder(orderId: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.deleteOrder(orderId)
            loadOrders(userId)  // Re-load orders after deleting
        }
    }
}