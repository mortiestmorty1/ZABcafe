package com.szabist.zabapp1.ui.user

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.szabist.zabapp1.MainActivity
import com.szabist.zabapp1.R
import com.szabist.zabapp1.data.model.User
import com.szabist.zabapp1.viewmodel.CartViewModel
import com.szabist.zabapp1.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboard(userId: String, userViewModel: UserViewModel = viewModel(), cartViewModel: CartViewModel = viewModel()) {
    val activity = LocalContext.current as MainActivity
    val navController = rememberNavController()
    val items = listOf(
        UserNavItem.Menu,
        UserNavItem.OrderStatus,
        UserNavItem.MonthlyBilling,
        UserNavItem.PastOrders
    )

    // State to manage the account section visibility
    var accountSectionVisible by remember { mutableStateOf(false) }

    // Call the fetchUserDetails function when the screen is composed
    LaunchedEffect(userId) {
        userViewModel.fetchUserDetails(userId)
    }

    // Observe the currentUser state from the ViewModel
    val currentUser by userViewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Spacer to push the logo to the center
                        Spacer(modifier = Modifier.weight(1f))

                        // Logo in the center
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(80.dp)
                        )

                        // Spacer to balance the logo in the center
                        Spacer(modifier = Modifier.weight(1f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { accountSectionVisible = !accountSectionVisible }) {
                        Icon(Icons.Filled.AccountBox, contentDescription = "Account")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("cart") }) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                        val cartItems by cartViewModel.cartItems.collectAsState()
                        if (cartItems.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Top)
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
        Row(modifier = Modifier.padding(innerPadding)) {
            // Collapsible account section sliding from left to right
            AnimatedVisibility(
                visible = accountSectionVisible,
                enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)),
                exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
            ) {
                AccountSection(user = currentUser, onLogout = { activity.logout() })
            }

            // Navigation host
            Column(modifier = Modifier.fillMaxWidth()) {
                NavHost(navController, startDestination = UserNavItem.Menu.route) {
                    composable(UserNavItem.Menu.route) {
                        MenuScreen(navController = navController, cartViewModel = cartViewModel)
                    }
                    composable(UserNavItem.OrderStatus.route) {
                        OrderStatusScreen(navController = navController, userId = userId, orderViewModel = viewModel())
                    }
                    composable(UserNavItem.MonthlyBilling.route) {
                        MonthlyBillingScreen(navController = navController, userId = userId)
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
                    composable("order_details/{orderId}") { backStackEntry ->
                        OrderDetailsScreen(
                            navController = navController,
                            orderId = backStackEntry.arguments?.getString("orderId") ?: "",
                            orderViewModel = viewModel()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AccountSection(user: User?, onLogout: () -> Unit) {
    user?.let {
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
                text = "Name: ${user.username}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface // Ensure text color contrasts with background
            )
            Text(
                text = "Email: ${user.email}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Contact: ${user.contactNumber}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp)) // Added space before the logout button

            // Make logout button more prominent
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
    } ?: run {
        // Display loading text with appropriate padding and background
        Text(
            text = "Loading user details...",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

sealed class UserNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Menu : UserNavItem("menu", Icons.Filled.Home, "Menu")
    object OrderStatus : UserNavItem("order_status", Icons.Filled.List, "Order Status")
    object MonthlyBilling : UserNavItem("monthly_billing", Icons.Filled.AccountBox, "Monthly Bill")
    object PastOrders : UserNavItem("past_orders", Icons.Filled.CheckCircle, "Past Orders")
}