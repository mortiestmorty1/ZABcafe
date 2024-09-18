package com.szabist.zabapp1.data.model

data class MonthlyBill(
    var billId: String = "",
    val userId: String = "",
    val month: String = "",
    var amount: Double = 0.0,
    var paid: Boolean = false,
    var partialPaid: Boolean = false,
    var partialPaymentAmount: Double = 0.0,
    var arrears: Double = 0.0, // Remaining amount that will be carried over to next month
    var adminApproved: Boolean = false,
    var orders: List<Order> = listOf(),
    var ordersMade: Boolean = orders.isNotEmpty(),
    var userIdMonth: String = "${userId}_$month"
)