package com.szabist.zabapp1

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var navController: NavController

    // Request Notification Permission for Android 13+
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+ (API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

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
                            navController.navigate("admin_dashboard/${user.id}")
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
            composable("admin_dashboard/{adminId}") { backStackEntry ->
                val adminId = backStackEntry.arguments?.getString("adminId") ?: ""
                AdminDashboard(
                    adminId = adminId, // Pass adminId here
                    onLogout = { logout() }
                )
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
