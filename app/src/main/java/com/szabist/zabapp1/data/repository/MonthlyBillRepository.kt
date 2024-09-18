package com.szabist.zabapp1.data.repository

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.szabist.zabapp1.data.firebase.FirebaseService
import com.szabist.zabapp1.data.model.MonthlyBill

class MonthlyBillRepository {
    private val billsRef: DatabaseReference = FirebaseService.getDatabaseReference("monthly_bills")

    // Add a new bill with the user's orders
    fun addMonthlyBill(bill: MonthlyBill, callback: (Boolean) -> Unit) {
        val key = billsRef.push().key ?: return
        bill.billId = key
        bill.userIdMonth = "${bill.userId.trim()}_${bill.month.trim()}"
        billsRef.child(key).setValue(bill).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    // Retrieve all bills
    fun getAllBills(callback: (List<MonthlyBill>) -> Unit) {
        billsRef.get().addOnSuccessListener { snapshot ->
            val bills = snapshot.children.mapNotNull { it.getValue(MonthlyBill::class.java) }
            callback(bills)
        }.addOnFailureListener {
            Log.e("MonthlyBillRepository", "Failed to fetch all bills", it)
            callback(emptyList())
        }
    }

    // Retrieve bills for a specific user
    fun getBillsForUser(userId: String, callback: (List<MonthlyBill>) -> Unit) {
        getAllBills { allBills ->
            val userBills = allBills.filter { it.userId == userId }
            callback(userBills)
        }
    }

    // Update an existing bill
    fun updateMonthlyBill(bill: MonthlyBill, callback: (Boolean) -> Unit) {
        if (bill.billId.isNotEmpty()) {
            billsRef.child(bill.billId).setValue(bill).addOnSuccessListener {
                callback(true)
            }.addOnFailureListener {
                callback(false)
            }
        }
    }

    // Fetch a bill by its ID
    fun getBillById(billId: String, callback: (MonthlyBill?) -> Unit) {
        billsRef.child(billId).get().addOnSuccessListener { snapshot ->
            val bill = snapshot.getValue(MonthlyBill::class.java)
            callback(bill)
        }
    }

    // Update bill for partial payment
    fun updateMonthlyBillWithPartialPayment(bill: MonthlyBill, partialPayment: Double, callback: (Boolean) -> Unit) {
        bill.partialPaid = true
        bill.partialPaymentAmount = partialPayment
        bill.arrears = bill.amount - partialPayment // Calculate the remaining amount
        billsRef.child(bill.billId).setValue(bill).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    // Carry over arrears to the next month's bill
    fun carryOverArrearsToNewBill(userId: String, arrears: Double, newMonth: String, callback: (Boolean) -> Unit) {
        getMonthlyBillByMonth(userId, newMonth) { existingBill ->
            if (existingBill != null) {
                existingBill.amount += arrears
                updateMonthlyBill(existingBill, callback)
            } else {
                val newBill = MonthlyBill(
                    userId = userId,
                    month = newMonth,
                    amount = arrears,
                    arrears = arrears
                )
                addMonthlyBill(newBill, callback)
            }
        }
    }

    // Fetch a bill by userId and month
    fun getMonthlyBillByMonth(userId: String, month: String, callback: (MonthlyBill?) -> Unit) {
        val userIdMonth = "${userId.trim()}_${month.trim()}"
        billsRef.orderByChild("userIdMonth").equalTo(userIdMonth).limitToFirst(1).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.children.any()) {
                    val bill = snapshot.children.first().getValue(MonthlyBill::class.java)
                    callback(bill)
                } else {
                    Log.d("Repository", "No existing bill found for $userId in $month")
                    callback(null)
                }
            }.addOnFailureListener {
                Log.e("Repository", "Error fetching bill for $userIdMonth", it)
                callback(null)
            }
    }
    fun approveMonthlyBill(bill: MonthlyBill, callback: (Boolean) -> Unit) {
        bill.adminApproved = true
        billsRef.child(bill.billId).setValue(bill).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }
}