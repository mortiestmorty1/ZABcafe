package com.szabist.zabapp1.ui.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.CartItem
import com.szabist.zabapp1.viewmodel.CartViewModel

@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel = viewModel()) {
    val cartItems by cartViewModel.cartItems.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        if (cartItems.isEmpty()) {
            // Show a message when the cart is empty
            Text("Your cart is empty", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 20.dp, bottom = 20.dp))
        } else {
            LazyColumn {
                items(cartItems) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        increaseQuantity = { cartViewModel.increaseItemQuantity(cartItem.menuItem) },
                        decreaseQuantity = { cartViewModel.decreaseItemQuantity(cartItem.menuItem) },
                        removeItem = { cartViewModel.removeItemFromCart(cartItem.menuItem) }
                    )
                }
            }
            val subtotal = cartItems.sumOf { it.quantity * it.menuItem.price }
            Text("Subtotal: $$subtotal", style = MaterialTheme.typography.titleSmall)
            Button(
                onClick = {
                    navController.navigate("checkout")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Proceed to Checkout")
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    increaseQuantity: () -> Unit,
    decreaseQuantity: () -> Unit,
    removeItem: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            IconButton(onClick = decreaseQuantity) {
                Icon(Icons.Filled.Clear, contentDescription = "Decrease quantity")
            }
            Text("${cartItem.quantity} x ${cartItem.menuItem.name}: ${cartItem.quantity * cartItem.menuItem.price}")
            IconButton(onClick = increaseQuantity) {
                Icon(Icons.Filled.Add, contentDescription = "Increase quantity")
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = removeItem) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove item")
            }
        }
    }
}