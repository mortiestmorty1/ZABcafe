package com.szabist.zabapp1.data.repository

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.szabist.zabapp1.data.firebase.FirebaseService
import com.szabist.zabapp1.data.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val usersRef: DatabaseReference = FirebaseService.getDatabaseReference("users")

    suspend fun addUser(user: User) {
        val key = usersRef.push().key ?: return
        user.id = key  // Ensure the user ID is set
        usersRef.child(key).setValue(user).await()
    }

    suspend fun getUserById(userId: String): User? {
        Log.d("UserRepository", "Fetching user with ID: $userId")
        val snapshot = usersRef.child(userId).get().await()
        val user = snapshot.getValue(User::class.java)
        Log.d("UserRepository", "Fetched user: $user")
        return user
    }

    suspend fun getUsers(): List<User> {
        val snapshot = usersRef.get().await()
        return snapshot.children.mapNotNull { it.getValue(User::class.java) }.filter { it.role != "admin" }
    }

    suspend fun updateUser(user: User) {
        usersRef.child(user.id).setValue(user).await()
    }

    suspend fun deleteUser(userId: String) {
        usersRef.child(userId).removeValue().await()
    }
}