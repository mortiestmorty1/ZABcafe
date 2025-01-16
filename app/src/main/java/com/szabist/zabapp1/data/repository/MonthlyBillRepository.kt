package com.szabist.zabapp1.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.szabist.zabapp1.data.firebase.FirebaseService
import com.szabist.zabapp1.data.firebase.NotificationHelper
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
            Log.d("Repository", "Fetched all bills: $bills")
            callback(bills)
        }.addOnFailureListener {
            Log.e("Repository", "Failed to fetch all bills", it)
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
    fun updateMonthlyBill(bill: MonthlyBill, context: Context, callback: (Boolean) -> Unit) {
        if (bill.billId.isNotEmpty()) {
            billsRef.child(bill.billId).setValue(bill).addOnSuccessListener {
                // Send notification for bill update
                val notificationHelper = NotificationHelper(context)
                notificationHelper.sendNotification(
                    title = "Bill Update",
                    message = "Your bill for ${bill.month} has been updated.",
                    type = "bill",
                    id = bill.billId
                )
                callback(true)
            }.addOnFailureListener {
                callback(false)
            }
        } else {
            callback(false)
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
        bill.paid = true // Marking as paid but partialPaid will indicate it's only partially
        bill.partialPaymentAmount = partialPayment
        bill.arrears = bill.amount - partialPayment // Calculate the remaining amount
        billsRef.child(bill.billId).setValue(bill).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    // Carry over arrears to the next month's bill


    // Fetch a bill by userId and month
    fun getMonthlyBillByMonth(userId: String, month: String, callback: (MonthlyBill?) -> Unit) {
        val userIdMonth = "${userId.trim()}_${month.trim()}"

        billsRef.orderByChild("userIdMonth").equalTo(userIdMonth).limitToFirst(1).get()
            .addOnSuccessListener { snapshot ->
                val bill = snapshot.children.firstOrNull()?.getValue(MonthlyBill::class.java)
                callback(bill)
            }.addOnFailureListener {
                Log.e("Repository", "Error fetching bill for $userIdMonth", it)
                callback(null)
            }
    }
    fun getMonthlyBillsForUserAndMonth(userId: String, yearMonth: String, callback: (List<MonthlyBill>) -> Unit) {
        val userIdMonth = "${userId.trim()}_${yearMonth.trim()}"  // Ensure format is "userId_YYYY-MM"

        Log.d("MonthlyBillRepository", "Querying for userIdMonth: $userIdMonth")

        billsRef.orderByChild("userIdMonth").equalTo(userIdMonth).get()
            .addOnSuccessListener { snapshot ->
                val bills = snapshot.children.mapNotNull { it.getValue(MonthlyBill::class.java) }
                Log.d("MonthlyBillRepository", "Fetched bills for userIdMonth $userIdMonth: $bills")
                callback(bills)
            }
            .addOnFailureListener { exception ->
                Log.e("MonthlyBillRepository", "Failed to fetch bills for $userIdMonth", exception)
                callback(emptyList())
            }
    }
}