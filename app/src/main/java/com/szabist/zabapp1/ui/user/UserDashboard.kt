package com.szabist.zabapp1.ui.user

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.szabist.zabapp1.viewmodel.CartViewModel
import com.szabist.zabapp1.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboard(userId: String, userViewModel: UserViewModel = viewModel(), cartViewModel: CartViewModel = viewModel()) {
    val navController = rememberNavController()
    val items = listOf(
        UserNavItem.Menu,
        UserNavItem.OrderStatus,
        UserNavItem.MonthlyBilling,
        UserNavItem.PastOrders
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Dashboard") },
                actions = {
                    val cartItems by cartViewModel.cartItems.collectAsState()
                    Box(contentAlignment = Alignment.TopEnd) {
                        IconButton(onClick = { navController.navigate("cart") }) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                        }
                        if (cartItems.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-10).dp, y = 8.dp)
                                    .size(20.dp)
                                    .background(Color.Red, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartItems.size.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
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
        NavHost(navController, startDestination = UserNavItem.Menu.route, Modifier.padding(innerPadding)) {
            composable(UserNavItem.Menu.route) {
                MenuScreen(navController = navController, cartViewModel = cartViewModel)
            }
            composable(UserNavItem.OrderStatus.route) {
                OrderStatusScreen(navController = navController, userId = userId, orderViewModel = viewModel())
            }
            composable(UserNavItem.MonthlyBilling.route) {
                MonthlyBillingScreen(navController = navController, userId = userId, monthlyBillViewModel = viewModel())
            }
            composable(UserNavItem.PastOrders.route) {
                PastOrdersScreen(navController = navController, userId = userId, orderViewModel = viewModel())
            }
            composable("cart") {
                CartScreen(navController = navController, cartViewModel = cartViewModel)
            }
            composable("menu_item_details/{menuItemId}") { backStackEntry ->
                MenuItemDetailsScreen(
                    navController = navController,
                    menuItemId = backStackEntry.arguments?.getString("menuItemId") ?: "",
                    cartViewModel = cartViewModel
                )
            }
            composable("checkout") {
                CheckoutScreen(navController = navController, userId = userId, cartViewModel = cartViewModel, orderViewModel = viewModel())
            }
            composable("bill_details/{billId}") { backStackEntry ->
                BillDetailsScreen(
                    navController = navController,
                    billId = backStackEntry.arguments?.getString("billId") ?: "",
                    viewModel = viewModel()
                )
            }
        }
    }
}

sealed class UserNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Menu : UserNavItem("menu", Icons.Filled.Home, "Menu")
    object OrderStatus : UserNavItem("order_status", Icons.Filled.List, "Order Status")
    object MonthlyBilling : UserNavItem("monthly_billing", Icons.Filled.AccountBox, "Monthly Bill")
    object PastOrders : UserNavItem("past_orders", Icons.Filled.CheckCircle, "Past Orders")
}
