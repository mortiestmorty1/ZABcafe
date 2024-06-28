package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabcafe.model.Order
import com.szabist.zabcafe.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<Order.OrderItem>>(emptyList())
    val cartItems: StateFlow<List<Order.OrderItem>> = _cartItems

    private val _totalCost = MutableStateFlow(0.0)
    val totalCost: StateFlow<Double> = _totalCost

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            val items = orderRepository.fetchCartItems() // Implement this method in your repository
            _cartItems.value = items
            calculateTotalCost(items)
        }
    }

    fun updateCartItemQuantity(itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            val updatedItems = _cartItems.value.map {
                if (it.itemId == itemId) it.copy(quantity = newQuantity) else it
            }
            _cartItems.value = updatedItems
            calculateTotalCost(updatedItems)
            // Update the repository with the new quantities if needed
        }
    }
    fun removeItemFromCart(itemId: String) {
        viewModelScope.launch {
            val updatedItems = _cartItems.value.filterNot { it.itemId == itemId }
            _cartItems.value = updatedItems
            calculateTotalCost(updatedItems)
            orderRepository.removeItemFromCart(itemId)
        }
    }

    fun addItemToCart(orderItem: Order.OrderItem) {
        viewModelScope.launch {
            val success = orderRepository.addItemToCart(orderItem)
            if (success) {
                loadCartItems() // Reload items to reflect the addition
            }
        }
    }

    private fun calculateTotalCost(items: List<Order.OrderItem>) {
        _totalCost.value = items.sumOf { it.price * it.quantity }
    }
}