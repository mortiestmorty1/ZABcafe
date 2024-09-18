
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

    Scaffold(
        topBar = { TopAppBar(title = { Text("View Orders") }) }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(orders, key = { it.id }) { order ->
                OrderAdminItem(order, onStatusChange = { newStatus ->
                    orderViewModel.updateOrderStatus(order.id, newStatus)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderAdminItem(order: Order, onStatusChange: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        onClick = {
            // Optionally add navigation to a detailed order status update screen
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order ID: ${order.id}", style = MaterialTheme.typography.bodyLarge)
            Text("Total: $${order.totalAmount}", style = MaterialTheme.typography.bodyLarge)
            Text("Status: ${order.status}", style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OrderStatusButton("Accept", Icons.Filled.ThumbUp, onStatusChange, MaterialTheme.colorScheme.primaryContainer)
                OrderStatusButton("Prepare", Icons.Filled.Build, onStatusChange, MaterialTheme.colorScheme.tertiaryContainer)
                OrderStatusButton("Ready for Pickup", Icons.Filled.LocationOn, onStatusChange, MaterialTheme.colorScheme.secondaryContainer)
                OrderStatusButton("Completed", Icons.Filled.Done, onStatusChange, MaterialTheme.colorScheme.onSurface)
                OrderStatusButton("Rejected", Icons.Filled.Delete, onStatusChange, MaterialTheme.colorScheme.errorContainer)
            }
        }
    }
}

@Composable
fun OrderStatusButton(status: String, icon: ImageVector, onStatusChange: (String) -> Unit, backgroundColor: Color) {
    Button(
        onClick = { onStatusChange(status) },
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier
            .padding(4.dp)
            .size(width = 64.dp, height = 32.dp)  // Smaller buttons
    ) {
        Icon(
            imageVector = icon,
            contentDescription = status,
            modifier = Modifier.size(16.dp)  // Smaller icons
        )
        Text(text = status, style = MaterialTheme.typography.labelSmall)
    }
}
