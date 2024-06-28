package com.szabist.zabcafe.utils

import com.google.firebase.database.FirebaseDatabase

object FirebaseUtil {
    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance(Constants.DATABASE_URL)
    }

    fun getUserReference() = database.getReference(Constants.USERS_NODE)
    fun getOrderReference() = database.getReference(Constants.ORDERS_NODE)
    fun getMenuItemsReference() = database.getReference(Constants.MENU_ITEMS_NODE)
    fun getCartItemsReference() = database.getReference(Constants.CART_ITEMS_NODE)
    fun getTransactionsReference() = database.getReference(Constants.TRANSACTIONS_NODE)

    // Add other Firebase utility functions here
}