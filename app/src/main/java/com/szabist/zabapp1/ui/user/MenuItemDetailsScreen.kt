package com.szabist.zabapp1.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.szabist.zabapp1.data.model.MenuItem
import com.szabist.zabapp1.viewmodel.CartViewModel
import com.szabist.zabapp1.viewmodel.MenuViewModel


@Composable
fun MenuItemDetailsScreen(
    navController: NavController,
    menuItemId: String,
    menuViewModel: MenuViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val menuItemState = produceState<MenuItem?>(initialValue = null, key1 = menuItemId) {
        menuViewModel.getMenuItemById(menuItemId) { item ->
            value = item
        }
    }

    var isInCart by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) } // State for tracking quantity

    LaunchedEffect(menuItemId, cartViewModel.cartItems.collectAsState().value) {
        isInCart = cartViewModel.isItemInCart(menuItemId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        menuItemState.value?.let { menuItem ->
            MenuItemCard(
                menuItem = menuItem,
                menuViewModel = menuViewModel,
                isInCart = isInCart,
                quantity = quantity, // Pass quantity to the card
                onQuantityChange = { newQuantity -> quantity = newQuantity }, // Handle quantity change
                onAddToCartClicked = {
                    if (!isInCart) {
                        cartViewModel.addToCart(menuItem, quantity) // Pass quantity to the ViewModel
                        isInCart = true
                    }
                },
                onGoToCartClicked = {
                    navController.navigate("cart")
                }
            )
        } ?: Text("Loading...", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    menuViewModel: MenuViewModel,
    isInCart: Boolean,
    quantity: Int, // Quantity parameter
    onQuantityChange: (Int) -> Unit, // Callback for quantity change
    onAddToCartClicked: () -> Unit,
    onGoToCartClicked: () -> Unit
) {
    val categoryName = menuViewModel.getCategoryNameById(menuItem.categoryId) // Get the category name
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (menuItem.imageUrl != null) {
                Image(
                    painter = rememberImagePainter(menuItem.imageUrl),
                    contentDescription = menuItem.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(menuItem.name, style = MaterialTheme.typography.titleLarge)
            Text("Description: ${menuItem.description}", style = MaterialTheme.typography.bodyMedium)
            Text("Category: $categoryName", style = MaterialTheme.typography.bodyMedium)
            Text("Price: $${menuItem.price}", style = MaterialTheme.typography.bodyMedium)
            Text("Status: ${if (menuItem.available) "Available" else "Not Available"}", style = MaterialTheme.typography.bodyMedium)

            // Quantity selector
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }) {
                    Text("-")
                }
                Text(quantity.toString(), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(horizontal = 16.dp))
                Button(onClick = { onQuantityChange(quantity + 1) }) {
                    Text("+")
                }
            }

            Button(
                onClick = {
                    if (!isInCart) {
                        onAddToCartClicked()
                    } else {
                        onGoToCartClicked()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (!isInCart) "Add to Cart" else "Go to Cart")
            }
        }
    }
}
