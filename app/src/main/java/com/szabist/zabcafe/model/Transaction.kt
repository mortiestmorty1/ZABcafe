package com.szabist.zabcafe.model

import java.io.Serializable
import java.util.Date

/**
 * Data class that captures transaction details for monthly billing.
 */
data class Transaction(
    val transactionId: String = "",
    val description: String = "",
    val date: Date = Date(),
    val amount: Double = 0.0
) : Serializable