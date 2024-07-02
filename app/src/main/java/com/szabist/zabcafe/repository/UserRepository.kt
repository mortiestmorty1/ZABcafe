package com.szabist.zabcafe.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.szabist.zabcafe.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {

    private val db = FirebaseDatabase.getInstance()
    private val userRef = db.getReference("users")

    suspend fun checkAdminStatus(userId: String): Boolean {
        val adminRef = FirebaseDatabase.getInstance().getReference("admins/$userId")
        val snapshot = adminRef.get().await()
        return snapshot.exists()
    }
    suspend fun authenticateUser(username: String, password: String): User? = withContext(Dispatchers.IO) {
        try {
            val query = db.getReference("users").orderByChild("username").equalTo(username)
            val snapshot = query.get().await()
            if (snapshot.exists()) {
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user?.password == password) {
                        Log.d("LoginCheck", "Authentication successful for user: ${user.username}")
                        return@withContext user
                    }
                }
            }
            Log.d("LoginCheck", "No matching user found or incorrect password.")
        } catch (e: Exception) {
            Log.e("LoginError", "Error fetching user data: ${e.localizedMessage}")
        }
        return@withContext null
    }


    suspend fun addUser(user: User): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            // Generate a new key for each user
            val key = userRef.push().key ?: throw Exception("Failed to generate a unique key")
            val updatedUser = user.copy(userId = key)

            userRef.child(key).setValue(updatedUser).await()
            true
        } catch (e: Exception) {
            false
        }
    }


    // Retrieve a user by ID
    suspend fun fetchUserById(userId: String): User? = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = userRef.orderByChild("userId").equalTo(userId).get().await()
            if (snapshot.exists()) {
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    Log.d("UserRepository", "Fetched user: $user")
                    return@withContext user
                }
            }
            Log.d("UserRepository", "User not found with ID: $userId")
            null
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user by ID: ${e.message}")
            null
        }
    }

    // Update an existing user
    suspend fun updateUser(userId: String, user: User): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            userRef.child(userId).setValue(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Delete a user
    suspend fun deleteUser(userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val query = userRef.orderByChild("userId").equalTo(userId)
            val snapshot = query.get().await()
            if (snapshot.exists()) {
                snapshot.children.forEach {
                    val username = it.key ?: return@forEach
                    userRef.child(username).removeValue().await()
                }
                true
            } else {
                Log.d("DeleteUser", "No user found with ID: $userId")
                false
            }
        } catch (e: Exception) {
            Log.e("DeleteUser", "Failed to delete user: ${e.localizedMessage}")
            false
        }
    }

    suspend fun fetchAllUsers(): List<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = userRef.get().await()
            val users = snapshot.children.mapNotNull { it.getValue<User>() }
            users.filter { it.role != "admin" } // Filter out admin users
        } catch (e: Exception) {
            emptyList()
        }
    }
}