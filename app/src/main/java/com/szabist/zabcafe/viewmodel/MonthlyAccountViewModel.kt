package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabcafe.model.Transaction
import com.szabist.zabcafe.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MonthlyAccountViewModel(private val accountRepository: AccountRepository) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _totalAmountDue = MutableStateFlow(0.0)
    val totalAmountDue: StateFlow<Double> = _totalAmountDue

    init {
        loadMonthlyAccount()
    }

    private fun loadMonthlyAccount() {
        viewModelScope.launch {
            val transactionsList = accountRepository.fetchTransactions()
            _transactions.value = transactionsList
            calculateTotalAmountDue(transactionsList)
        }
    }

    private fun calculateTotalAmountDue(transactions: List<Transaction>) {
        _totalAmountDue.value = transactions.sumOf { it.amount }
    }
}