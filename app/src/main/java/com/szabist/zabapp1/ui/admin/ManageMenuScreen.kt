package com.szabist.zabapp1.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.MenuItem
import com.szabist.zabapp1.viewmodel.MenuViewModel

@Composable
fun ManageMenuScreen(navController: NavController, menuViewModel: MenuViewModel = viewModel()) {
    val menuItems by menuViewModel.menuItems.collectAsState()

    LaunchedEffect(Unit) {
        menuViewModel.loadMenuItems()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { navController.navigate("add_menu_item") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Menu Item")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(menuItems) { menuItem ->
                MenuItemCard(navController, menuItem, menuViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemCard(navController: NavController, menuItem: MenuItem, menuViewModel: MenuViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { navController.navigate("menu_item_details/${menuItem.id}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f) // Ensures description wraps within space
            ) {
                Text(menuItem.name, style = MaterialTheme.typography.titleMedium)
                Text(menuItem.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                Text("PKR ${menuItem.price}", style = MaterialTheme.typography.bodyMedium)
            }
            Row(
                modifier = Modifier.padding(start = 8.dp) // Added padding to separate buttons from text
            ) {
                IconButton(onClick = { navController.navigate("edit_menu_item/${menuItem.id}") }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Menu Item") },
            text = { Text("Are you sure you want to delete this menu item?") },
            confirmButton = {
                Button(onClick = {
                    menuViewModel.deleteMenuItem(menuItem) { success ->
                        if (success) {
                            menuViewModel.loadMenuItems()
                            showDeleteDialog = false
                        }
                    }
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}