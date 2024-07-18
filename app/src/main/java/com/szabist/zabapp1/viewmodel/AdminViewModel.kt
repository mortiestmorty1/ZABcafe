package com.szabist.zabapp1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.database.FirebaseDatabase
import com.szabist.zabapp1.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await

class AdminViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference

    private suspend fun fetchAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val usersRef = database.child("users")
        val snapshot = usersRef.get().await()
        snapshot.children.forEach { snapshot ->
            val user = snapshot.getValue(User::class.java)
            user?.let { users.add(it) }
        }
        return users
    }

    fun getAllUsers() = liveData(Dispatchers.IO) {
        val users = fetchAllUsers()
        emit(users)
    }
}