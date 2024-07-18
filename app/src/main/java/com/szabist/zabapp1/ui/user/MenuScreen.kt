package com.szabist.zabapp1.ui.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.MenuItem
import com.szabist.zabapp1.viewmodel.CartViewModel
import com.szabist.zabapp1.viewmodel.MenuViewModel
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(navController: NavController, menuViewModel: MenuViewModel = viewModel(), cartViewModel: CartViewModel = viewModel()) {
    val menuItems by menuViewModel.menuItems.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(menuItems.size) { index ->
            val menuItem = menuItems[index]
            MenuItemCard(
                menuItem = menuItem,
                onAddToCart = {
                    coroutineScope.launch {
                        cartViewModel.addToCart(menuItem)
                    }
                },
                onClick = {
                    navController.navigate("menu_item_details/${menuItem.id}")
                }
            )
        }
    }
}

@Composable
fun MenuItemCard(menuItem: MenuItem, onAddToCart: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Name: ${menuItem.name}", style = MaterialTheme.typography.titleLarge)
            Text("Description: ${menuItem.description}", style = MaterialTheme.typography.bodyLarge)
            Text("Price: $${menuItem.price}", style = MaterialTheme.typography.bodyLarge)
            Text(if (menuItem.available) "Available" else "Not Available", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onAddToCart) {
                Text("Add to Cart")
            }
        }
    }
}