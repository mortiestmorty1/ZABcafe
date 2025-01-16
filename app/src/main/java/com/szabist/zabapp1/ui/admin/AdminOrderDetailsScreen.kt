package com.szabist.zabapp1.ui.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.R
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailsScreen(
    navController: NavController,
    orderId: String,
    orderViewModel: OrderViewModel = viewModel()
) {
    val context = LocalContext.current
    // Load the order details when the screen is opened
    LaunchedEffect(orderId) {
        orderViewModel.loadOrderById(orderId)
    }

    // Collect the current order state
    val currentOrder by orderViewModel.currentOrder.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        content = { padding ->
            currentOrder?.let { order ->
                AdminOrderDetailsContent(
                    initialOrder = order,
                    onActionClick = { action ->
                        // Pass the context to updateOrderStatus
                        orderViewModel.updateOrderStatus(
                            context = context,
                            orderId = order.id,
                            newStatus = action
                        )
                    }
                )
            } ?: run {
                // Display a "not found" message if the order is null
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Order not found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    )
}

@Composable
fun AdminOrderDetailsContent(initialOrder: Order, onActionClick: (String) -> Unit) {
    // Track the order's current status using state
    var orderStatus by remember { mutableStateOf(initialOrder.status) }

    // Update UI components based on the current status
    val (statusImageRes, statusIcon, statusColor) = when (orderStatus.lowercase()) {
        "pending" -> Triple(R.drawable.pending, Icons.Default.Info, Color.Gray)
        "accepted" -> Triple(R.drawable.accepted, Icons.Default.CheckCircle, Color.Green)
        "rejected" -> Triple(R.drawable.rejected, Icons.Default.Close, Color.Red)
        "completed" -> Triple(R.drawable.ordercomplete, Icons.Default.CheckCircle, Color.Green)
        "prepare" -> Triple(R.drawable.preparing, Icons.Default.Build, Color.Gray)
        "ready for pickup" -> Triple(R.drawable.readyforpickup, Icons.Default.CheckCircle, Color.Blue)
        else -> Triple(R.drawable.pending, Icons.Default.Info, Color.Gray)
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Order Status Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = statusImageRes),
                contentDescription = orderStatus,
                modifier = Modifier.fillMaxWidth(0.8f),
                contentScale = ContentScale.Fit
            )
        }

        // Order Status Section
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
                text = orderStatus.uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(color = statusColor)
            )
        }

        // Order Details Section
        AdminDetailsRow(label = "Order ID", value = initialOrder.id)
        AdminDetailsRow(label = "Order Date", value = formatDate(initialOrder.timestamp))
        AdminDetailsRow(label = "User Name", value = initialOrder.userName)
        AdminDetailsRow(label = "Total Amount", value = "PKR ${initialOrder.totalAmount}")
        AdminDetailsRow(label = "Payment Method", value = initialOrder.paymentMethod)

        // Items Section
        Text(
            text = "Items:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 16.dp, bottom = 8.dp)
        )

        initialOrder.items.forEach { item ->
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

        Spacer(modifier = Modifier.height(24.dp))

        // Admin Actions Section
        AdminOrderActions(
            currentStatus = orderStatus,
            onActionClick = { action ->
                // Update the local status to reflect changes immediately
                orderStatus = action
                onActionClick(action)
            }
        )
    }
}
@Composable
fun AdminDetailsRow(label: String, value: String) {
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
fun AdminOrderActions(currentStatus: String, onActionClick: (String) -> Unit) {
    // Define available actions based on the current status
    val actions = when (currentStatus.lowercase()) {
        "pending" -> listOf("Accept", "Reject")
        "accepted" -> listOf("Prepare")
        "prepare" -> listOf("Ready for Pickup")
        "ready for pickup" -> listOf("Completed")
        else -> emptyList()
    }

    val colors = actions.map { action ->
        when (action.lowercase()) {
            "accept" -> MaterialTheme.colorScheme.primary
            "reject" -> MaterialTheme.colorScheme.error
            "prepare" -> MaterialTheme.colorScheme.tertiary
            "ready for pickup" -> MaterialTheme.colorScheme.secondary
            "completed" -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurface
        }
    }

    AdminActionRow(actions = actions, colors = colors, onActionClick = onActionClick)
}

@Composable
fun AdminActionRow(actions: List<String>, colors: List<Color>, onActionClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        actions.forEachIndexed { index, action ->
            Button(
                onClick = { onActionClick(action) },
                colors = ButtonDefaults.buttonColors(containerColor = colors[index]),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(width = 150.dp, height = 50.dp)
            ) {
                Text(text = action, color = Color.White)
            }
        }
    }
}

fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return formatter.format(date)
}


