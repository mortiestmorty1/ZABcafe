package com.szabist.zabapp1.ui.user

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.Order
import com.szabist.zabapp1.viewmodel.CartViewModel
import com.szabist.zabapp1.viewmodel.MonthlyBillViewModel
import com.szabist.zabapp1.viewmodel.OrderViewModel
import com.szabist.zabapp1.viewmodel.UserViewModel


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
    val currentUserRole by userViewModel.currentUserRole.collectAsState(initial = null) // Set initial to null
    val currentUser by userViewModel.currentUser.collectAsState()
    val userName = currentUser?.username ?: "Unknown" // Get user name from currentUser

    var paymentMethod by remember { mutableStateOf("cash") }

    Log.d("CheckoutScreen", "Current role: $currentUserRole") // Log role

    if (currentUserRole == null) {
        // Show loading indicator until role is available
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Loading...", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            val subtotal = cartItems.sumOf { it.quantity * it.menuItem.price }
            Text("Subtotal: PKR $subtotal")

            // Payment method selection
            RadioButtonWithLabel(paymentMethod, "cash", "Pay by Cash") {
                paymentMethod = "cash"
            }

            // Show "Add to Bill" option only for "teacher" and "hosteler" roles
            if (currentUserRole == "teacher" || currentUserRole == "hosteler") {
                RadioButtonWithLabel(paymentMethod, "bill", "Add to Bill") {
                    paymentMethod = "bill"
                }
            }

            Button(
                onClick = {
                    val order = Order(
                        userId = userId,
                        userName = userName, // Pass the userName here
                        items = cartItems.map { it.menuItem },
                        totalAmount = subtotal,
                        paymentMethod = paymentMethod
                    )
                    if (paymentMethod == "cash") {
                        processOrder(order, cartViewModel, orderViewModel, navController)
                    } else {
                        processBillOrder(order, cartViewModel, orderViewModel, monthlyBillViewModel, navController)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Place Order")
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
            navController.navigate("order_details/$orderId") {
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
            navController.navigate("order_details/$orderId") {
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
    Row(modifier = Modifier.padding(8.dp)) {
        RadioButton(
            selected = selectedMethod == thisMethod,
            onClick = onSelect
        )
        Text(text = label)
    }
}