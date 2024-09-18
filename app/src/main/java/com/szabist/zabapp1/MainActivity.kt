package com.szabist.zabapp1

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.szabist.zabapp1.ui.admin.AdminDashboard
import com.szabist.zabapp1.ui.auth.LoginScreen
import com.szabist.zabapp1.ui.auth.RegisterScreen
import com.szabist.zabapp1.ui.theme.Zabapp1Theme
import com.szabist.zabapp1.ui.user.UserDashboard

class MainActivity : ComponentActivity() {
    lateinit var navController: NavController
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Zabapp1Theme {
                navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun SetupNavGraph(navController: NavController) {
        NavHost(navController = navController as NavHostController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { user ->
                        // Directly navigate with user information
                        if (user.role == "admin") {
                            navController.navigate("admin_dashboard")
                        } else {
                            // Pass the entire user object or just the userId
                            navController.navigate("user_dashboard/${user.id}")
                        }
                    },
                    navController = navController
                )
            }
            composable("register") {
                RegisterScreen(navController = navController)
            }
            composable("user_dashboard/{userId}") { backStackEntry ->
                // Fetch userId from the navigation argument
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                UserDashboard(userId = userId)
            }
            composable("admin_dashboard") {
                AdminDashboard(onLogout = { logout() })
            }
        }
    }
    var shouldNavigateToLogin = false

    fun logout() {
        Log.d("MainActivity", "Attempting to logout")
        FirebaseAuth.getInstance().signOut()
        if (::navController.isInitialized) {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            navigateToLoginScreen()
        } else {
            Log.e("MainActivity", "NavController is not yet initialized")
            shouldNavigateToLogin = true
        }
    }

    fun navigateToLoginScreen() {
        if(::navController.isInitialized) {
            Log.d("MainActivity", "Navigating to login screen")
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        } else {
            Log.e("MainActivity", "NavController is not yet initialized")
        }
    }
}
