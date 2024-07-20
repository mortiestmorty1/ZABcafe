package com.szabist.zabapp1.data.repository

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.szabist.zabapp1.data.firebase.FirebaseService
import com.szabist.zabapp1.data.model.MonthlyBill

class MonthlyBillRepository {
    private val billsRef: DatabaseReference = FirebaseService.getDatabaseReference("monthly_bills")

    fun addMonthlyBill(bill: MonthlyBill, callback: (Boolean) -> Unit) {
        val key = billsRef.push().key ?: return
        bill.billId = key  // Make sure your model supports billId
        billsRef.child(key).setValue(bill).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun getMonthlyBills(userId: String, callback: (List<MonthlyBill>) -> Unit) {
        billsRef.orderByChild("userId").equalTo(userId).get().addOnSuccessListener { snapshot ->
            val bills = snapshot.children.mapNotNull { it.getValue(MonthlyBill::class.java) }
            callback(bills)
        }
    }

    fun updateMonthlyBill(bill: MonthlyBill) {
        if (bill.billId.isNotEmpty()) {
            billsRef.child(bill.billId).setValue(bill)
        }
    }


    fun getBillById(billId: String, callback: (MonthlyBill?) -> Unit) {
        billsRef.child(billId).get().addOnSuccessListener { snapshot ->
            val bill = snapshot.getValue(MonthlyBill::class.java)
            callback(bill)
        }
    }
    fun getMonthlyBillByMonth(userId: String, month: String, callback: (MonthlyBill?) -> Unit) {
        billsRef.orderByChild("userId_month").equalTo("$userId$month").limitToFirst(1).get()
            .addOnSuccessListener { snapshot ->
                val bill = snapshot.children.firstOrNull()?.getValue(MonthlyBill::class.java)
                callback(bill)
            }.addOnFailureListener {
                Log.e("MonthlyBillRepository", "Failed to fetch bill for $userId in $month", it)
                callback(null)
            }
    }
}