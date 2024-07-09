package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabcafe.model.CartItem
import com.szabist.zabcafe.model.Order
import com.szabist.zabcafe.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CartViewModel(private val userId: String, private val orderRepository: OrderRepository) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    val cartItemCount: StateFlow<Int> = _cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(),
            initialValue = 0
        )

    val total: Double
        get() = _cartItems.value.sumOf { it.price * it.quantity }

    fun addToCart(item: CartItem) {
        viewModelScope.launch {
            val existingItem = _cartItems.value.find { it.itemId == item.itemId }
            if (existingItem == null) {
                _cartItems.value = _cartItems.value + item
            } else {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
                _cartItems.value = _cartItems.value.map { if (it.itemId == item.itemId) updatedItem else it }
            }
        }
    }

    fun removeFromCart(itemId: String) {
        viewModelScope.launch {
            _cartItems.value = _cartItems.value.filter { it.itemId != itemId }
        }
    }

    fun updateQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            _cartItems.value = _cartItems.value.map {
                if (it.itemId == itemId) it.copy(quantity = quantity) else it
            }
        }
    }

    fun addOrder() {
        viewModelScope.launch {
            val orderItems = _cartItems.value.map { Order.OrderItem(it.itemId, it.name, it.quantity, it.price) }
            val order = Order(
                orderId = UUID.randomUUID().toString(),
                userId = userId,
                items = orderItems,
                totalCost = total,
                status = Order.OrderStatus.PENDING,
                createdAt = Date(),
                updatedAt = Date()
            )
            if (orderRepository.createOrder(order)) {
                _cartItems.value = emptyList()  // Clear cart after successful order
            }
        }
    }
}