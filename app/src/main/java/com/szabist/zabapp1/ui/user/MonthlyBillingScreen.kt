package com.szabist.zabapp1.ui.user


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.MonthlyBill
import com.szabist.zabapp1.viewmodel.MonthlyBillViewModel
import com.szabist.zabapp1.viewmodel.UserViewModel


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyBillingScreen(
    navController: NavController,
    userId: String,
    userViewModel: UserViewModel = viewModel()  // Assuming UserViewModel can provide the user's role
) {
    val monthlyBillViewModel: MonthlyBillViewModel = viewModel()

    // Load user details and bills when the component is composed
    LaunchedEffect(key1 = userId) {
        userViewModel.fetchUserById(userId)
        monthlyBillViewModel.loadBillsForUser(userId)
    }

    // Collect the necessary states
    val bills by monthlyBillViewModel.monthlyBills.collectAsState()
    val currentUserRole by userViewModel.currentUserRole.collectAsState(initial = "")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Monthly Bills") }) }
    ) { padding ->
        if (currentUserRole in listOf("teacher", "hostilities")) {
            // User has access
            LazyColumn(modifier = Modifier.padding(padding)) {
                if (bills.isEmpty()) {
                    item { Text("No bills available.", modifier = Modifier.padding(16.dp)) }
                } else {
                    items(bills.sortedByDescending { it.month }) { bill ->
                        BillItem(bill) { billId ->
                            navController.navigate("bill_details/$billId")
                        }
                    }
                }
            }
        } else {
            // User does not have access
            Column(modifier = Modifier.padding(padding).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("You do not have access to view this page.", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.error)
                Button(onClick = { navController.popBackStack() }) {
                    Text("Go Back")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BillItem(bill: MonthlyBill, navigateToDetails: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = { navigateToDetails(bill.billId) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Month: ${bill.month}")
                Text("Total: $${bill.amount}", style = MaterialTheme.typography.bodyLarge)
            }

            Icon(
                imageVector = if (bill.paid) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                contentDescription = if (bill.paid) "Paid" else "Unpaid",
                tint = if (bill.paid) Color.Green else Color.Red
            )
        }
    }
}
