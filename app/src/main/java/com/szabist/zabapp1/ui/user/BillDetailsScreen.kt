package com.szabist.zabapp1.ui.user


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.MonthlyBillViewModel


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailsScreen(
    navController: NavController,
    billId: String,
    viewModel: MonthlyBillViewModel
) {
    LaunchedEffect(billId) {
        viewModel.getBillById(billId)
    }

    val bill by viewModel.selectedBill.collectAsState()
    var showPaymentOptionsDialog by remember { mutableStateOf(false) }

    // Trigger a recomposition when the selected bill changes
    LaunchedEffect(bill) {
        showPaymentOptionsDialog = false  // Reset dialog state on bill update
    }

    bill?.let { bill ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Bill Details for ${bill.month}") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("Bill ID: ${bill.billId}", style = MaterialTheme.typography.headlineMedium)
                Text("User ID: ${bill.userId}", style = MaterialTheme.typography.bodyMedium)
                Text("Month: ${bill.month}", style = MaterialTheme.typography.bodyMedium)
                Text("Total Amount: $${bill.amount}", style = MaterialTheme.typography.titleLarge)
                Text("Paid: ${bill.paid}", style = MaterialTheme.typography.bodyMedium)
                Text("Partially Paid: ${bill.partialPaid}", style = MaterialTheme.typography.bodyMedium)
                Text("Partial Payment Amount: $${bill.partialPaymentAmount}", style = MaterialTheme.typography.bodyMedium)
                Text("Arrears: $${bill.arrears}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))

                if (bill.orders.isNotEmpty()) {
                    bill.orders.forEach { order ->
                        ExpandableOrderItem(order)
                    }
                } else {
                    Text("No orders associated with this bill.", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(16.dp))
                when {
                    bill.paid -> {
                        Text("Paid", color = Color.Green)
                        Button(onClick = {}, enabled = false) {
                            Text("Paid")
                        }
                    }
                    bill.partialPaid && !bill.adminApproved -> {
                        Text("Partially Paid - Waiting for Admin Approval", color = Color.Gray)
                        Button(onClick = {}, enabled = false) {
                            Text("Partially Paid")
                        }
                    }
                    bill.partialPaid && bill.adminApproved -> {
                        Text("Partially Paid - Admin Approved", color = Color.Green)
                        Button(onClick = {}, enabled = false) {
                            Text("Partially Paid")
                        }
                    }
                    else -> {
                        Button(
                            onClick = { showPaymentOptionsDialog = true },
                            enabled = bill.ordersMade
                        ) {
                            Text("Make Payment")
                        }
                    }
                }

                // Payment options dialog
                if (showPaymentOptionsDialog) {
                    PaymentOptionsDialog(
                        billTotal = bill.amount,
                        onDismiss = { showPaymentOptionsDialog = false },
                        onFullPaymentConfirm = {
                            viewModel.handleFullPayment(bill.billId) {
                                showPaymentOptionsDialog = false // Close the dialog
                            }
                        },
                        onPartialPaymentConfirm = { partialAmount ->
                            viewModel.handlePartialPayment(bill.billId, partialAmount) {
                                showPaymentOptionsDialog = false // Close the dialog
                            }
                        }
                    )
                }
            }
        }
    } ?: Text("Bill not found", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun PaymentOptionsDialog(
    billTotal: Double,
    onDismiss: () -> Unit,
    onFullPaymentConfirm: () -> Unit,
    onPartialPaymentConfirm: (Double) -> Unit
) {
    var showPartialPaymentDialog by remember { mutableStateOf(false) }

    if (showPartialPaymentDialog) {
        // Dialog for selecting the partial payment percentage
        PartialPaymentDialog(
            billTotal = billTotal,
            onDismiss = { showPartialPaymentDialog = false },
            onConfirm = { percentage ->
                onPartialPaymentConfirm(billTotal * (percentage / 100.0))
            }
        )
    } else {
        // Main dialog for choosing full or partial payment
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Select Payment Type") },
            text = {
                Column {
                    Button(
                        onClick = {
                            onFullPaymentConfirm()
                            onDismiss()
                        }
                    ) {
                        Text("Full Payment")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { showPartialPaymentDialog = true }
                    ) {
                        Text("Partial Payment")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PartialPaymentDialog(
    billTotal: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    val options = listOf(25.0, 50.0, 75.0) // Percent options
    var selectedOption by remember { mutableStateOf(options.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Partial Payment Amount") },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = option }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { selectedOption = option }
                        )
                        Text("${option.toInt()}% of $billTotal = ${String.format("%.2f", billTotal * (option / 100))}")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedOption) }
            ) {
                Text("Confirm Payment")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun ExpandableOrderItem(order: Order) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.clickable { expanded = !expanded }) {
        Text("Order ID: ${order.id} - Total: $${order.totalAmount}", style = MaterialTheme.typography.bodyMedium)
        if (expanded) {
            order.items.forEach { menuItem ->
                Text("Item: ${menuItem.name} - Price: $${menuItem.price}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}