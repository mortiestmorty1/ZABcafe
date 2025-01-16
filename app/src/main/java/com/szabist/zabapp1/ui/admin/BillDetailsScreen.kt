// BillDetailsScreen.kt
package com.szabist.zabapp1.ui.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.MonthlyBillViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailsScreen(
    navController: NavController,
    billId: String,
    viewModel: MonthlyBillViewModel = viewModel()
) {
    val bill by viewModel.selectedBill.collectAsState()
    var showPaymentDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(billId) {
        viewModel.getBillById(billId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bill Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        if (bill != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Bill Details Section
                BillDetailsCard(bill!!)

                Spacer(modifier = Modifier.height(24.dp))

                // Orders Section
                if (bill!!.orders.isNotEmpty()) {
                    Text(
                        text = "Orders",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    bill!!.orders.forEach { order ->
                        ExpandableOrderItem(order)
                    }
                } else {
                    Text(
                        text = "No orders associated with this bill.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Payment Status
                PaymentStatusLabel(paid = bill!!.paid)

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Button
                if (!bill!!.paid) {
                    Button(
                        onClick = { showPaymentDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Mark as Paid", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        } else {
            Text(
                text = "Loading bill details...",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }

        if (showPaymentDialog) {
            PaymentDialog(
                totalAmount = bill!!.amount,
                arrears = bill!!.arrears,
                onConfirmFullPayment = {
                    viewModel.handleFullPayment(billId, context) { success ->
                        if (success) {
                            showPaymentDialog = false
                            viewModel.getBillById(billId) // Refresh UI
                        }
                    }
                },
                onDismiss = { showPaymentDialog = false }
            )
        }
    }
}

@Composable
fun BillDetailsCard(bill: com.szabist.zabapp1.data.model.MonthlyBill) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            DetailItem(label = "Bill ID", value = bill.billId)
            DetailItem(label = "User ID", value = bill.userId)
            DetailItem(label = "Month", value = bill.month)
            DetailItem(
                label = "Total Amount",
                value = "$${bill.amount}",
                valueColor = MaterialTheme.colorScheme.primary
            )
            DetailItem(
                label = "Arrears",
                value = "$${bill.arrears}",
                valueColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onBackground) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor
        )
    }
}

@Composable
fun PaymentStatusLabel(paid: Boolean) {
    val (statusText, color) = if (paid) {
        "Status: Paid" to MaterialTheme.colorScheme.primary
    } else {
        "Status: Unpaid" to MaterialTheme.colorScheme.error
    }

    Text(
        text = statusText,
        style = MaterialTheme.typography.bodyLarge,
        color = color,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ExpandableOrderItem(order: Order) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Order ID: ${order.id}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Total: $${order.totalAmount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                order.items.forEach { menuItem ->
                    Text(
                        text = "Item: ${menuItem.name} - Price: $${menuItem.price}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

