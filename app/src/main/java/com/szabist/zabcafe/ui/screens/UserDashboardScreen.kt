package com.szabist.zabcafe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabcafe.repository.CartRepository
import com.szabist.zabcafe.viewmodel.CartViewModel
import com.szabist.zabcafe.viewmodel.CartViewModelFactory
import com.szabist.zabcafe.viewmodel.UserDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboard(
    navController: NavController,
    userDashboardViewModel: UserDashboardViewModel,
    userId: String
) {
    if (userId == null) {

        Text("Error: User not logged in. Please log in.")
        return
    }

    val cartViewModel: CartViewModel = viewModel(factory = CartViewModelFactory(userId, CartRepository()))

    val cartItemCount by cartViewModel.cartItemCount.collectAsState()
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("User Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate("cart") }) {
                        BadgeBox(cartItemCount)
                    }
                }
            )
            Column(
                modifier = Modifier
                    .padding(PaddingValues(16.dp))
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Welcome to ZAB Cafe!", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = { navController.navigate("viewBills") }) {
                    Text("View Bills")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navController.navigate("menu") }) {
                    Text("View Menu")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navController.navigate("pastOrders") }) {
                    Text("Past Orders")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navController.navigate("orderStatus") }) {
                    Text("Order Status")
                }
            }
        }
    }
}

@Composable
fun BadgeBox(cartItemCount: Int) {
    Box(contentAlignment = Alignment.TopEnd) {
        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
        if (cartItemCount > 0) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .padding(top = 2.dp, end = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cartItemCount.toString(),
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimary),
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}