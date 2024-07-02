package com.szabist.zabcafe.ui.screens

import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.szabist.zabcafe.model.User
import com.szabist.zabcafe.ui.components.UserComponent
import com.szabist.zabcafe.viewmodel.UserViewModel


@Composable
fun UserManagementScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    var userList by remember { mutableStateOf(listOf<User>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = true) {
        userViewModel.fetchAllUsers { users ->
            userList = users
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text("User Management", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Admin Dashboard")
        }
        Button(
            onClick = {
                navController.navigate("addUser")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add New User")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(userList) { user ->
                UserComponent(user = user) {
                    // Placeholder for click action, potentially open a detail view
                }
                if (user.role != "admin") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                navController.navigate("editUser/${user.userId}")
                            }
                        ) {
                            Text("Edit")
                        }
                        Button(
                            onClick = {
                                userViewModel.deleteUser(
                                    userId = user.userId,
                                    onSuccess = {
                                        userList = userList.filter { it.userId != user.userId }
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                    }
                                )
                            }
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }

    }
}
@Composable
fun AdminRegisterUserScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("student") }
    var contactNumber by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val roleOptions = listOf("hostilities", "faculty")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add New User", style = MaterialTheme.typography.headlineMedium)

        // Username input
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            Text("Role")
            roleOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { role = option },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (role == option),
                        onClick = { role = option }
                    )
                    Text(text = option, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contactNumber,
            onValueChange = { contactNumber = it },
            label = { Text("Contact Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                userViewModel.addUser(
                    User(
                        username = username,
                        password = password,
                        email = email,
                        role = role,
                        contactNumber = contactNumber
                    ),
                    onSuccess = {
                        message = "User added successfully"
                        navController.popBackStack()
                    },
                    onError = { errMsg ->
                        message = errMsg
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        if (message.isNotEmpty()) {
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to User Management")
        }
    }
}
@Composable
fun EditUserScreen(navController: NavController, userId: String, userViewModel: UserViewModel) {
    var user by remember { mutableStateOf<User?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(key1 = userId) {
        Log.d("EditUserScreen", "Attempting to fetch user with ID: $userId")
        userViewModel.fetchUserById(userId) { fetchedUser ->
            user = fetchedUser
            fetchedUser?.let {
                Log.d("EditUserScreen", "User fetched successfully: ${it.username}")
            } ?: Log.e("EditUserScreen", "Failed to fetch user or user is null")
        }
    }

    user?.let { editableUser ->
        var username by remember { mutableStateOf(editableUser.username) }
        var email by remember { mutableStateOf(editableUser.email) }
        var role by remember { mutableStateOf(editableUser.role) }
        var contactNumber by remember { mutableStateOf(editableUser.contactNumber) }
        var emailValid by remember { mutableStateOf(editableUser.emailValid) }
        var profileComplete by remember { mutableStateOf(editableUser.profileComplete) }
        val roleOptions = listOf("student","hostilities", "faculty")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Edit User", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Column {
                Text("Role")
                roleOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { role = option },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (role == option),
                            onClick = { role = option }
                        )
                        Text(text = option, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = contactNumber,
                onValueChange = { contactNumber = it },
                label = { Text("Contact Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = emailValid.toString(),
                onValueChange = { emailValid = it.toBoolean() },
                label = { Text("Email Valid") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = profileComplete.toString(),
                onValueChange = { profileComplete = it.toBoolean() },
                label = { Text("Profile Complete") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                Log.d("EditUserScreen", "Attempting to save changes for user: $userId")
                val updatedUser = editableUser.copy(
                    username = username,
                    email = email,
                    role = role,
                    contactNumber = contactNumber,
                    emailValid = emailValid,
                    profileComplete = profileComplete
                )
                userViewModel.updateUser(updatedUser.userId, updatedUser, {
                    Log.d("EditUserScreen", "User updated successfully")
                    navController.popBackStack()
                }, { error ->
                    Log.e("EditUserScreen", "Failed to update user: $error")
                    errorMessage = error
                })
            }) {
                Text("Save Changes")
            }

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                navController.popBackStack()
            }) {
                Text("Back to User Management")
            }
        }
    } ?: Text("Loading user data...", style = MaterialTheme.typography.bodyLarge)
}