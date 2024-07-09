package com.szabist.zabcafe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.szabist.zabcafe.model.CartItem
import com.szabist.zabcafe.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController, cartViewModel: CartViewModel) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val total = cartViewModel.total

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (cartItems.isEmpty()) {
                Text(
                    "Your cart is empty",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                cartItems.forEach { item ->
                    CheckoutItemView(item)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Total: $total", style = MaterialTheme.typography.titleLarge)
                Button(
                    onClick = {
                        // Add order logic
                        cartViewModel.addOrder()
                        navController.navigate("userDashboard") {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Add to Bill")
                }
            }
        }
    }
}

@Composable
fun CheckoutItemView(item: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("${item.name} x${item.quantity}")
        Text("Price: ${item.price * item.quantity}")
    }
}