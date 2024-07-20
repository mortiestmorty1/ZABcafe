package com.szabist.zabapp1

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.szabist.zabapp1.ui.admin.AdminDashboard
import com.szabist.zabapp1.ui.auth.LoginScreen
import com.szabist.zabapp1.ui.auth.RegisterScreen
import com.szabist.zabapp1.ui.theme.Zabapp1Theme
import com.szabist.zabapp1.ui.user.UserDashboard

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Zabapp1Theme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
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
                    composable("admin_dashboard") { AdminDashboard() }
                }
            }
        }
    }
}
