package com.szabist.zabcafe.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.szabist.zabcafe.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AccountRepository {

    private val db = FirebaseDatabase.getInstance()
    private val transactionsRef = db.getReference("transactions")

    suspend fun fetchTransactions(): List<Transaction> = withContext(Dispatchers.IO) {
        val snapshot = transactionsRef.get().await()
        return@withContext snapshot.children.mapNotNull { it.getValue<Transaction>() }
    }
}