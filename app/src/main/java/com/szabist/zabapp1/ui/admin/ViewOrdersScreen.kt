
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.OrderViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrdersScreen(navController: NavController, orderViewModel: OrderViewModel = viewModel()) {
    val orders by orderViewModel.orders.collectAsState()

    LaunchedEffect(Unit) {
        orderViewModel.loadAllOrders()
    }

    val newOrders = orders.filter { it.status == "pending" }
    val currentOrders = orders.filter { it.status == "Accepted" || it.status == "Prepare" }
    val completedOrders = orders.filter { it.status == "Rejected" || it.status == "Completed" || it.status == "Ready for Pickup" }

    var selectedCategory by remember { mutableStateOf<OrderCategory?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("View Orders") }) }
    ) {
        Column(modifier = Modifier.padding(it).fillMaxSize()) {
            if (selectedCategory == null) {
                CategorySelectionScreen(
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
                    onBack = { selectedCategory = null },
                    onStatusChange = { orderId, newStatus ->
                        orderViewModel.updateOrderStatus(orderId, newStatus)
                    }
                )
            }
        }
    }
}

// OrderCategoryScreen and other functions remain the same

// Updated OrderAdminItem with User Name and Payment Type


// Order Category Enum
enum class OrderCategory(val displayName: String) {
    NewOrders("New Orders"),
    CurrentOrders("Current Orders"),
    CompletedOrders("Completed Orders")
}

// Category Selection Screen
@Composable
fun CategorySelectionScreen(onCategorySelected: (OrderCategory) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CategoryCard("New Orders", color = MaterialTheme.colorScheme.primary) {
            onCategorySelected(OrderCategory.NewOrders)
        }
        CategoryCard("Current Orders", color = MaterialTheme.colorScheme.secondary) {
            onCategorySelected(OrderCategory.CurrentOrders)
        }
        CategoryCard("Completed Orders", color = MaterialTheme.colorScheme.tertiary) {
            onCategorySelected(OrderCategory.CompletedOrders)
        }
    }
}

// Reusable Category Card
@Composable
fun CategoryCard(title: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, color = Color.White)
        }
    }
}

// Display Orders Based on the Selected Category
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCategoryScreen(
    category: OrderCategory,
    newOrders: List<Order>,
    currentOrders: List<Order>,
    completedOrders: List<Order>,
    onBack: () -> Unit,
    onStatusChange: (String, String) -> Unit
) {
    val ordersToShow = when (category) {
        OrderCategory.NewOrders -> newOrders
        OrderCategory.CurrentOrders -> currentOrders
        OrderCategory.CompletedOrders -> completedOrders
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.displayName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(ordersToShow, key = { it.id }) { order ->
                OrderAdminItem(order = order, onStatusChange = onStatusChange)
            }
        }
    }
}

// Updated Order Item with Button Visibility Based on Status
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderAdminItem(order: Order, onStatusChange: (String, String) -> Unit) {
    var isInitialSelection by remember { mutableStateOf(order.status == "pending") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order ID: ${order.id}", style = MaterialTheme.typography.bodyLarge)
            Text("Order Date: ${order.timestamp}", style = MaterialTheme.typography.bodyMedium)
            Text("User: ${order.userName}", style = MaterialTheme.typography.bodyLarge)  // Display user name
            Text("Total: $${order.totalAmount}", style = MaterialTheme.typography.bodyLarge)
            Text("Payment Type: ${order.paymentMethod}", style = MaterialTheme.typography.bodyLarge)  // Display payment type
            Text("Status: ${order.status}", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.padding(8.dp))

            if (isInitialSelection && order.status == "pending") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    OrderStatusButton(
                        status = "Accept",
                        icon = Icons.Filled.ThumbUp,
                        onStatusChange = { status ->
                            onStatusChange(order.id, status)
                            isInitialSelection = false
                        },
                        backgroundColor = MaterialTheme.colorScheme.primary
                    )
                    OrderStatusButton(
                        status = "Reject",
                        icon = Icons.Filled.Delete,
                        onStatusChange = { status ->
                            onStatusChange(order.id, status)
                            isInitialSelection = false
                        },
                        backgroundColor = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (order.status == "Accepted" || order.status == "Prepare") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    OrderStatusButton(
                        status = "Prepare",
                        icon = Icons.Filled.Build,
                        onStatusChange = { status -> onStatusChange(order.id, status) },
                        backgroundColor = MaterialTheme.colorScheme.tertiary
                    )
                    OrderStatusButton(
                        status = "Ready for Pickup",
                        icon = Icons.Filled.LocationOn,
                        onStatusChange = { status -> onStatusChange(order.id, status) },
                        backgroundColor = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            if (order.status == "Ready for Pickup") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    OrderStatusButton(
                        status = "Completed",
                        icon = Icons.Filled.Done,
                        onStatusChange = { status -> onStatusChange(order.id, status) },
                        backgroundColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Reusable Order Status Button
@Composable
fun OrderStatusButton(status: String, icon: ImageVector, onStatusChange: (String) -> Unit, backgroundColor: Color) {
    Button(
        onClick = { onStatusChange(status) },
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier
            .padding(8.dp)
            .size(width = 150.dp, height = 50.dp)  // Adjusted width for longer text
    ) {
        Icon(
            imageVector = icon,
            contentDescription = status,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(4.dp)) // Add space between icon and text
        Text(text = status, style = MaterialTheme.typography.labelLarge)
    }
}
