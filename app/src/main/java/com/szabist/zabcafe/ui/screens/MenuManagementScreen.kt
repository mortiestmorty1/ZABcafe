package com.szabist.zabcafe.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.szabist.zabcafe.model.MenuItem
import com.szabist.zabcafe.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen(
    navController: NavController,
    menuViewModel: MenuViewModel
) {
    val menuItems by menuViewModel.menuItems.collectAsState()
    val errorMessage by menuViewModel.errorMessage.collectAsState()
    val isLoading by menuViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Menu Management") }, actions = {
                IconButton(onClick = { navController.navigate("addMenuItem") }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Menu Item")
                }
            })
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn {
                        items(menuItems) { menuItem ->
                            MenuItemRow(menuItem, menuViewModel, navController)
                        }
                    }
                }
                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuItemScreen(navController: NavController, menuViewModel: MenuViewModel) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(true) }
    val categories = listOf("Beverages", "Snacks", "Desserts", "Main Courses")
    var successMessage by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            kotlinx.coroutines.delay(1500)  // Show the message for 1.5 seconds
            showSuccessMessage = false  // Hide the message after the delay
            successMessage = ""  // Clear the success message text
        }
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Text("Select Category", style = MaterialTheme.typography.bodyLarge)
        Column {  // Changed from Row to Column for vertical display
            categories.forEach { category ->
                Chip(
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    isSelected = category == selectedCategory
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Available")
            Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
        }
        Button(
            onClick = {
                if (selectedCategory.isNotEmpty()) {
                    menuViewModel.addMenuItem(MenuItem("", name, description, price.toDouble(), selectedCategory, isAvailable))
                    successMessage = "Item added successfully!"
                    showSuccessMessage = true  // Trigger the success message display
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Menu Item")
        }

        if (showSuccessMessage) {
            Text(successMessage, color = MaterialTheme.colorScheme.primary)
        }
        Button(
            onClick = { navController.popBackStack() }
        ) {
            Text("Back")
        }
    }
}
@Composable
fun Chip(
    label: @Composable () -> Unit,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            label()
        }
    }
}
@Composable
fun EditMenuItemScreen(
    navController: NavController,
    menuViewModel: MenuViewModel, // Correct type
    menuItemId: String // Pass menuItemId instead of menuItem
) {
    // State variables for the menu item properties
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(false) }
    val categories = listOf("Beverages", "Snacks", "Desserts", "Main Courses")

    // Load the menu item details
    var loadState by remember { mutableStateOf(true) } // Track if the data is loading

    LaunchedEffect(menuItemId) {
        menuViewModel.fetchMenuItemById(menuItemId) { menuItem ->
            if (menuItem != null) {
                name = menuItem.name
                description = menuItem.description
                price = menuItem.price.toString()
                selectedCategory = menuItem.category
                isAvailable = menuItem.isAvailable ?: false
                loadState = false
            }
        }
    }

    if (loadState) {
       // CircularProgressIndicator()
    } else {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") }
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") }
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Text("Select Category", style = MaterialTheme.typography.bodyLarge)
            Column {
                categories.forEach { category ->
                    Chip(
                        label = { Text(category) },
                        isSelected = category == selectedCategory,
                        onClick = { selectedCategory = category }
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Available")
                Switch(checked = isAvailable, onCheckedChange = { isAvailable = it })
            }
            Button(
                onClick = {
                    if (selectedCategory.isNotEmpty()) {
                        menuViewModel.updateMenuItem(
                            MenuItem(
                                itemId = menuItemId,
                                name = name,
                                description = description,
                                price = price.toDouble(),
                                category = selectedCategory,
                                isAvailable = isAvailable
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Menu Item")
            }
            Button(
                onClick = { navController.popBackStack() }
            ) {
                Text("Back")
            }
        }
    }
}
@Composable
fun MenuItemRow(menuItem: MenuItem, menuViewModel: MenuViewModel, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(menuItem.name, style = MaterialTheme.typography.bodyLarge)
        IconButton(onClick = { navController.navigate("editMenuItem/${menuItem.itemId}") }) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = {
            menuViewModel.deleteMenuItem(menuItem.itemId)
        }) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete")
        }
    }
}
