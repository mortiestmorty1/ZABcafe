package com.szabist.zabapp1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabapp1.data.model.MonthlyBill
import com.szabist.zabapp1.data.repository.MonthlyBillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MonthlyBillViewModel : ViewModel() {
    private val monthlyBillRepository = MonthlyBillRepository()
    private val _selectedBill = MutableStateFlow<MonthlyBill?>(null)
    val selectedBill: StateFlow<MonthlyBill?> = _selectedBill
    private val _monthlyBills = MutableStateFlow<List<MonthlyBill>>(emptyList())
    val monthlyBills: StateFlow<List<MonthlyBill>> = _monthlyBills

    fun addMonthlyBill(bill: MonthlyBill) {
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.addMonthlyBill(bill) { success ->
                if (success) {
                    loadBillsForUser(bill.userId)
                }
            }
        }
    }

    fun loadBillsForUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.getMonthlyBills(userId) { bills ->
                _monthlyBills.value = bills
            }
        }
    }

    fun updateMonthlyBill(bill: MonthlyBill) {
        viewModelScope.launch(Dispatchers.IO) {
            monthlyBillRepository.updateMonthlyBill(bill)
            loadBillsForUser(bill.userId)  // Optionally reload the bills
        }
    }
    fun flagBillAsPaid(billId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Assuming the repository has a method to fetch a bill by ID and update it
            monthlyBillRepository.getBillById(billId) { bill ->
                bill?.let {
                    it.flaggedAsPaid = true
                    monthlyBillRepository.updateMonthlyBill(it)
                }
            }
        }
    }

    fun getBillById(billId: String) {
        viewModelScope.launch {
            monthlyBillRepository.getBillById(billId) { bill ->
                _selectedBill.value = bill
            }
        }
    }
}