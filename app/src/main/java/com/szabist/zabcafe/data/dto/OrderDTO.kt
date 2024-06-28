package com.szabist.zabcafe.data.dto

import java.io.Serializable
import java.util.Date

data class OrderDTO(
    val orderId: String = "",
    val userId: String = "",
    val items: List<OrderItemDTO> = listOf(),
    val totalCost: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Serializable {

    data class OrderItemDTO(
        val itemId: String,
        val itemName: String,
        val quantity: Int,
        val price: Double
    ) : Serializable

    enum class OrderStatus {
        PENDING,
        CONFIRMED,
        READYFORPICKUP,
        CANCELLED
    }
}