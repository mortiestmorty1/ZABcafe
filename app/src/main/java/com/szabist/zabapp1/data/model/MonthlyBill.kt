package com.szabist.zabapp1.data.model

data class MonthlyBill(
    val userId: String = "",
    val month: String = "",
    val amount: Double = 0.0,
    val paid: Boolean = false,
    val orders: List<Order> = listOf()
)