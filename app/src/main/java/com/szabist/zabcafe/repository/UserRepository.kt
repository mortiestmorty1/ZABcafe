package com.szabist.zabcafe.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.szabist.zabcafe.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {

    private val db = FirebaseDatabase.getInstance()
    private val userRef = db.getReference("users")

    suspend fun authenticateUser(username: String, password: String): Boolean {
        return try {
            val snapshot = userRef.orderByChild("username").equalTo(username).get().await()
            if (snapshot.exists()) {
                val user = snapshot.children.firstOrNull()?.getValue(User::class.java)
                user?.password == password
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // Add a new user
    suspend fun addUser(user: User): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val key = userRef.push().key ?: throw Exception("Invalid key")
            userRef.child(key).setValue(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Retrieve a user by ID
    suspend fun fetchUserById(userId: String): User? = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = userRef.child(userId).get().await()
            snapshot.getValue<User>()
        } catch (e: Exception) {
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
        return@withContext try {
            userRef.child(userId).removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun fetchAllUsers(): List<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = userRef.get().await()
            snapshot.children.mapNotNull { it.getValue<User>() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}