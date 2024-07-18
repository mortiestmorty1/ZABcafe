package com.szabist.zabapp1.data.model

data class Order(
    var id: String = "",
    val userId: String = "",
    val items: List<MenuItem> = listOf(),
    val totalAmount: Double = 0.0,
    val status: String = "pending"
)