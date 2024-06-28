package com.szabist.zabcafe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.szabist.zabcafe.model.Transaction
import com.szabist.zabcafe.viewmodel.MonthlyAccountViewModel

@Composable
fun MonthlyAccountScreen(monthlyAccountViewModel: MonthlyAccountViewModel) {
    val transactions by monthlyAccountViewModel.transactions.collectAsState()
    val totalAmountDue by monthlyAccountViewModel.totalAmountDue.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Monthly Billing",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Total Amount Due: $${totalAmountDue}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItemComponent(transaction)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Handle payment action */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pay Now")
        }
    }
}

@Composable
fun TransactionItemComponent(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.description, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Date: ${transaction.date}", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            Text("Amount: $${transaction.amount}", fontSize = 16.sp)
        }
    }
}