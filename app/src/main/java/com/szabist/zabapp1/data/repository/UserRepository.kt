package com.szabist.zabapp1.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.szabist.zabapp1.data.firebase.FirebaseService
import com.szabist.zabapp1.data.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val usersRef: DatabaseReference = FirebaseService.getDatabaseReference("users")
    private val auth = FirebaseAuth.getInstance()
    suspend fun addUser(user: User) {
        val key = usersRef.push().key ?: return
        user.id = key  // Ensure the user ID is set
        usersRef.child(key).setValue(user).await()
    }
    suspend fun addUserWithAuth(user: User, password: String, onComplete: (Boolean, String?) -> Unit) {
        try {
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()
            val userId = result.user?.uid ?: return
            user.id = userId
            usersRef.child(userId).setValue(user).await()
            onComplete(true, null)  // Success
        } catch (e: FirebaseAuthUserCollisionException) {
            // Handle specific case when the email is already in use
            Log.e("UserRepository", "User already exists with this email", e)
            onComplete(false, "User already exists with this email.")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error creating user with authentication", e)
            onComplete(false, e.localizedMessage ?: "An unknown error occurred")
        }
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
        val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
        Log.d("UserRepository", "Fetched users from Firebase: $users") // Log fetched users
        return users.filter { it.role != "admin" } // Ensure admin is filtered out
    }

    suspend fun updateUser(user: User) {
        usersRef.child(user.id).setValue(user).await()
    }

    suspend fun deleteUser(userId: String) {
        usersRef.child(userId).removeValue().await()
    }
}