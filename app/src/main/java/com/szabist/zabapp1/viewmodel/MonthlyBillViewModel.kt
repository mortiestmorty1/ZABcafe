package com.szabist.zabapp1.viewmodel
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabapp1.data.firebase.NotificationHelper
import com.szabist.zabapp1.data.model.MonthlyBill
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.data.repository.MonthlyBillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class MonthlyBillViewModel : ViewModel() {
    private val monthlyBillRepository = MonthlyBillRepository()
    private val _selectedBill = MutableStateFlow<MonthlyBill?>(null)
    val selectedBill: StateFlow<MonthlyBill?> = _selectedBill
    private val _monthlyBills = MutableStateFlow<List<MonthlyBill>>(emptyList())
    val monthlyBills: StateFlow<List<MonthlyBill>> = _monthlyBills

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleOrder(
        order: Order,
        userId: String,
        orderViewModel: OrderViewModel,
        context: Context,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
            order.timestamp = Date()
            monthlyBillRepository.getMonthlyBillByMonth(userId, month) { existingBill ->
                if (existingBill != null) {
                    // Update existing bill
                    existingBill.orders += order
                    existingBill.amount += order.totalAmount
                    updateMonthlyBill(existingBill, context, callback)
                } else {
                    // Create a new bill
                    val newBill = MonthlyBill(
                        userId = userId,
                        month = month,
                        amount = order.totalAmount,
                        orders = listOf(order),
                        ordersMade = true
                    )
                    addMonthlyBill(newBill, callback)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleFullPayment(billId: String, context: Context, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.getBillById(billId) { bill ->
                bill?.let {
                    it.paid = true
                    it.partialPaid = false
                    monthlyBillRepository.updateMonthlyBill(it, context) { success -> // Pass context
                        if (success) {
                            _monthlyBills.value = _monthlyBills.value.map { b -> if (b.billId == billId) it else b }

                            // Notify user about full payment
                            val notificationHelper = NotificationHelper(context)
                            notificationHelper.sendNotification(
                                title = "Bill Payment",
                                message = "Thank you for paying your bill for ${it.month}.",
                                type = "bill",
                                id = billId
                            )
                        }
                        callback(success)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handlePartialPayment(
        billId: String,
        partialPaymentAmount: Double,
        context: Context,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.getBillById(billId) { bill ->
                bill?.let {
                    it.partialPaid = true
                    it.paid = true
                    it.partialPaymentAmount = partialPaymentAmount
                    it.arrears = it.amount - partialPaymentAmount
                    monthlyBillRepository.updateMonthlyBill(it, context) { success -> // Pass context
                        if (success) {
                            _monthlyBills.value = _monthlyBills.value.map { b -> if (b.billId == billId) it else b }

                            // Notify user about partial payment
                            val notificationHelper = NotificationHelper(context)
                            notificationHelper.sendNotification(
                                title = "Partial Payment",
                                message = "Your partial payment of PKR $partialPaymentAmount for ${it.month} has been received. Remaining balance: PKR ${it.arrears}.",
                                type = "bill",
                                id = billId
                            )
                        }
                        callback(success)
                    }
                }
            }
        }
    }


    fun getBillById(billId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.getBillById(billId) { bill ->
                _selectedBill.value = bill
            }
        }
    }

    fun loadBillsForUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.getBillsForUser(userId) { bills ->
                _monthlyBills.value = bills
            }
        }
    }

    fun loadAllBills() {
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.getAllBills { bills ->
                _monthlyBills.value = bills
            }
        }
    }

    private fun addMonthlyBill(bill: MonthlyBill, callback: (Boolean) -> Unit) {
        monthlyBillRepository.addMonthlyBill(bill, callback)
    }

    private fun updateMonthlyBill(bill: MonthlyBill, context: Context, callback: (Boolean) -> Unit) {
        monthlyBillRepository.updateMonthlyBill(bill, context) { success ->
            if (success) {
                loadBillsForUser(bill.userId)
            }
            callback(success)
        }
    }



    fun loadBillsForUserAndMonth(userId: String, month: String) {
        Log.d("MonthlyBillViewModel", "Loading bills for userId: $userId and month: $month")

        monthlyBillRepository.getMonthlyBillsForUserAndMonth(userId, month) { bills ->
            Log.d("MonthlyBillViewModel", "Received bills: $bills")
            _monthlyBills.value = bills
        }
    }
}

