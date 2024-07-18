package com.szabist.zabapp1.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.MenuItem
import com.szabist.zabapp1.viewmodel.MenuViewModel

@Composable
fun EditMenuItemScreen(navController: NavController, menuItemId: String, menuViewModel: MenuViewModel = viewModel()) {
    // State for storing menu item details
    var menuItem by remember { mutableStateOf<MenuItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch the menu item details
    LaunchedEffect(menuItemId) {
        menuViewModel.getMenuItemById(menuItemId) { item ->
            menuItem = item
            isLoading = false
        }
    }

    if (isLoading) {
        //CircularProgressIndicator() // Show loading indicator while data is being fetched
    } else {
        menuItem?.let { item ->
            EditMenuItemForm(item, navController, menuViewModel)
        }
    }
}

@Composable
fun EditMenuItemForm(menuItem: MenuItem, navController: NavController, menuViewModel: MenuViewModel) {
    var name by remember { mutableStateOf(menuItem.name) }
    var description by remember { mutableStateOf(menuItem.description) }
    var price by remember { mutableStateOf(menuItem.price.toString()) }
    var available by remember { mutableStateOf(menuItem.available) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )
        TextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = available,
                onCheckedChange = { available = it }
            )
            Text("Available")
        }
        Button(
            onClick = {
                val updatedItem = menuItem.copy(name = name, description = description, price = price.toDouble(), available = available)
                menuViewModel.updateMenuItem(updatedItem)
                navController.popBackStack() // Navigate back after update
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update")
        }
    }
}