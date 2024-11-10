package com.szabist.zabapp1.data.model

data class MenuItem(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val available: Boolean = true,
    val imageUrl: String? = null,
    val categoryId: String? = null
)
val predefinedCategories = listOf(
    "Ready to Eat",
    "Ready to Cook",
    "Beverages",
    "Dessert"
)