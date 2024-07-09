package com.szabist.zabcafe.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DashboardScreen(val route: String, val icon: ImageVector, val label: String) {
    object Menu : DashboardScreen("menu", Icons.Filled.Home, "Menu")
    object ViewBills : DashboardScreen("viewBills", Icons.Filled.AccountBox, "Bills")
    object PastOrders : DashboardScreen("pastOrders", Icons.Filled.List, "Orders")
    object OrderStatus : DashboardScreen("orderStatus", Icons.Filled.Info, "Status")
}