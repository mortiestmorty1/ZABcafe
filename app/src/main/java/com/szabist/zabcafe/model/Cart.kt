package com.szabist.zabcafe.model

import java.io.Serializable

data class CartItem(
    val itemId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val userId: String
) : Serializable