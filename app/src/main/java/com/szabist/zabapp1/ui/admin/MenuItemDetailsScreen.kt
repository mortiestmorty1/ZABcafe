package com.szabist.zabapp1.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.MenuItem
import com.szabist.zabapp1.viewmodel.MenuViewModel

@Composable
fun MenuItemDetailsScreen(navController: NavController, menuItemId: String, menuViewModel: MenuViewModel = viewModel()) {
    // Collecting menu item details as state
    val menuItemState = produceState<MenuItem?>(initialValue = null, key1 = menuItemId) {
        menuViewModel.getMenuItemById(menuItemId) { item ->
            value = item
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        menuItemState.value?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(it.name, style = MaterialTheme.typography.titleMedium)
                    Divider()
                    Text(it.description, style = MaterialTheme.typography.bodyMedium)
                    Divider()
                    Text("$${it.price}", style = MaterialTheme.typography.bodyMedium)
                    Divider()
                    Text(
                        if (it.available) "Available" else "Not Available",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } ?: Text("loading")  // Show loading or not found message
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}