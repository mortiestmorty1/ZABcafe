package com.szabist.zabapp1.ui.user

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.R
import com.szabist.zabapp1.data.model.CartItem
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.CartViewModel
import com.szabist.zabapp1.viewmodel.MonthlyBillViewModel
import com.szabist.zabapp1.viewmodel.OrderViewModel
import com.szabist.zabapp1.viewmodel.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CheckoutScreen(
    navController: NavController,
    userId: String,
    userViewModel: UserViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
    orderViewModel: OrderViewModel = viewModel(),
    monthlyBillViewModel: MonthlyBillViewModel = viewModel()
) {
    LaunchedEffect(userId) {
        userViewModel.fetchUserById(userId)
    }

    val cartItems by cartViewModel.cartItems.collectAsState()
    val currentUserRole by userViewModel.currentUserRole.collectAsState(initial = null)
    val currentUser by userViewModel.currentUser.collectAsState()
    val userName = currentUser?.username ?: "Unknown"
    var paymentMethod by remember { mutableStateOf("cash") }
    Scaffold(
        topBar = {
            // Add a top app bar with a "Go Back" button
            androidx.compose.material3.TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go Back")
                    }
                }
            )
        }
    ) { padding ->

        if (currentUserRole == null) {
            // Loading state
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Loading...", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Image at the top
                Image(
                    painter = painterResource(id = R.drawable.checkout),
                    contentDescription = "Checkout Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

                // Cart items list
                Text(
                    text = "Cart Items",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Start
                )
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                ) {
                    items(cartItems) { cartItem ->
                        CartItemCard(cartItem = cartItem)
                    }
                }

                // Subtotal
                val subtotal = cartItems.sumOf { it.quantity * it.menuItem.price }
                Text(
                    text = "Subtotal: PKR $subtotal",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End
                )

                // Payment method selection
                Text(
                    text = "Select Payment Method",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                RadioButtonWithLabel(paymentMethod, "cash", "Pay by Cash") {
                    paymentMethod = "cash"
                }
                if (currentUserRole == "teacher" || currentUserRole == "hosteler") {
                    RadioButtonWithLabel(paymentMethod, "bill", "Add to Bill") {
                        paymentMethod = "bill"
                    }
                }

                // Place order button
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val order = Order(
                            userId = userId,
                            userName = userName,
                            items = cartItems.map { it.menuItem },
                            totalAmount = subtotal,
                            paymentMethod = paymentMethod
                        )
                        if (paymentMethod == "cash") {
                            processOrder(order, cartViewModel, orderViewModel, navController)
                        } else {
                            processBillOrder(
                                order,
                                cartViewModel,
                                orderViewModel,
                                monthlyBillViewModel,
                                navController
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Place Order", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
fun CartItemCard(cartItem: CartItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.menuItem.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "PKR ${cartItem.menuItem.price} x ${cartItem.quantity} = PKR ${cartItem.quantity * cartItem.menuItem.price}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun processOrder(
    order: Order,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    navController: NavController
) {
    orderViewModel.addOrder(order, onSuccess = { success, orderId ->
        if (success && orderId != null) {
            cartViewModel.clearCart()
            navController.navigate("order_details/$orderId?fromCheckout=true") {
                launchSingleTop = true
            }
        } else {
            Log.e("CheckoutScreen", "Failed to process order")
        }
    }, onFailure = {
        Log.e("CheckoutScreen", "Failed to process order")
    })
}

@RequiresApi(Build.VERSION_CODES.O)
fun processBillOrder(
    order: Order,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    monthlyBillViewModel: MonthlyBillViewModel,
    navController: NavController
) {
    order.status = "pending" // Set initial status to pending
    orderViewModel.addOrder(order, onSuccess = { success, orderId ->
        if (success && orderId != null) {
            cartViewModel.clearCart()
            navController.navigate("order_details/$orderId?fromCheckout=true") {
                launchSingleTop = true
            }
        } else {
            Log.e("CheckoutScreen", "Failed to process bill order")
        }
    }, onFailure = {
        Log.e("CheckoutScreen", "Failed to process bill order")
    })
}

@Composable
fun RadioButtonWithLabel(
    selectedMethod: String,
    thisMethod: String,
    label: String,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selectedMethod == thisMethod,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
