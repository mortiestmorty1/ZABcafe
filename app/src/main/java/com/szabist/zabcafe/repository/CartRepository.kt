package com.szabist.zabcafe.repository

import com.google.firebase.database.FirebaseDatabase
import com.szabist.zabcafe.model.CartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CartRepository {

    private val db = FirebaseDatabase.getInstance()
    private val cartRef = db.getReference("cartItems")

    suspend fun addCartItem(userId: String, cartItem: CartItem): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val userCartRef = cartRef.child(userId)
            val key = userCartRef.push().key ?: throw Exception("Failed to generate a unique key for cart item")
            userCartRef.child(key).setValue(cartItem.copy(itemId = key)).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getCartItems(userId: String): List<CartItem> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = cartRef.child(userId).get().await()
            snapshot.children.mapNotNull { it.getValue(CartItem::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateCartItem(userId: String, cartItem: CartItem): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            cartRef.child(userId).child(cartItem.itemId).setValue(cartItem).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeCartItem(userId: String, itemId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            cartRef.child(userId).child(itemId).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun clearCart(userId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            cartRef.child(userId).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}