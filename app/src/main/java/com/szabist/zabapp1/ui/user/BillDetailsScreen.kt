package com.szabist.zabapp1.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.MonthlyBillViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailsScreen(navController: NavController, billId: String, viewModel: MonthlyBillViewModel) {
    // Trigger loading the bill details when billId changes
    LaunchedEffect(billId) {
        viewModel.getBillById(billId)
    }

    // Collecting the latest bill details
    val bill by viewModel.selectedBill.collectAsState()

    bill?.let {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Bill Details for ${it.month}") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("Bill ID: ${it.billId}", style = MaterialTheme.typography.headlineMedium)
                Text("Total: $${it.amount}", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                it.orders.forEach { order ->
                    OrderDetailsItem(order)
                    Divider()
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Grand Total: $${it.amount}", style = MaterialTheme.typography.titleLarge)
                    if (!it.paid) {
                        if (it.flaggedAsPaid) {
                            Icon(Icons.Filled.Warning, contentDescription = "Pending Verification", tint = Color.Yellow)
                        } else {
                            Button(onClick = { viewModel.flagBillAsPaid(it.billId) }) {
                                Text("Flag as Paid")
                            }
                        }
                    } else {
                        Icon(Icons.Filled.Check, contentDescription = "Verified Payment", tint = Color.Green)
                    }
                }
            }
        }
    } ?: Text("Bill not found", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun OrderDetailsItem(order: Order) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Order ID: ${order.id}", style = MaterialTheme.typography.bodyMedium)
            Text("Amount: $${order.totalAmount}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}