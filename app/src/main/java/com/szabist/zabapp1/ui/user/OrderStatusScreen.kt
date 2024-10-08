package com.szabist.zabapp1.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.OrderViewModel

@Composable
fun OrderStatusScreen(navController: NavController, userId: String, orderViewModel: OrderViewModel = viewModel()) {
    // Load active orders when the screen is first composed or when userId changes
    LaunchedEffect(userId) {
        orderViewModel.loadOrders(userId)
    }

    // Collect the latest list of active orders from the ViewModel
    val orders by orderViewModel.orders.collectAsState()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(orders, key = { it.id }) { order ->
            OrderItem(order, onOrderSelected = { selectedOrder ->
                navController.navigate("order_details/${selectedOrder.id}")
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderItem(order: Order, onOrderSelected: (Order) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = { onOrderSelected(order) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Order ID: ${order.id}", style = MaterialTheme.typography.bodyLarge)
            Text("Total Amount: ${order.totalAmount}", style = MaterialTheme.typography.bodyLarge)
            Text("Status: ${order.status}", style = MaterialTheme.typography.bodyMedium)
            // Add more details as needed
        }
    }
}