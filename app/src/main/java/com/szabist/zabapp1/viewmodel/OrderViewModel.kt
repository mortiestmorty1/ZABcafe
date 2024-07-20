package com.szabist.zabapp1.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabapp1.data.model.MonthlyBill
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.data.repository.MonthlyBillRepository
import com.szabist.zabapp1.data.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OrderViewModel : ViewModel() {
    private val orderRepository = OrderRepository()
    private val monthlyBillRepository = MonthlyBillRepository()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _pastOrders = MutableStateFlow<List<Order>>(emptyList())
    val pastOrders: StateFlow<List<Order>> = _pastOrders

    @RequiresApi(Build.VERSION_CODES.O)
    fun addOrder(order: Order, userId: String, onSuccess: (Order) -> Unit) {
        orderRepository.addOrder(order) { addedOrder ->
            updateOrCreateMonthlyBill(addedOrder, userId)
            onSuccess(addedOrder)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateOrCreateMonthlyBill(order: Order, userId: String) {
        val month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.getMonthlyBillByMonth(userId, month) { existingBill ->
                if (existingBill != null) {
                    existingBill.amount += order.totalAmount
                    existingBill.orders += order
                    monthlyBillRepository.updateMonthlyBill(existingBill)
                } else {
                    val newBill = MonthlyBill(
                        userId = userId,
                        month = month,
                        amount = order.totalAmount,
                        orders = listOf(order),
                        billId = ""  // Initially empty, set upon creation in repository
                    )
                    monthlyBillRepository.addMonthlyBill(newBill) { isSuccess ->
                        // Handle success or failure
                    }
                }
            }
        }
    }

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
                    _orders.value = orders
                    Log.d("OrderViewModel", "Orders loaded: $orders")
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

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.updateOrderStatus(orderId, newStatus)
            // Reload past orders after updating status
            orderRepository.getOrderById(orderId) { order ->
                if (order != null) {
                    loadPastOrders(order.userId)
                }
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
