package com.szabist.zabapp1.ui.admin

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.szabist.zabapp1.data.model.MonthlyBill
import com.szabist.zabapp1.viewmodel.MonthlyBillViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ManageMonthlyBillingScreen(
    monthlyBillViewModel: MonthlyBillViewModel = viewModel()
) {
    val bills by monthlyBillViewModel.monthlyBills.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        monthlyBillViewModel.loadAllBills()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Manage Monthly Billing") }) }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            items(bills) { bill ->
                MonthlyBillItem(bill, monthlyBillViewModel, context)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyBillItem(bill: MonthlyBill, monthlyBillViewModel: MonthlyBillViewModel, context: Context) {
    var showPaymentDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("User ID: ${bill.userId}", style = MaterialTheme.typography.bodyMedium)
            Text("Month: ${bill.month}", style = MaterialTheme.typography.bodyMedium)
            Text("Total Amount: $${bill.amount}", style = MaterialTheme.typography.bodyMedium)
            Text("Arrears: $${bill.arrears}", style = MaterialTheme.typography.bodyMedium)
            Text("Paid: ${if (bill.paid) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            if (!bill.paid) {
                Button(onClick = { showPaymentDialog = true }) {
                    Text("Mark as Paid")
                }
            }
        }
    }

    if (showPaymentDialog) {
        PaymentDialog(
            totalAmount = bill.amount,
            arrears = bill.arrears,
            onConfirmFullPayment = {
                monthlyBillViewModel.handleFullPayment(bill.billId, context) { success ->
                    if (success) println("Bill marked as fully paid.")
                }
                showPaymentDialog = false
            },
            onDismiss = { showPaymentDialog = false }
        )
    }
}
