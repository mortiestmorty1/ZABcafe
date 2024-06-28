package com.szabist.zabcafe.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.szabist.zabcafe.model.Order
import com.szabist.zabcafe.viewmodel.CartViewModel

@Composable
fun CartScreen(cartViewModel: CartViewModel, onCheckout: () -> Unit) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalCost by cartViewModel.totalCost.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Cart",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(cartItems) { cartItem ->
                CartItemComponent(
                    cartItem = cartItem,
                    onRemove = { cartViewModel.removeItemFromCart(cartItem.itemId) },
                    onUpdateQuantity = { newQuantity -> cartViewModel.updateCartItemQuantity(cartItem.itemId, newQuantity) }
                )
            }
        }

        Text(
            text = "Total: $${totalCost}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.End).padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onCheckout() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Proceed to Checkout")
        }
    }
}

@Composable
fun CartItemComponent(cartItem: Order.OrderItem, onRemove: () -> Unit, onUpdateQuantity: (Int) -> Unit) {
    var quantity by remember { mutableIntStateOf(cartItem.quantity) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(cartItem.itemName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Quantity:", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = quantity.toString(),
                    onValueChange = {
                        quantity = it.toIntOrNull() ?: quantity
                        onUpdateQuantity(quantity)
                    },
                    modifier = Modifier.width(60.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onRemove() }) {
                    Text("Remove")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Price: $${cartItem.price * quantity}", fontSize = 18.sp)
        }
    }
}