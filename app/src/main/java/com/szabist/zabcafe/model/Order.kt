package com.szabist.zabcafe.model

import java.io.Serializable
import java.util.Date

/**
 * Data class that captures order details for transactions processed in the app.
 */
data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<OrderItem> = listOf(),
    val totalCost: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Serializable {

    /**
     * Nested class to handle individual items in an order.
     */
    data class OrderItem(
        val itemId: String,
        val itemName: String,
        val quantity: Int,
        val price: Double
    ) : Serializable

    /**
     * Enum class for order statuses.
     */
    enum class OrderStatus {
        PENDING,
        CONFIRMED,
        READYFORPICKUP,
        CANCELLED
    }
}