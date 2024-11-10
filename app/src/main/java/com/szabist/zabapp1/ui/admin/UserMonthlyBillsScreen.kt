// UserMonthlyBillsScreen.kt
package com.szabist.zabapp1.ui.admin

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.MonthlyBill
import com.szabist.zabapp1.viewmodel.MonthlyBillViewModel


@Composable
fun UserMonthlyBillsScreen(
    navController: NavController,
    userId: String,
    yearMonth: String,  // Pass month in "YYYY-MM" format
    viewModel: MonthlyBillViewModel = viewModel()
) {
    val bills by viewModel.monthlyBills.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("UserMonthlyBillsScreen", "Calling loadBillsForUserAndMonth with userId: $userId, month: $yearMonth")
        viewModel.loadBillsForUserAndMonth(userId, yearMonth)  // Use yearMonth in "YYYY-MM"
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Bills for $yearMonth", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(bills) { bill ->
                Log.d("UserMonthlyBillsScreen", "Displaying bill: ${bill.billId} with amount ${bill.amount}")
                MonthlyBillRow(bill) {
                    navController.navigate("bill_details/${bill.billId}")
                }
            }
        }
    }
}

@Composable
fun MonthlyBillRow(bill: MonthlyBill, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Bill ID: ${bill.billId}", style = MaterialTheme.typography.bodyMedium)
            Text("Amount: $${bill.amount}", style = MaterialTheme.typography.bodyMedium)
            Text("Paid: ${if (bill.paid) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
