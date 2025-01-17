package com.szabist.zabapp1.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.szabist.zabapp1.R
import com.szabist.zabapp1.data.model.User
import com.szabist.zabapp1.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9))
                )
            )
    ) {
        // Top overlapping circles for the background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFFBBDEFB),
                radius = 200f,
                center = center.copy(x = center.x - 300, y = 150F)
            )
            drawCircle(
                color = Color(0xFF64B5F6),
                radius = 150f,
                center = center.copy(x = center.x + 300, y = 100F)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo (same size as LoginScreen)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(300.dp) // Same size as LoginScreen
                    .padding(bottom = 32.dp)
            )

            // Username Field with Rounded Corners
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Email Field with Rounded Corners
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Contact Number Field with Rounded Corners
            TextField(
                value = contactNumber,
                onValueChange = { contactNumber = it },
                label = { Text("Contact Number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Password Field with Rounded Corners
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Register Button
            Button(
                onClick = {
                    if (username.isBlank() || email.isBlank() || contactNumber.isBlank() || password.isBlank()) {
                        Toast.makeText(
                            context,
                            "Please fill in all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                        isLoading = false
                    } else {
                        isLoading = true
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = auth.currentUser?.uid ?: ""
                                    val user = User(
                                        id = userId,
                                        username = username,
                                        email = email,
                                        contactNumber = contactNumber,
                                        role = "student"
                                    )
                                    authViewModel.addUser(user) {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Registration successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("login")
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp)
            ) {
                Text("Register", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Back to Login Button
            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Back to Login",
                    fontSize = 14.sp,
                    color = Color(0xFF1E88E5),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
