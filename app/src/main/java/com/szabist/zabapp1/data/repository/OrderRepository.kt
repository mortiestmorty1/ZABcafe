package com.szabist.zabapp1.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.szabist.zabapp1.data.firebase.FirebaseService
import com.szabist.zabapp1.data.firebase.NotificationHelper
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.data.model.User
import kotlinx.coroutines.tasks.await
import java.util.Date

class OrderRepository {
    private val ordersRef: DatabaseReference = FirebaseService.getDatabaseReference("orders")
    private val usersRef: DatabaseReference = FirebaseService.getDatabaseReference("users")

    private suspend fun fetchUserName(userId: String): String {
        val snapshot = usersRef.child(userId).get().await()
        val user = snapshot.getValue(User::class.java)
        return user?.username ?: "Unknown"
    }
    suspend fun addOrder(order: Order, onSuccess: (Boolean, String?) -> Unit) {
        val key = ordersRef.push().key
        if (key != null) {
            // Fetch the user's name
            val userName = fetchUserName(order.userId)
            order.id = key
            order.userName = userName  // Set the user name in the order
            order.timestamp = Date()
            ordersRef.child(key).setValue(order).addOnSuccessListener {
                Log.d("OrderRepository", "Order successfully added with ID: $key")
                onSuccess(true, key)  // Pass the order ID on success
            }.addOnFailureListener {
                Log.e("OrderRepository", "Failed to add order: ", it)
                onSuccess(false, null)
            }
        } else {
            Log.e("OrderRepository", "Failed to generate a key for the new order")
            onSuccess(false, null)
        }
    }
    fun observeOrders(userId: String, onOrderUpdated: (List<Order>) -> Unit) {
        ordersRef.orderByChild("userId").equalTo(userId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedOrders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                onOrderUpdated(updatedOrders)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrderRepository", "Error observing orders: ${error.message}")
            }
        })
    }
    fun observeOrderStatusChanges(userId: String, onStatusChanged: (Order) -> Unit) {
        ordersRef.orderByChild("userId").equalTo(userId).addChildEventListener(object : ChildEventListener {
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedOrder = snapshot.getValue(Order::class.java)
                if (updatedOrder != null) {
                    onStatusChanged(updatedOrder)
                }
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
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
                .filter { it.status == "Completed" || it.status == "Rejected" }
            callback(orders)
        }.addOnFailureListener {
            Log.e("OrderRepository", "Error fetching past orders for userId $userId", it)
        }
    }

    fun getOrders(userId: String, callback: (List<Order>) -> Unit) {
        ordersRef.orderByChild("userId").equalTo(userId).get().addOnSuccessListener { snapshot ->
            val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
            callback(orders)
        }.addOnFailureListener {
            Log.e("OrderRepository", "Error fetching orders for userId $userId", it)
        }
    }
    fun getAllOrders(callback: (List<Order>) -> Unit) {
        ordersRef.get().addOnSuccessListener { snapshot ->
            val ordersList = mutableListOf<Order>()
            snapshot.children.forEach { orderSnapshot ->
                val order = orderSnapshot.getValue(Order::class.java)
                if (order != null) {
                    ordersList.add(order)
                }
            }
            callback(ordersList)
        }.addOnFailureListener {
            Log.e("OrderRepository", "Error fetching orders: ${it.message}")
            callback(emptyList())
        }
    }


    fun updateOrder(order: Order) {
        ordersRef.child(order.id).setValue(order)
    }

    suspend fun deleteOrder(orderId: String): Boolean {
        return try {
            ordersRef.child(orderId).removeValue().await()
            Log.d("OrderRepository", "Order deleted successfully with ID: $orderId")
            true
        } catch (e: Exception) {
            Log.e("OrderRepository", "Error deleting order with ID: $orderId", e)
            false
        }
    }

    fun updateOrderStatus(
        context: Context,
        orderId: String,
        newStatus: String,
        callback: (Boolean) -> Unit
    ) {
        // Update the order status in the database
        ordersRef.child(orderId).child("status").setValue(newStatus).addOnSuccessListener {
            // Fetch the updated order details
            ordersRef.child(orderId).get().addOnSuccessListener { snapshot ->
                val order = snapshot.getValue(Order::class.java)
                if (order != null) {
                    // Send notification for order status change
                    val notificationHelper = NotificationHelper(context)
                    notificationHelper.sendNotification(
                        title = "Order Update",
                        message = "Your order status has been updated to $newStatus.",
                        type = "order",
                        id = orderId
                    )

                    // Trigger real-time database updates for the user
                    ordersRef.child(orderId).child("lastUpdated").setValue(System.currentTimeMillis())
                }
            }
            // Trigger the callback for successful operation
            callback(true)
        }.addOnFailureListener { exception ->
            // Log the error and trigger the failure callback
            Log.e("OrderRepository", "Failed to update order status: $exception")
            callback(false)
        }
    }

}