package com.szabist.zabapp1.data.model

data class MenuItem(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val available: Boolean = true
)