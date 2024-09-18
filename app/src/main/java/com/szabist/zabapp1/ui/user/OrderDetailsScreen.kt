package com.szabist.zabapp1.ui.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(navController: NavController, orderId: String, orderViewModel: OrderViewModel = viewModel()) {
    // Trigger loading the order when OrderDetailsScreen is composed or the orderId changes.
    LaunchedEffect(orderId) {
        orderViewModel.loadOrderById(orderId)
    }

    // Collect the current order as state.
    val order by orderViewModel.currentOrder.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { padding ->
            order?.let {
                OrderDetailsContent(order = it, navController = navController)
            } ?: run {
                Text("Order not found", modifier = Modifier.padding(padding).padding(16.dp))
            }
        }
    )
}

@Composable
fun OrderDetailsContent(order: Order, navController: NavController) {
    Column(modifier = Modifier
        .padding(52.dp)
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())) { // Ensures the content is scrollable if overflow occurs
        Spacer(Modifier.height(16.dp))
        Text(
            "Order ID: ${order.id}",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(Modifier.height(8.dp))

        Text(
            "Total Amount: ${order.totalAmount}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Text(
            "Status: ${order.status}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        order.items.forEach { item ->
            Text(
                "Item: ${item.name} - ${item.price}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}