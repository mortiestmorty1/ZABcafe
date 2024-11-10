package com.szabist.zabapp1.data.model

import java.util.Date

data class Order(
    var id: String = "",
    val userId: String = "",
    var userName: String = "",
    val items: List<MenuItem> = listOf(),
    var totalAmount: Double = 0.0,
    var status: String = "pending",
    val paymentMethod: String = "bill",
    var timestamp: Date = Date() // Use Date instead of LocalDateTime
)