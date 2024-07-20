package com.szabist.zabapp1.data.model

data class MonthlyBill(
    var billId: String = "",
    val userId: String = "",
    val month: String = "",
    var amount: Double = 0.0,
    val paid: Boolean = false,
    var flaggedAsPaid: Boolean = false,
    var orders: List<Order> = listOf(),
    val userIdMonth: String = userId + "_" + month  // Ensure this is set whenever a bill is created or updated.
)