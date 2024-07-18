package com.szabist.zabapp1.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.User
import com.szabist.zabapp1.viewmodel.UserViewModel

@Composable
fun ManageUsersScreen(navController: NavController, userViewModel: UserViewModel = viewModel()) {
    val users by userViewModel.users.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Manage Users", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("add_user") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add User")
            Text("Add User")
        }

        Spacer(modifier = Modifier.height(8.dp))

        users.filter { it.role != "admin" }.forEach { user ->
            UserRow(user, navController, userViewModel)
        }
    }
}

@Composable
fun UserRow(user: User, navController: NavController, userViewModel: UserViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                if (user.id.isNotEmpty()) {
                    navController.navigate("user_details/${user.id}") {
                        println("Navigating to user_details/${user.id}")
                    }
                } else {
                    println("Error: User ID is empty")
                }
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Username: ${user.username}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                IconButton(onClick = {
                    if (user.id.isNotEmpty()) {
                        navController.navigate("edit_user/${user.id}") {
                            println("Navigating to edit_user/${user.id}")
                        }
                    } else {
                        println("Error: User ID is empty")
                    }
                }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit User", tint = Color.Blue)
                }
                IconButton(onClick = { userViewModel.deleteUser(user.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete User", tint = Color.Red)
                }
            }
        }
    }
}
