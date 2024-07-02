package com.szabist.zabcafe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AdminDashboard(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Admin Dashboard", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(32.dp))

            // User management
            Button(onClick = { navController.navigate("userManagement") }) {
                Text("Manage Users")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Orders management
            Button(onClick = { navController.navigate("viewAllOrders") }) {
                Text("View All Orders")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("modifyOrders") }) {
                Text("Modify Orders")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Billing management
            Button(onClick = { navController.navigate("viewBills") }) {
                Text("View Bills")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu items management
            Button(onClick = { navController.navigate("menuManagement") }) {
                Text("Manage Menu Items")
            }

        }
    }
}