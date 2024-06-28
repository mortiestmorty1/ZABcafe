package com.szabist.zabcafe.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.szabist.zabcafe.model.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MenuRepository {

    private val db = FirebaseDatabase.getInstance()
    private val menuRef = db.getReference("menuItems")

    // Add a new menu item
    suspend fun addMenuItem(menuItem: MenuItem): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val key = menuRef.push().key ?: throw Exception("Invalid key")
            menuRef.child(key).setValue(menuItem).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Retrieve all menu items
    suspend fun fetchMenuItems(): List<MenuItem> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = menuRef.get().await()
            snapshot.children.mapNotNull { it.getValue<MenuItem>() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Update an existing menu item
    suspend fun updateMenuItem(menuItemId: String, menuItem: MenuItem): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            menuRef.child(menuItemId).setValue(menuItem).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Delete a menu item
    suspend fun deleteMenuItem(menuItemId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            menuRef.child(menuItemId).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}