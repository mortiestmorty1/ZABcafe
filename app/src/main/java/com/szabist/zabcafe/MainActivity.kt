package com.szabist.zabcafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import com.szabist.zabcafe.ui.theme.ZABcafeTheme

class MainActivity : ComponentActivity() {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZABcafeTheme {
                SignupScreen()
            }
        }
    }

    @Composable
    fun SignupScreen() {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var contact by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("") }
        var isLoggedIn by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") }
            )
            TextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text("Contact Number") }
            )
            TextField(
                value = role,
                onValueChange = { role = it },
                label = { Text("Role") }
            )
            Button(onClick = {
                addUserToDatabase(username, password, contact, role)
                isLoggedIn = true
            }) {
                Text("Sign Up")
            }
            if (isLoggedIn) {
                Text("User is logged in")
            }
            Button(onClick = { isLoggedIn = false }) {
                Text("Log Out")
            }
        }
    }

    private fun addUserToDatabase(username: String, password: String, contact: String, role: String) {
        val userRef = database.getReference("users/$username")
        userRef.child("password").setValue(password)
        userRef.child("contact").setValue(contact)
        userRef.child("role").setValue(role)
        println("User $username added successfully with role $role")
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewSignupScreen() {
        ZABcafeTheme {
            SignupScreen()
        }
    }
}