package com.szabist.zabapp1.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
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
    fun getOrderById(orderId: String, callback: (Order?) -> Unit) {
        ordersRef.child(orderId).get().addOnSuccessListener { snapshot ->
            val order = snapshot.getValue(Order::class.java)
            callback(order)
        }.addOnFailureListener {
            Log.e("OrderRepository", "Error fetching order with ID $orderId", it)
            callback(null)
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
    fun getAllOrders(callback: (List<Order>) -> Unit) {
        val ordersRef = Firebase.database.reference.child("orders")

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ordersList = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    if (order != null) {
                        ordersList.add(order)
                    }
                }
                callback(ordersList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrderRepository", "Error fetching orders: ${error.message}")
                callback(emptyList())
            }
        })
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