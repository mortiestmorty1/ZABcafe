
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.MonthlyBillViewModel
import com.szabist.zabapp1.viewmodel.OrderViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrdersScreen(
    navController: NavController,
    orderViewModel: OrderViewModel = viewModel(),
    monthlyBillViewModel: MonthlyBillViewModel = viewModel()
) {
    val orders by orderViewModel.orders.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        orderViewModel.loadAllOrders()
    }

    val newOrders = orders.filter { it.status == "pending" }
    val currentOrders = orders.filter { it.status == "Accepted" || it.status == "Prepare" }
    val completedOrders = orders.filter { it.status == "Rejected" || it.status == "Completed" || it.status == "Ready for Pickup" }

    var selectedCategory by remember { mutableStateOf<OrderCategory?>(null) }
    val loadingStates = remember { mutableStateMapOf<String, Boolean>() }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (selectedCategory == null) {
            FullScreenCategoryButtons(
                onCategorySelected = { category ->
                    selectedCategory = category
                }
            )
        } else {
            OrderCategoryScreen(
                category = selectedCategory!!,
                newOrders = newOrders,
                currentOrders = currentOrders,
                completedOrders = completedOrders,
                navController = navController,
                onBack = { selectedCategory = null },
                isLoadingStates = loadingStates,
                onStatusChange = { orderId, newStatus ->
                    loadingStates[orderId] = true
                    orderViewModel.updateOrderStatus(
                        orderId = orderId,
                        newStatus = newStatus,
                        context = context,
                        handleMonthlyBill = { acceptedOrder ->
                            monthlyBillViewModel.handleOrder(
                                order = acceptedOrder,
                                userId = acceptedOrder.userId,
                                orderViewModel = orderViewModel,
                                context = context,
                            ) { success ->
                                if (!success) {
                                    // Show error and reset loading state
                                    Toast.makeText(context, "Failed to update order status", Toast.LENGTH_SHORT).show()
                                }
                                // Reset loading state regardless of success or failure
                                loadingStates[orderId] = false
                                // Reload orders to sync with backend
                                orderViewModel.loadAllOrders()
                            }

                        }
                    )
                    orderViewModel.loadAllOrders()
                }
            )
        }
    }
}
enum class OrderCategory(val displayName: String) {
    NewOrders("New Orders"),
    CurrentOrders("Current Orders"),
    CompletedOrders("Completed Orders")
}

@Composable
fun FullScreenCategoryButtons(onCategorySelected: (OrderCategory) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically) // Reduced spacing
    ) {
        FullScreenCategoryButton(
            title = "New Orders",
            icon = Icons.Filled.ThumbUp,
            color = MaterialTheme.colorScheme.primary,
            onClick = { onCategorySelected(OrderCategory.NewOrders) }
        )
        FullScreenCategoryButton(
            title = "Current Orders",
            icon = Icons.Filled.Build,
            color = MaterialTheme.colorScheme.secondary,
            onClick = { onCategorySelected(OrderCategory.CurrentOrders) }
        )
        FullScreenCategoryButton(
            title = "Completed Orders",
            icon = Icons.Filled.Done,
            color = MaterialTheme.colorScheme.tertiary,
            onClick = { onCategorySelected(OrderCategory.CompletedOrders) }
        )
    }
}

@Composable
fun FullScreenCategoryButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp), // Reduced spacing
        shape = RoundedCornerShape(24.dp), // Increased rounding
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(64.dp) // Larger icon
            )
            Spacer(modifier = Modifier.height(12.dp)) // Slightly larger space
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCategoryScreen(
    category: OrderCategory,
    newOrders: List<Order>,
    currentOrders: List<Order>,
    completedOrders: List<Order>,
    navController: NavController,
    onBack: () -> Unit,
    isLoadingStates: MutableMap<String, Boolean>,
    onStatusChange: (String, String) -> Unit
) {
    val ordersToShow = when (category) {
        OrderCategory.NewOrders -> newOrders
        OrderCategory.CurrentOrders -> currentOrders
        OrderCategory.CompletedOrders -> completedOrders
    }

    var searchQuery by remember { mutableStateOf("") }
    var loadingStates = remember { mutableStateMapOf<String, Boolean>() }

    val filteredOrders = ordersToShow.filter { order ->
        order.id.contains(searchQuery, ignoreCase = true) ||
                order.userName.contains(searchQuery, ignoreCase = true) ||
                order.status.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.displayName, style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Orders", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Orders Section
            if (filteredOrders.isEmpty()) {
                Text(
                    text = "No orders found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        EnhancedOrderItem(
                            order = order,
                            navController = navController,
                            isLoading = isLoadingStates[order.id] ?: false,
                            onStatusChange = onStatusChange
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedOrderItem(
    order: Order,
    navController: NavController,
    isLoading: Boolean,
    onStatusChange: (String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { navController.navigate("admin_order_details/${order.id}") },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Order ID: ${order.id}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Order Date: ${order.timestamp}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "User: ${order.userName}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Total: $${order.totalAmount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Payment Type: ${order.paymentMethod}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Status: ${order.status}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(12.dp))

            // Dynamic Buttons
            when (order.status) {
                "pending" -> {
                    OrderActionRow(
                        actions = listOf(
                            "Accept" to MaterialTheme.colorScheme.primary,
                            "Reject" to MaterialTheme.colorScheme.error
                        ),
                        icons = listOf(Icons.Filled.ThumbUp, Icons.Filled.Delete),
                        onClick = { action -> onStatusChange(order.id, action) },
                        isLoading = isLoading
                    )
                }
                "Accepted", "Prepare" -> {
                    OrderActionRow(
                        actions = listOf(
                            "Prepare" to MaterialTheme.colorScheme.tertiary,
                            "Ready for Pickup" to MaterialTheme.colorScheme.secondary
                        ),
                        icons = listOf(Icons.Filled.Build, Icons.Filled.LocationOn),
                        onClick = { action -> onStatusChange(order.id, action) },
                        isLoading = isLoading
                    )
                }
                "Ready for Pickup" -> {
                    OrderActionRow(
                        actions = listOf("Completed" to MaterialTheme.colorScheme.primary),
                        icons = listOf(Icons.Filled.Done),
                        onClick = { action -> onStatusChange(order.id, action) },
                        isLoading = isLoading
                    )
                }
            }
        }
    }
}
@Composable
fun OrderActionRow(
    actions: List<Pair<String, Color>>,
    icons: List<ImageVector>,
    onClick: (String) -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        actions.forEachIndexed { index, (action, color) ->
            Button(
                onClick = { if (!isLoading) onClick(action) },
                colors = ButtonDefaults.buttonColors(containerColor = color),
                modifier = Modifier
                    .size(width = 140.dp, height = 48.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = icons[index],
                        contentDescription = action,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = action,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OrderStatusButton(displayStatus: String,
                      backendStatus: String, icon: ImageVector, onStatusChange: (String) -> Unit, backgroundColor: Color) {
    Button(
        onClick = { onStatusChange(backendStatus) }, // Pass backendStatus when clicked
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier
            .padding(8.dp)
            .size(width = 150.dp, height = 50.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = displayStatus,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(4.dp)) // Add space between icon and text
        Text(text = displayStatus, style = MaterialTheme.typography.labelLarge)
    }
}
