package com.szabist.zabapp1.ui.admin

import ViewOrdersScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.szabist.zabapp1.MainActivity
import com.szabist.zabapp1.R
import com.szabist.zabapp1.viewmodel.AdminViewModel


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    onLogout: () -> Unit,
    adminViewModel: AdminViewModel = viewModel()
) {
    val activity = LocalContext.current as MainActivity
    val navController = rememberNavController()
    val items = listOf(
        AdminNavItem.Menu,
        AdminNavItem.Orders,
        AdminNavItem.Users,
        AdminNavItem.MonthlyBilling
    )

    var accountSectionVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),  // Optional padding for centering
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(80.dp)  // Set both width and height to 80.dp
                        )

                    }
                },
                navigationIcon = {
                    IconButton(onClick = { accountSectionVisible = !accountSectionVisible }) {
                        Icon(Icons.Filled.AccountBox, contentDescription = "Account")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry = navController.currentBackStackEntry
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
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
        Row(modifier = Modifier.padding(innerPadding)) {
            // Collapsible account section sliding from left to right
            AnimatedVisibility(
                visible = accountSectionVisible,
                enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)),
                exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
            ) {
                AdminAccountSection(onLogout = { activity.logout() })
            }

            // Navigation host
            Column(modifier = Modifier.fillMaxWidth()) {
                NavHost(navController, startDestination = AdminNavItem.Menu.route) {
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
                        navController.navigate("user_list")
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
                    composable("user_list") {
                        UserListScreen(navController)
                    }
                    composable("user_month_list/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                        MonthListScreen(navController = navController, userId = userId)
                    }
                    composable("user_bills/{userId}/{yearMonth}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                        val yearMonth = backStackEntry.arguments?.getString("yearMonth") ?: return@composable
                        UserMonthlyBillsScreen(navController = navController, userId = userId, yearMonth = yearMonth)
                    }
                    composable("bill_details/{billId}") { backStackEntry ->
                        val billId = backStackEntry.arguments?.getString("billId") ?: return@composable
                        BillDetailsScreen(navController = navController, billId = billId)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAccountSection(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.6f) // Adjusted width to 60% of the screen
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium) // Clean background with Material theme surface
            .padding(16.dp), // Added padding for content inside
        horizontalAlignment = Alignment.Start, // Align content to the start (left)
        verticalArrangement = Arrangement.spacedBy(8.dp) // Space between items
    ) {
        Text(
            text = "Admin Account",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface // Ensure text color contrasts with background
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Logout button
        Button(
            onClick = { onLogout() },
            modifier = Modifier
                .fillMaxWidth() // Make the button span the full width
                .padding(top = 16.dp), // Add some spacing from the text
            shape = MaterialTheme.shapes.medium // Rounded button shape for better UI
        ) {
            Text("Logout", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// AdminNavItem remains the same as before
sealed class AdminNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Menu : AdminNavItem("menu", Icons.Filled.Menu, "Menu")
    object Orders : AdminNavItem("orders", Icons.Filled.ShoppingCart, "Orders")
    object Users : AdminNavItem("users", Icons.Filled.Person, "Users")
    object MonthlyBilling : AdminNavItem("monthly_billing", Icons.Filled.List, "Billing")
}
