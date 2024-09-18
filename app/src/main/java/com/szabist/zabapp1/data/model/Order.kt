package com.szabist.zabapp1.data.model

data class Order(
    var id: String = "",
    val userId: String = "",
    val items: List<MenuItem> = listOf(),
    var totalAmount: Double = 0.0,
    var status: String = "pending",
    val paymentMethod: String = "bill" // Can be "bill" or "cash"
)