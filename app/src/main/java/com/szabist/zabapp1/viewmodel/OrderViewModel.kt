package com.szabist.zabapp1.viewmodel

import android.annotation.SuppressLint
import android.content.Context
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
                val activeOrders = orders.filter { it.status !in listOf("Rejected", "Completed") }
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
                val readyForPickupOrders =
                    allOrders.filter { it.status.equals("Ready for Pickup", ignoreCase = true) }
                _pastOrders.value = readyForPickupOrders
                Log.d(
                    "OrderViewModel",
                    "Loaded past orders ready for pickup: $readyForPickupOrders"
                )
            }
        }
    }

    fun updateOrder(order: Order) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.updateOrder(order)
            loadOrders(order.userId)  // Re-load orders to refresh the list
        }
    }

    fun updateOrderStatus(
        orderId: String,
        newStatus: String,
        context: Context,
        handleMonthlyBill: (Order) -> Unit = {},
        onComplete: (Boolean) -> Unit = {}
    ) {
        // Optimistically update the UI
        updateOrderInState(orderId, newStatus)

        // Perform backend update asynchronously
        viewModelScope.launch(Dispatchers.IO) {
            val validStatuses = mapOf(
                "Accept" to "Accepted",
                "Reject" to "Rejected",
                "Prepare" to "Prepare",
                "Ready for Pickup" to "Ready for Pickup",
                "Completed" to "Completed"
            )
            val finalStatus = validStatuses[newStatus] ?: newStatus

            orderRepository.updateOrderStatus(context, orderId, finalStatus) { success ->
                if (success) {
                    // Fetch the updated order to ensure state consistency
                    orderRepository.getOrderById(orderId) { order ->
                        if (order != null) {
                            if (finalStatus == "Accepted" && order.paymentMethod == "bill") {
                                handleMonthlyBill(order)
                            }
                            // Update the local state with the updated order
                            updateOrderInState(order.id, order.status)
                        }
                        // Notify the caller that the update was successful
                        onComplete(true)
                    }
                } else {
                    // Notify the caller that the update failed
                    onComplete(false)
                }
            }
        }
    }

    fun updateOrderInState(orderId: String, newStatus: String) {
        viewModelScope.launch(Dispatchers.Main) {
            // Map orders to update the status or remove it from the list
            _orders.value = _orders.value.map { order ->
                if (order.id == orderId) {
                    order.copy(status = newStatus) // Update the status
                } else {
                    order
                }
            }
        }
    }


    @SuppressLint("StateFlowValueCalledInComposition")
    fun loadOrderById(orderId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getOrderById(orderId) { order ->
                _currentOrder.value = order
            }
        }
    }


    fun deleteOrder(orderId: String, userId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = orderRepository.deleteOrder(orderId)
            viewModelScope.launch(Dispatchers.Main) {
                if (success) {
                    loadOrders(userId)  // Refresh orders for the user
                    onSuccess()
                } else {
                    onFailure()
                }
            }
        }
    }
    fun observeOrderStatusChanges(userId: String, onStatusChanged: (Order) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.observeOrderStatusChanges(userId, onStatusChanged)
        }
    }
    fun observeUserOrders(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.observeOrders(userId) { updatedOrders ->
                _orders.value = updatedOrders
                Log.d("OrderViewModel", "Real-time orders updated: $updatedOrders")
            }
        }
    }

}

