package com.szabist.zabapp1.data.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseService {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://zabcafe1-default-rtdb.firebaseio.com/")

    fun getDatabaseReference(path: String): DatabaseReference {
        return database.getReference(path)
    }
}