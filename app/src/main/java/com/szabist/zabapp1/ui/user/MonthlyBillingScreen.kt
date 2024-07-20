package com.szabist.zabapp1.ui.user

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.MonthlyBill
import com.szabist.zabapp1.viewmodel.MonthlyBillViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyBillingScreen(navController: NavController, userId: String, monthlyBillViewModel: MonthlyBillViewModel = viewModel()) {
    LaunchedEffect(userId) {
        monthlyBillViewModel.loadBillsForUser(userId)
    }

    val bills by monthlyBillViewModel.monthlyBills.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Monthly Bills") }) },
        content = { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {
                if (bills.isEmpty()) {
                    item { Text("No orders made this month.", modifier = Modifier.padding(16.dp)) }
                } else {
                    items(bills.sortedByDescending { it.month }) { bill ->
                        BillItem(bill, navController)
                    }
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillItem(bill: MonthlyBill, navController: NavController) {
    val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = {
            navController.navigate("bill_details/${bill.billId}")
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Month: ${bill.month}")
                Text("Total: $${bill.amount}", style = MaterialTheme.typography.bodyLarge)
            }

            // Check if it's the current month
            if (bill.month == currentMonth) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Current Month",
                    tint = Color.Blue
                )
            }

            Icon(
                imageVector = if (bill.paid) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                contentDescription = if (bill.paid) "Paid" else "Unpaid",
                tint = if (bill.paid) Color.Green else Color.Red
            )

            if (!bill.paid && bill.flaggedAsPaid) {
                Button(
                    onClick = { /* Navigate to verification screen or directly handle verification */ },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Verify Payment")
                }
            }
        }
    }
}