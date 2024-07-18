package com.szabist.zabapp1.data.repository

import com.google.firebase.database.DatabaseReference
import com.szabist.zabapp1.data.firebase.FirebaseService
import com.szabist.zabapp1.data.model.MonthlyBill

class MonthlyBillRepository {
    private val billsRef: DatabaseReference = FirebaseService.getDatabaseReference("monthly_bills")

    fun addMonthlyBill(bill: MonthlyBill) {
        val key = billsRef.push().key ?: return
        billsRef.child(key).setValue(bill)
    }

    fun getMonthlyBills(userId: String, callback: (List<MonthlyBill>) -> Unit) {
        billsRef.orderByChild("userId").equalTo(userId).get().addOnSuccessListener { snapshot ->
            val bills = snapshot.children.mapNotNull { it.getValue(MonthlyBill::class.java) }
            callback(bills)
        }
    }

    fun updateMonthlyBill(bill: MonthlyBill) {
        billsRef.child(bill.userId).setValue(bill)
    }
}