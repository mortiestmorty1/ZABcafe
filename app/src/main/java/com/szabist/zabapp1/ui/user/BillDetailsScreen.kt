package com.szabist.zabapp1.ui.user


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.MenuViewModel
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

                // Show payment status
                when {
                    bill.paid && bill.partialPaid -> {
                        Text("Status: Partially Paid", color = Color.Gray)
                    }
                    bill.paid -> {
                        Text("Status: Paid", color = Color.Green)
                    }
                    else -> {
                        Text("Status: Unpaid", color = Color.Red)
                    }
                }
            }
        }
    } ?: Text("Bill not found", style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun ExpandableOrderItem(order: Order, menuViewModel: MenuViewModel = viewModel()) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.clickable { expanded = !expanded }) {
        Text("Order ID: ${order.id} - Total: $${order.totalAmount}", style = MaterialTheme.typography.bodyMedium)
        Text("Order Date: ${order.timestamp}", style = MaterialTheme.typography.bodyMedium)
        if (expanded) {
            order.items.forEach { menuItem ->
                val categoryName = menuViewModel.getCategoryNameById(menuItem.categoryId) // Fetch category name
                Text("Item: ${menuItem.name} - Category: $categoryName - Price: $${menuItem.price}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
