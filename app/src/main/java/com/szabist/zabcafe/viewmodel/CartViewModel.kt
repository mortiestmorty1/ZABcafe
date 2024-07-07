package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabcafe.model.CartItem
import com.szabist.zabcafe.repository.CartRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(private val userId: String, private val cartRepository: CartRepository) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    val cartItemCount: StateFlow<Int> = _cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(
            scope = CoroutineScope(Dispatchers.Default), // Define coroutine scope
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(), // Define when to start collecting
            initialValue = 0
        )

    val total: Double
        get() = _cartItems.value.sumOf { it.price * it.quantity }

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            val items = cartRepository.getCartItems(userId)
            _cartItems.value = items
        }
    }

    fun addToCart(userId: String, item: CartItem) {
        viewModelScope.launch {
            val success = cartRepository.addCartItem(userId, item)
            if (success) {
                loadCartItems()  // Assuming loadCartItems also needs userId
            }
        }
    }

    fun removeFromCart(itemId: String) {
        viewModelScope.launch {
            if (cartRepository.removeCartItem(userId, itemId)) {
                loadCartItems()
            }
        }
    }

    fun updateQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            cartRepository.updateCartItem(userId, _cartItems.value.first { it.itemId == itemId }.copy(quantity = quantity))
            loadCartItems()
        }
    }
}