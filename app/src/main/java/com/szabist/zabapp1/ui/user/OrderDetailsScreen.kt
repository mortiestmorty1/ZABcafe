package com.szabist.zabapp1.ui.user

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.szabist.zabapp1.R
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    navController: NavController,
    orderId: String,
    fromCheckout: Boolean = false, // Flag to determine navigation source
    orderViewModel: OrderViewModel = viewModel()
) {
    val activity = LocalContext.current as? Activity

    // Load order when the screen is first opened or when orderId changes.
    LaunchedEffect(orderId) {
        orderViewModel.loadOrderById(orderId)
    }

    // Collect the current order as state
    val order by orderViewModel.currentOrder.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        if (fromCheckout) {
                            // Navigate to the Menu Screen if accessed from Checkout
                            navController.navigate("menu") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            // Navigate back normally if accessed from OrderStatus or PastOrders
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { padding ->
            order?.let {
                OrderDetailsContent(order = it)
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Order not found",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun OrderDetailsContent(order: Order) {
    // Map order status to image and color
    val (statusImageRes, statusIcon, statusColor) = when (order.status.lowercase()) {
        "pending" -> Triple(R.drawable.pending, Icons.Default.Info, Color.Gray)
        "accepted" -> Triple(R.drawable.accepted, Icons.Default.CheckCircle, Color.Green)
        "rejected" -> Triple(R.drawable.rejected, Icons.Default.Close, Color.Red)
        "completed" -> Triple(R.drawable.ordercomplete, Icons.Default.CheckCircle, Color.Green)
        "prepare" -> Triple(R.drawable.preparing, Icons.Default.Build, Color.DarkGray)
        "ready for pickup" -> Triple(R.drawable.readyforpickup, Icons.Default.CheckCircle, Color.Blue)
        else -> Triple(R.drawable.pending, Icons.Default.Info, Color.Gray)
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()), // Enable scrolling for overflow
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Add uniform spacing at the top
        Spacer(modifier = Modifier.height(32.dp))

        // Status Image with consistent size and alignment
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // Set a fixed height for consistency
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center // Center the image inside the box
        ) {
            Image(
                painter = painterResource(id = statusImageRes),
                contentDescription = order.status,
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Slightly reduce the width for padding
                    .aspectRatio(1f), // Maintain square aspect ratio
                contentScale = ContentScale.Fit // Ensure the entire image fits
            )
        }

        // Status Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = "Order Status Icon",
                tint = statusColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = order.status.uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(color = statusColor)
            )
        }

        // Order Details
        DetailsRow(label = "Order ID", value = order.id)
        DetailsRow(label = "Order Date", value = formatDate(order.timestamp))
        DetailsRow(label = "Total Amount", value = "PKR ${order.totalAmount}")

        // Items Section
        Text(
            text = "Items:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 16.dp, bottom = 8.dp)
        )

        order.items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "PKR ${item.price}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun DetailsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return formatter.format(date)
}