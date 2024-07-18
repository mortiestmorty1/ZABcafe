package com.szabist.zabapp1.data.model

// Assuming MenuItem is your existing model for items available for purchase.
data class CartItem(
    val menuItem: MenuItem,
    var quantity: Int
)