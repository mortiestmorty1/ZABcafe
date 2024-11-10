package com.szabist.zabapp1.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.szabist.zabapp1.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference

    // Admin user details will be stored in this property
    val adminUser: MutableLiveData<User?> = MutableLiveData()

    private suspend fun fetchAdminUser(adminId: String): User? {
        val userRef = database.child("users").child(adminId)
        val snapshot = userRef.get().await()
        return snapshot.getValue(User::class.java)
    }

    // Method to fetch the admin user details
    fun getAdminUser(adminId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = fetchAdminUser(adminId)
            adminUser.postValue(user) // Update the LiveData with admin details
        }
    }

    // Fetch all users (for other parts of the admin functionality)
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