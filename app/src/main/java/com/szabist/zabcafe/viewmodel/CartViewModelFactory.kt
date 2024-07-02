package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szabist.zabcafe.repository.CartRepository

class CartViewModelFactory(private val userId: String, private val cartRepository: CartRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(userId, cartRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}