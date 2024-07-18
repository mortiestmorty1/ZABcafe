package com.szabist.zabapp1.data.repository

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.szabist.zabapp1.data.firebase.FirebaseService
import com.szabist.zabapp1.data.model.Order

class OrderRepository {
    private val ordersRef: DatabaseReference = FirebaseService.getDatabaseReference("orders")

    fun addOrder(order: Order, onSuccess: (Order) -> Unit) {
        val key = ordersRef.push().key
        if (key != null) {
            order.id = key  // Set the order ID
            ordersRef.child(key).setValue(order).addOnSuccessListener {
                onSuccess(order)
            }.addOnFailureListener {
                Log.e("OrderRepository", "Failed to add order: ", it)
            }
        }
    }
    fun getPastOrders(userId: String, callback: (List<Order>) -> Unit) {
        ordersRef.orderByChild("userId").equalTo(userId).get().addOnSuccessListener { snapshot ->
            val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                .filter { it.status == "pickedup" }
            callback(orders)
        }.addOnFailureListener {
            Log.e("OrderRepository", "Error fetching past orders for userId $userId", it)
        }
    }

    fun getOrders(userId: String, callback: (List<Order>) -> Unit) {
        ordersRef.orderByChild("userId").equalTo(userId).get().addOnSuccessListener { snapshot ->
            val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }.also {
                Log.d("OrderRepository", "Fetched orders for userId $userId: $it")
            }
            callback(orders)
        }.addOnFailureListener {
            Log.e("OrderRepository", "Error fetching orders for userId $userId", it)
        }
    }

    fun updateOrder(order: Order) {
        ordersRef.child(order.id).setValue(order)
    }

    fun deleteOrder(orderId: String) {
        ordersRef.child(orderId).removeValue()
    }
    fun updateOrderStatus(orderId: String, newStatus: String) {
        ordersRef.child(orderId).child("status").setValue(newStatus)
    }
}