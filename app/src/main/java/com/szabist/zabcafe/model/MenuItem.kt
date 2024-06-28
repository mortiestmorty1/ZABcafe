package com.szabist.zabcafe.model

import java.io.Serializable

/**
 * Data class that captures details about a menu item in the app.
 */
data class MenuItem(
    val itemId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",  // Categories like "Beverages", "Snacks", etc.
    val isAvailable: Boolean = true  // Availability might change based on stock or time of day.
) : Serializable {
    // Optionally, you can add methods here to manage stock or handle other business logic.
}