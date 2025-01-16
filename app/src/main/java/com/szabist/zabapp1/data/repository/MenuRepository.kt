package com.szabist.zabapp1.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.szabist.zabapp1.data.model.MenuItem
import kotlinx.coroutines.tasks.await

class MenuRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private val menuRef: DatabaseReference = database.child("menu_items")


    suspend fun addMenuItem(menuItem: MenuItem) {
        val key = menuRef.push().key ?: return
        menuItem.id = key
        menuRef.child(key).setValue(menuItem).await()
    }

    fun getMenuItems(callback: (List<MenuItem>) -> Unit) {
        menuRef.get().addOnSuccessListener { snapshot ->
            val items = snapshot.children.mapNotNull { it.getValue(MenuItem::class.java) }
            callback(items)
        }
    }
    fun getMenuItemsRealtime(callback: (List<MenuItem>) -> Unit) {
        menuRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(MenuItem::class.java) }
                callback(items)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MenuRepository", "Failed to fetch menu items: ${error.message}")
            }
        })
    }

    fun updateMenuItem(menuItem: MenuItem) {
        menuRef.child(menuItem.id).setValue(menuItem)
    }

    fun deleteMenuItem(menuItemId: String) {
        menuRef.child(menuItemId).removeValue()
    }
    fun generateMenuItemId(): String {
        return menuRef.push().key ?: ""
    }
    fun getMenuItemById(menuItemId: String, callback: (MenuItem?) -> Unit) {
        menuRef.child(menuItemId).get().addOnSuccessListener { snapshot ->
            val item = snapshot.getValue(MenuItem::class.java)
            callback(item)
        }
    }
}