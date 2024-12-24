package com.szabist.zabapp1.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.data.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class OrderViewModel : ViewModel() {
    private val orderRepository = OrderRepository()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _pastOrders = MutableStateFlow<List<Order>>(emptyList())
    val pastOrders: StateFlow<List<Order>> = _pastOrders

    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder

    @RequiresApi(Build.VERSION_CODES.O)
    fun addOrder(order: Order, onSuccess: (Boolean, String?) -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            order.timestamp = Date()
            orderRepository.addOrder(order) { success, orderId ->
                if (success && orderId != null) {
                    loadAllOrders()  // Refresh orders after adding
                    onSuccess(true, orderId)
                } else {
                    onFailure()
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)


    fun loadAllOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getAllOrders { orders ->
                _orders.value = orders
                Log.d("OrderViewModel", "All orders loaded: $orders")
            }
        }
    }

    fun loadOrders(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getOrders(userId) { orders ->
                // Filter out orders with status "Ready for Pickup" and "Rejected"
                val activeOrders = orders.filter { it.status !in listOf("Rejected" ,"Completed") }
                _orders.value = activeOrders
                Log.d("OrderViewModel", "Active orders loaded: $activeOrders")
            }
        }
    }

    fun loadPastOrders(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getPastOrders(userId) { orders ->
                _pastOrders.value = orders
                Log.d("OrderViewModel", "Loaded past orders: $orders")
            }
        }
    }
    fun loadPastOrdersReadyForPickup(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getAllOrders { allOrders ->
                // Filter orders with status "Ready for Pickup"
                val readyForPickupOrders = allOrders.filter { it.status.equals("Ready for Pickup", ignoreCase = true) }
                _pastOrders.value = readyForPickupOrders
                Log.d("OrderViewModel", "Loaded past orders ready for pickup: $readyForPickupOrders")
            }
        }
    }

        fun updateOrder(order: Order) {
            viewModelScope.launch(Dispatchers.IO) {
                orderRepository.updateOrder(order)
                loadOrders(order.userId)  // Re-load orders to refresh the list
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateOrderStatus(orderId: String, newStatus: String,onComplete: () -> Unit = {}){
    viewModelScope.launch(Dispatchers.IO) {
            orderRepository.updateOrderStatus(orderId, newStatus)
            // Reload past orders after updating status
            loadAllOrders()
            orderRepository.getOrderById(orderId) { order ->
                if (order != null) {
                    loadPastOrders(order.userId)

                }

            }
        onComplete()
        }
    }

    fun loadOrderById(orderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getOrderById(orderId) { order ->
                _currentOrder.value = order
            }
        }
    }


        fun deleteOrder(orderId: String, userId: String) {
            viewModelScope.launch(Dispatchers.IO) {
                orderRepository.deleteOrder(orderId)
                loadOrders(userId)  // Re-load orders after deleting
            }
        }
    }

