package com.szabist.zabcafe.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.szabist.zabcafe.model.CartItem
import com.szabist.zabcafe.ui.components.MenuItemComponent
import com.szabist.zabcafe.ui.navigation.DashboardScreen
import com.szabist.zabcafe.viewmodel.CartViewModel
import com.szabist.zabcafe.viewmodel.MenuViewModel
import com.szabist.zabcafe.viewmodel.UserDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboard(
    navController: NavController,
    userDashboardViewModel: UserDashboardViewModel,
    userId: String,
    menuViewModel: MenuViewModel,
    cartViewModel: CartViewModel
) {
    val navHostController = rememberNavController()
    val screens = listOf(
        DashboardScreen.ViewBills,
        DashboardScreen.Menu,
        DashboardScreen.PastOrders,
        DashboardScreen.OrderStatus
    )

    val cartItemCount by cartViewModel.cartItemCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Dashboard") },
                actions = {
                    IconButton(onClick = { navController.navigate("cart") }) {
                        BadgeBox(cartItemCount)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation {
                screens.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = navHostController.currentDestination?.route == screen.route,
                        onClick = {
                            if (navHostController.currentDestination?.route != screen.route) {
                                navHostController.navigate(screen.route) {
                                    popUpTo(navHostController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    restoreState = true
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navHostController,
            startDestination = DashboardScreen.ViewBills.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(DashboardScreen.ViewBills.route) { ViewBillsScreen() }
            composable(DashboardScreen.Menu.route) { MenuScreen(menuViewModel, cartViewModel, userId) }
            composable(DashboardScreen.PastOrders.route) { PastOrdersScreen() }
            composable(DashboardScreen.OrderStatus.route) { OrderStatusScreen() }
        }
    }
}

@Composable
fun ViewBillsScreen() { /* Content for View Bills */ }

@Composable
fun MenuScreen(menuViewModel: MenuViewModel, cartViewModel: CartViewModel, userId: String) {
    val menuItems by menuViewModel.menuItems.collectAsState()

    Column {
        menuItems.forEach { menuItem ->
            MenuItemComponent(menuItem = menuItem, onClick = {
                val cartItem = CartItem(
                    itemId = menuItem.itemId,
                    name = menuItem.name,
                    price = menuItem.price,
                    quantity = 1,
                    userId = userId
                )
                cartViewModel.addToCart(userId, cartItem)
            })
        }
    }
}

@Composable
fun PastOrdersScreen() { /* Content for Past Orders */ }

@Composable
fun OrderStatusScreen() { /* Content for Order Status */ }

@Composable
fun BadgeBox(cartItemCount: Int) {
    Box(contentAlignment = Alignment.TopEnd) {
        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
        if (cartItemCount > 0) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .padding(top = 2.dp, end = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cartItemCount.toString(),
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onPrimary),
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}