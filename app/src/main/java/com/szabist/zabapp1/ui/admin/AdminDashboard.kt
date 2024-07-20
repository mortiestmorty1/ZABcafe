package com.szabist.zabapp1.ui.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.szabist.zabapp1.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(adminViewModel: AdminViewModel = viewModel()) {
    val navController = rememberNavController()
    val items = listOf(
        AdminNavItem.Menu,
        AdminNavItem.Orders,
        AdminNavItem.Users,
        AdminNavItem.MonthlyBilling
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin Dashboard") }) },
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = navController.currentBackStackEntry?.destination?.route == item.route,
                        onClick = {
                            // Check if the current route is already displayed
                            if (navController.currentBackStackEntry?.destination?.route != item.route) {
                                // Navigate and clear all previous routes from the back stack
                                navController.navigate(item.route) {
                                    // Remove all entries from the back stack up to but not including the root
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = false
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = AdminNavItem.Menu.route, Modifier.padding(innerPadding)) {
            composable(AdminNavItem.Menu.route) {
                ManageMenuScreen(navController = navController)
            }
            composable(AdminNavItem.Orders.route) {
                ViewOrdersScreen(navController = navController, orderViewModel = viewModel())
            }
            composable(AdminNavItem.Users.route) {
                ManageUsersScreen(navController = navController)
            }
            composable(AdminNavItem.MonthlyBilling.route) {
                ManageMonthlyBillingScreen()
            }
            composable("add_menu_item") {
                AddMenuItemScreen(navController = navController)
            }
            composable("menu_item_details/{menuItemId}") { backStackEntry ->
                MenuItemDetailsScreen(navController = navController, menuItemId = backStackEntry.arguments?.getString("menuItemId") ?: "")
            }
            composable("edit_menu_item/{menuItemId}") { backStackEntry ->
                val menuItemId = backStackEntry.arguments?.getString("menuItemId") ?: return@composable
                EditMenuItemScreen(navController, menuItemId)
            }
            composable("add_user") {
                AddUserScreen(navController = navController)
            }
            composable("edit_user/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                EditUserScreen(navController, userId)
            }
            composable("user_details/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                UserDetailsScreen(navController, userId)
            }

        }
    }
}

sealed class AdminNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Menu : AdminNavItem("menu", Icons.Filled.Menu, "Menu")
    object Orders : AdminNavItem("orders", Icons.Filled.ShoppingCart, "Orders")
    object Users : AdminNavItem("users", Icons.Filled.Person, "Users")
    object MonthlyBilling : AdminNavItem("monthly_billing", Icons.Filled.List, "Billing")
}
