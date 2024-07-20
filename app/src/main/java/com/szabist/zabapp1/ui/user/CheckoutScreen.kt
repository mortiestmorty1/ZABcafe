package com.szabist.zabapp1.ui.user

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.CartViewModel
import com.szabist.zabapp1.viewmodel.OrderViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CheckoutScreen(
    navController: NavController,
    userId: String,
    cartViewModel: CartViewModel = viewModel(),
    orderViewModel: OrderViewModel = viewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val subtotal = cartItems.sumOf { it.quantity * it.menuItem.price }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Checkout", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(cartItems) { cartItem ->
                Text("${cartItem.quantity} x ${cartItem.menuItem.name} - $${cartItem.quantity * cartItem.menuItem.price}")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Total: $$subtotal", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val order = Order(
                    userId = userId,
                    items = cartItems.map { it.menuItem },
                    totalAmount = subtotal,
                    status = "pending"
                )
                orderViewModel.addOrder(order, userId) { completedOrder ->
                    navController.navigate("order_status") {
                        popUpTo("menu") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Place Order")
        }
    }
}