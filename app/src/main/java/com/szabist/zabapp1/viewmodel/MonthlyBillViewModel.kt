package com.szabist.zabapp1.viewmodel
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    fun handleOrder(order: Order, userId: String, orderViewModel: OrderViewModel, callback: (Boolean, String?) -> Unit) {
        order.timestamp = Date()
        orderViewModel.addOrder(order, onSuccess = { success, orderId ->
            if (success && orderId != null) {
                val month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                viewModelScope.launch(Dispatchers.IO) {
                    monthlyBillRepository.getMonthlyBillByMonth(userId, month) { existingBill ->
                        if (existingBill != null) {
                            existingBill.orders += order
                            existingBill.amount += order.totalAmount
                            existingBill.ordersMade = true
                            updateMonthlyBill(existingBill) {
                                callback(true, orderId)
                            }
                        } else {
                            val newBill = MonthlyBill(
                                userId = userId,
                                month = month,
                                amount = order.totalAmount,
                                orders = listOf(order),
                                ordersMade = true
                            )
                            addMonthlyBill(newBill) {
                                callback(true, orderId)
                            }
                        }
                    }
                }
            } else {
                callback(false, null)
            }
        }, onFailure = {
            callback(false, null)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleFullPayment(billId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.getBillById(billId) { bill ->
                bill?.let {
                    it.paid = true
                    it.partialPaid = false
                    monthlyBillRepository.updateMonthlyBill(it) { success ->
                        if (success) _monthlyBills.value = _monthlyBills.value.map { b -> if (b.billId == billId) it else b }
                        callback(success)
                    }
                }
            }
        }
    }

    // Handle partial payment
    @RequiresApi(Build.VERSION_CODES.O)
    fun handlePartialPayment(billId: String, partialPaymentAmount: Double, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.getBillById(billId) { bill ->
                bill?.let {
                    it.partialPaid = true
                    it.paid = true
                    it.partialPaymentAmount = partialPaymentAmount
                    it.arrears = it.amount - partialPaymentAmount
                    monthlyBillRepository.updateMonthlyBill(it) { success ->
                        if (success) _monthlyBills.value = _monthlyBills.value.map { b -> if (b.billId == billId) it else b }
                        callback(success)
                    }
                }
            }
        }
    }


    fun carryOverArrearsToNextBill(userId: String, arrears: Double, month: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.getMonthlyBillByMonth(userId, month) { existingBill ->
                if (existingBill != null) {
                    existingBill.amount += arrears  // Add arrears to the existing bill amount
                    existingBill.arrears += arrears  // Update the arrears field
                    monthlyBillRepository.updateMonthlyBill(existingBill, callback)
                } else {
                    // If there is no bill for the next month, create a new one with the arrears
                    val newBill = MonthlyBill(
                        userId = userId,
                        month = month,
                        amount = arrears,
                        arrears = arrears,
                        ordersMade = false  // No orders yet for the new month
                    )
                    addMonthlyBill(newBill, callback)
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

    private fun updateMonthlyBill(bill: MonthlyBill, callback: (Boolean) -> Unit) {
        monthlyBillRepository.updateMonthlyBill(bill) {
            callback(true)
            loadBillsForUser(bill.userId)
        }
    }



    fun flagBillForAdminApproval(bill: MonthlyBill, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.updateMonthlyBill(bill) {
                callback(true)
            }
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

