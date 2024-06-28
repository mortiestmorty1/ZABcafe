package com.szabist.zabcafe.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.szabist.zabcafe.viewmodel.UserViewModel
import com.szabist.zabcafe.model.User

@Composable
fun UserManagementScreen(userViewModel: UserViewModel) {
    val users by userViewModel.users.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "User Management",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(users) { user ->
                UserItemComponent(user, userViewModel::updateUserRole, userViewModel::deleteUser)
            }
        }
    }
}

@Composable
fun UserItemComponent(user: User, onUpdateRole: (String, String) -> Unit, onDelete: (String) -> Unit) {
    val (role, setRole) = remember { mutableStateOf(user.role) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Username: ${user.username}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Email: ${user.email}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Contact Number: ${user.contactNumber}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                OutlinedTextField(
                    value = role,
                    onValueChange = setRole,
                    label = { Text("Role") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onUpdateRole(user.userId, role) }) {
                    Text("Update Role")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onDelete(user.userId) }, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)) {
                    Text("Delete")
                }
            }
        }
    }
}