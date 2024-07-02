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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.szabist.zabcafe.model.CartItem
import com.szabist.zabcafe.viewmodel.CartViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel) {
    val cartItems = cartViewModel.cartItems.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Shopping Cart") })
        }
    ) {
        Column(modifier = Modifier.padding(it).padding(16.dp)) {
            if (cartItems.isEmpty()) {
                Text("Your cart is empty", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                cartItems.forEach { item ->
                    CartItemView(item, cartViewModel::updateQuantity, cartViewModel::removeFromCart)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Total: ${cartViewModel.total}", style = MaterialTheme.typography.titleLarge)
                Button(
                    onClick = { /* Proceed to checkout */ },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Checkout")
                }
            }
        }
    }
}




@Composable
fun CartItemView(
    item: CartItem,
    updateQuantity: (String, Int) -> Unit,
    removeFromCart: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("${item.name} x${item.quantity}")
        Button(onClick = { updateQuantity(item.itemId, item.quantity + 1) }) {
            Text("+")
        }
        Button(onClick = { if (item.quantity > 1) updateQuantity(item.itemId, item.quantity - 1) }) {
            Text("-")
        }
        Button(onClick = { removeFromCart(item.itemId) }) {
            Text("Remove")
        }
    }
}