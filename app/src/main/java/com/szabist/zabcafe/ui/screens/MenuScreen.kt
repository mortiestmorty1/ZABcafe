package com.szabist.zabcafe.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.szabist.zabcafe.ui.components.MenuItemComponent
import com.szabist.zabcafe.viewmodel.MenuViewModel

@Composable
fun MenuScreen(menuViewModel: MenuViewModel) {
    val menuItems by menuViewModel.menuItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Menu",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(menuItems) { menuItem ->
                MenuItemComponent(
                    menuItem = menuItem,
                    onClick = { /* Handle click event, e.g., add to cart */ }
                )
            }
        }
    }
}