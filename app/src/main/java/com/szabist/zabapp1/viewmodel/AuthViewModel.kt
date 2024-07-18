package com.szabist.zabapp1.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.szabist.zabapp1.data.model.User

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    fun getUser(userId: String, onComplete: (User?) -> Unit) {
        val userRef = database.child("users").child(userId)
        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = task.result.getValue(User::class.java)
                onComplete(user)
            } else {
                Log.e("AuthViewModel", "Error getting user data", task.exception)
                onComplete(null)
            }
        }
    }

    fun addUser(user: User, onComplete: () -> Unit) {
        val userRef = database.child("users").child(user.id)
        userRef.setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete()
            } else {
                Log.e("AuthViewModel", "Error adding user", task.exception)
            }
        }
    }
}