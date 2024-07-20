package com.szabist.zabapp1.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrdersScreen(navController: NavController, orderViewModel: OrderViewModel = viewModel()) {
    val orders by orderViewModel.orders.collectAsState()

    LaunchedEffect(Unit) {
        orderViewModel.loadAllOrders()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("View Orders") }) }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(orders, key = { it.id }) { order ->
                OrderAdminItem(order, onStatusChange = { newStatus ->
                    orderViewModel.updateOrderStatus(order.id, newStatus)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderAdminItem(order: Order, onStatusChange: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        onClick = {
            // Optionally add navigation to a detailed order status update screen
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order ID: ${order.id}", style = MaterialTheme.typography.bodyLarge)
            Text("Total: $${order.totalAmount}", style = MaterialTheme.typography.bodyLarge)
            Text("Status: ${order.status}", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OrderStatusButton("Accept", onStatusChange)
                OrderStatusButton("Prepare", onStatusChange)
                OrderStatusButton("Ready for Pickup", onStatusChange)
                OrderStatusButton("Complete", onStatusChange)
            }
        }
    }
}

@Composable
fun OrderStatusButton(status: String, onStatusChange: (String) -> Unit) {
    Button(onClick = { onStatusChange(status) }) {
        Text(status)
    }
}