package com.szabist.zabcafe.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.szabist.zabcafe.utils.Constants

class FirebaseDatabaseManager {

    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance(Constants.DATABASE_URL)
    }

    fun getUserReference() = database.getReference(Constants.USERS_NODE)
    fun getOrderReference() = database.getReference(Constants.ORDERS_NODE)
    fun getMenuItemsReference() = database.getReference(Constants.MENU_ITEMS_NODE)
    fun getCartItemsReference() = database.getReference(Constants.CART_ITEMS_NODE)
    fun getTransactionsReference() = database.getReference(Constants.TRANSACTIONS_NODE)

    // Add other database operations here
}