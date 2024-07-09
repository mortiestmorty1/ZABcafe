package com.szabist.zabcafe.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.szabist.zabcafe.model.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OrderRepository {

    private val db = FirebaseDatabase.getInstance()
    private val orderRef = db.getReference("orders")
    private val cartRef = db.getReference("cartItems")

    // Create a new order
    suspend fun createOrder(order: Order): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val key = orderRef.push().key ?: throw Exception("Invalid key")
            orderRef.child(key).setValue(order).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Retrieve an order by ID
    suspend fun fetchOrderById(orderId: String): Order? = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = orderRef.child(orderId).get().await()
            snapshot.getValue<Order>()
        } catch (e: Exception) {
            null
        }
    }

    // Update an existing order
    suspend fun updateOrder(orderId: String, order: Order): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            orderRef.child(orderId).setValue(order).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Delete an order
    suspend fun deleteOrder(orderId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            orderRef.child(orderId).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Fetch all orders
    suspend fun fetchOrders(): List<Order> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = orderRef.get().await()
            snapshot.children.mapNotNull { it.getValue<Order>() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Fetch cart items
    suspend fun fetchCartItems(): List<Order.OrderItem> = withContext(Dispatchers.IO) {
        val snapshot = cartRef.get().await()
        return@withContext snapshot.children.mapNotNull { it.getValue<Order.OrderItem>() }
    }
}