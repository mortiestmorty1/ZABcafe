package com.szabist.zabapp1.ui.user

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    monthlyBillViewModel: MonthlyBillViewModel = viewModel()  // Inject MonthlyBillViewModel
) {
    LaunchedEffect(userId) {
        userViewModel.fetchUserById(userId)
    }

    val cartItems by cartViewModel.cartItems.collectAsState()
    val currentUserRole by userViewModel.currentUserRole.collectAsState(initial = "")
    var paymentMethod by remember { mutableStateOf("cash") }  // Default to cash

    Column(modifier = Modifier.padding(16.dp)) {
        val subtotal = cartItems.sumOf { it.quantity * it.menuItem.price }
        Text("Subtotal: $$subtotal")

        // Payment method selection
        RadioButtonWithLabel(paymentMethod, "cash", "Pay by Cash") {
            paymentMethod = "cash"
        }

        if (currentUserRole in listOf("teacher", "hostilities")) {
            RadioButtonWithLabel(paymentMethod, "bill", "Add to Bill") {
                paymentMethod = "bill"
            }
        }

        // Button to place order
        Button(
            onClick = {
                val order = Order(
                    userId = userId,
                    items = cartItems.map { it.menuItem },
                    totalAmount = subtotal,
                    paymentMethod = paymentMethod
                )
                if (paymentMethod == "cash") {
                    processOrder(order, cartViewModel, orderViewModel, navController)
                } else {
                    processBillOrder(order, cartViewModel,orderViewModel, monthlyBillViewModel, navController)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Place Order")
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
    orderViewModel.addOrder(order, order.userId, onSuccess = { success, orderId ->
        if (success && orderId != null) {
            cartViewModel.clearCart()
            navController.navigate("order_details/$orderId") { popUpTo("menu") { inclusive = true } }
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
    monthlyBillViewModel.handleOrder(order, order.userId, orderViewModel) { success, orderId ->
        if (success && orderId != null) {
            cartViewModel.clearCart()
            navController.navigate("order_details/$orderId") { popUpTo("menu") { inclusive = true } }
        } else {
            Log.e("CheckoutScreen", "Failed to process bill order")
        }
    }
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