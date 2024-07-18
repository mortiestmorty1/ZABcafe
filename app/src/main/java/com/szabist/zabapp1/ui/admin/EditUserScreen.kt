package com.szabist.zabapp1.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.szabist.zabapp1.data.model.User
import com.szabist.zabapp1.viewmodel.UserViewModel

@Composable
fun EditUserScreen(navController: NavController, userId: String, userViewModel: UserViewModel = viewModel()) {
    // Fetch the user data when the Composable is called or the userId changes
    LaunchedEffect(userId) {
        userViewModel.fetchUserById(userId)
    }

    val userState by userViewModel.currentUser.collectAsState()

    // Use a local constant to ensure smart casting works
    val user = userState

    // Handle null user by showing a loading indicator or a message
    if (user == null) {
        Text("Loading or no user found", style = MaterialTheme.typography.h6)
    } else {
        UserEditForm(user, navController, userViewModel)
    }
}

@Composable
fun UserEditForm(user: User, navController: NavController, userViewModel: UserViewModel) {
    var username by remember { mutableStateOf(user.username) }
    var email by remember { mutableStateOf(user.email) }
    var contactNumber by remember { mutableStateOf(user.contactNumber) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        TextField(
            value = contactNumber,
            onValueChange = { contactNumber = it },
            label = { Text("Contact Number") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                userViewModel.updateUser(user.copy(username = username, email = email, contactNumber = contactNumber))
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update User")
        }
    }
}