package com.szabist.zabapp1.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(
    navController: NavController,
    menuViewModel: MenuViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val menuItems by menuViewModel.menuItems.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    val filteredMenuItems = menuItems.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                (menuViewModel.getCategoryNameById(it.categoryId)
                    ?.contains(searchQuery, ignoreCase = true) == true)
    }
    Column(modifier = Modifier.padding(16.dp)) {
        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Menu Items") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredMenuItems.size) { index ->
                val menuItem = filteredMenuItems[index]
                MenuItemCard(
                    menuItem = menuItem,
                    onAddToCart = { quantity ->
                        coroutineScope.launch {
                            cartViewModel.addToCart(menuItem, quantity)
                        }
                    },
                    onClick = {
                        navController.navigate("menu_item_details/${menuItem.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    onAddToCart: (Int) -> Unit,
    onClick: () -> Unit,
    menuViewModel: MenuViewModel = viewModel()
) {
    val categoryName = menuViewModel.getCategoryNameById(menuItem.categoryId) // Fetch category name
    val showDialog = remember { mutableStateOf(false) }
    val quantity = remember { mutableStateOf(1) }

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
            if (menuItem.imageUrl != null) {
                Image(
                    painter = rememberImagePainter(menuItem.imageUrl),
                    contentDescription = menuItem.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Text("Name: ${menuItem.name}", style = MaterialTheme.typography.titleLarge)
            Text("Category: $categoryName", style = MaterialTheme.typography.bodyMedium) // Display category
            Text("Description: ${menuItem.description}", style = MaterialTheme.typography.bodyLarge)
            Text("Price: PKR ${menuItem.price}", style = MaterialTheme.typography.bodyLarge)
            Text(if (menuItem.available) "Available" else "Not Available", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showDialog.value = true }) {
                Text("Add to Cart")
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Select Quantity") },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { if (quantity.value > 1) quantity.value-- },
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("-")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = quantity.value.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { quantity.value++ },
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("+")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddToCart(quantity.value)
                        showDialog.value = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

