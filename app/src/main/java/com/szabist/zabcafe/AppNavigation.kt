package com.szabist.zabcafe.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.szabist.zabcafe.ui.screens.AddMenuItemScreen
import com.szabist.zabcafe.ui.screens.AdminDashboard
import com.szabist.zabcafe.ui.screens.AdminRegisterUserScreen
import com.szabist.zabcafe.ui.screens.CartScreen
import com.szabist.zabcafe.ui.screens.CheckoutScreen
import com.szabist.zabcafe.ui.screens.EditMenuItemScreen
import com.szabist.zabcafe.ui.screens.EditUserScreen
import com.szabist.zabcafe.ui.screens.LoginScreen
import com.szabist.zabcafe.ui.screens.MenuManagementScreen
import com.szabist.zabcafe.ui.screens.RegisterScreen
import com.szabist.zabcafe.ui.screens.UserDashboard
import com.szabist.zabcafe.ui.screens.UserManagementScreen
import com.szabist.zabcafe.viewmodel.AdminDashboardViewModel
import com.szabist.zabcafe.viewmodel.CartViewModel
import com.szabist.zabcafe.viewmodel.LoginViewModel
import com.szabist.zabcafe.viewmodel.MenuViewModel
import com.szabist.zabcafe.viewmodel.RegisterViewModel
import com.szabist.zabcafe.viewmodel.UserDashboardViewModel
import com.szabist.zabcafe.viewmodel.UserViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    registerViewModel: RegisterViewModel,
    loginViewModel: LoginViewModel,
    adminDashboardViewModel: AdminDashboardViewModel,
    userDashboardViewModel: UserDashboardViewModel,
    menuViewModel: MenuViewModel,
    userViewModel: UserViewModel,
    cartViewModel: CartViewModel
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(loginViewModel = loginViewModel, navController = navController)
        }
        composable("register") {
            RegisterScreen(registerViewModel = registerViewModel, navController = navController)
        }
        composable("adminDashboard") {
            AdminDashboard(navController = navController)
        }
        composable("userDashboard/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            UserDashboard(navController, userDashboardViewModel, userId, menuViewModel, cartViewModel)
        }
        composable("menuManagement") {
            MenuManagementScreen(navController, menuViewModel)
        }
        composable("userManagement") {
            UserManagementScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("addUser") {
            AdminRegisterUserScreen(navController = navController, userViewModel = userViewModel)
        }
        composable("editUser/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            EditUserScreen(navController, userId, userViewModel)
        }
        composable("addMenuItem") {
            AddMenuItemScreen(navController = navController, menuViewModel = menuViewModel)
        }
        composable("editMenuItem/{menuItemId}") { backStackEntry ->
            val menuItemId = backStackEntry.arguments?.getString("menuItemId") ?: return@composable
            EditMenuItemScreen(navController, menuViewModel, menuItemId)
        }
        composable("cart") {
            CartScreen(navController, cartViewModel)
        }
        composable("checkout") {
            CheckoutScreen(navController, cartViewModel)
        }
    }
}