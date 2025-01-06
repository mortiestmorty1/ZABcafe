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
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.szabist.zabapp1.MainActivity
import com.szabist.zabapp1.R
import com.szabist.zabapp1.viewmodel.AdminViewModel
import java.time.YearMonth


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
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center // Centers the content
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(50.dp) // Adjust size if needed
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
            NavigationBar(
                modifier = Modifier.height(64.dp),
                containerColor = MaterialTheme.colorScheme.surface // Set the background color
            ) {
                val navBackStackEntry = navController.currentBackStackEntry
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp) // Standard icon size
                            )
                        },
                        label = {
                            Text(
                                item.title,
                                style = MaterialTheme.typography.bodySmall // Adjust text size
                            )
                        },
                        selected = currentRoute?.contains(item.route) == true, // Updated logic for better matching
                        onClick = {
                            if (currentRoute?.contains(item.route) != true) { // Prevent re-navigation to the same route
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = false
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            }
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Blue,
                            selectedTextColor = Blue,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding)) {
            // Collapsible account section
            AnimatedVisibility(
                visible = accountSectionVisible,
                enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)),
                exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)),
                modifier = Modifier.zIndex(1f) // Ensures visibility over other layers
            ) {
                AdminAccountSection(onLogout = { activity.logout() })
            }

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
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        YearSelectionScreen(navController = navController, userId = userId)
                    }
                    composable("month_list/{userId}/{year}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        val year = backStackEntry.arguments?.getString("year")?.toInt() ?: YearMonth.now().year
                        MonthListScreen(navController = navController, userId = userId, year = year)
                    }
                    composable("user_bills/{userId}/{yearMonth}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        val yearMonth = backStackEntry.arguments?.getString("yearMonth") ?: ""
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
            .fillMaxWidth(0.6f)
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Admin Account",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Name: Admin",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Role: Super Admin",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
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
