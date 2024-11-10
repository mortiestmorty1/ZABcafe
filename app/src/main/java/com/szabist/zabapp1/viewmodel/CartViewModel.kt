package com.szabist.zabapp1.viewmodel

import androidx.lifecycle.ViewModel
import com.szabist.zabapp1.data.model.CartItem
import com.szabist.zabapp1.data.model.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    fun addToCart(menuItem: MenuItem, quantity: Int) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find { it.menuItem.id == menuItem.id }
            if (existingItem != null) {
                // If item already exists, update its quantity by adding the specified quantity
                currentItems.map {
                    if (it.menuItem.id == menuItem.id) it.copy(quantity = it.quantity + quantity) else it
                }
            } else {
                // Add the new item with the specified quantity
                currentItems + CartItem(menuItem, quantity)
            }
        }
    }

    fun increaseItemQuantity(menuItem: MenuItem) {
        _cartItems.update { currentItems ->
            currentItems.map { if (it.menuItem.id == menuItem.id) it.copy(quantity = it.quantity + 1) else it }
        }
    }

    fun decreaseItemQuantity(menuItem: MenuItem) {
        _cartItems.update { currentItems ->
            currentItems.mapNotNull {
                if (it.menuItem.id == menuItem.id && it.quantity > 1) it.copy(quantity = it.quantity - 1) else it
            }
        }
    }

    fun removeItemFromCart(menuItem: MenuItem) {
        _cartItems.update { currentItems ->
            currentItems.filterNot { it.menuItem.id == menuItem.id }
        }
    }

    fun isItemInCart(itemId: String): Boolean {
        return _cartItems.value.any { it.menuItem.id == itemId }
    }
    fun clearCart() {
        _cartItems.value = emptyList()  // Clear all items in the cart
    }
}