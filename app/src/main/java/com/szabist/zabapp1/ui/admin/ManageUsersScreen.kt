package com.szabist.zabapp1.ui.admin

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.User
import com.szabist.zabapp1.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(navController: NavController, userViewModel: UserViewModel = viewModel()) {
    val users by userViewModel.users.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Log.d("ManageUsersScreen", "Fetching users on screen load")
        userViewModel.fetchUsers()
    }

    val filteredUsers = users.filter {
        it.username.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Add User Button and Search Bar in a Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Users") },
                modifier = Modifier
                    .weight(0.7f)
                    .height(56.dp), // Adjust height to make it look like a proper input field
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search, // Replace with search icon
                        contentDescription = "Search Icon"
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(50.dp) // Rounded search bar
            )
            Button(
                onClick = { navController.navigate("add_user") },
                modifier = Modifier.weight(0.3f)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add User")
                Spacer(modifier = Modifier.width(8.dp)) // Spacing between icon and text
                Text("Add")
            }

        }

        // User List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredUsers.filter { it.role != "admin" }) { user ->
                UserRow(user, navController, userViewModel)
            }
        }
    }
}

@Composable
fun UserRow(user: User, navController: NavController, userViewModel: UserViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (user.id.isNotEmpty()) {
                    navController.navigate("user_details/${user.id}")
                } else {
                    Log.e("UserRow", "Error: User ID is empty")
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
                        navController.navigate("edit_user/${user.id}")
                    } else {
                        Log.e("UserRow", "Error: User ID is empty")
                    }
                }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit User", tint = Color.Blue)
                }
                IconButton(onClick = {
                    showDeleteDialog = true
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete User", tint = Color.Red)
                }
            }
        }
    }

    // Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete User") },
            text = { Text("Are you sure you want to delete this user?") },
            confirmButton = {
                Button(onClick = {
                    userViewModel.deleteUser(user.id)
                    userViewModel.fetchUsers()
                    showDeleteDialog = false
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



