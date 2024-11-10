package com.szabist.zabapp1.ui.user


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
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
    userViewModel: UserViewModel = viewModel(),
    monthlyBillViewModel: MonthlyBillViewModel = viewModel()
) {
    LaunchedEffect(userId) {
        userViewModel.fetchUserById(userId)
        monthlyBillViewModel.loadBillsForUser(userId)
    }

    val bills by monthlyBillViewModel.monthlyBills.collectAsState()
    val currentUserRole by userViewModel.currentUserRole.collectAsState(initial = "")

    Log.d("MonthlyBillingScreen", "Current user role: $currentUserRole") // Log role

    Scaffold(
        topBar = { TopAppBar(title = { Text("Monthly Bills") }) }
    ) { padding ->
        when {
            currentUserRole?.isEmpty() ?: true -> {
                // Loading state if role is not yet populated
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Loading...", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
            currentUserRole == "student" -> {
                // Show restricted access message for students
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Access Restricted",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Gray
                    )
                    Text("Monthly billing details are only available to teachers and hostelers.")
                }
            }
            else -> {
                // Display bills for authorized roles
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
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillItem(bill: MonthlyBill, navigateToDetails: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navigateToDetails(bill.billId) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Month: ${bill.month}")
                Text("Total: PKR ${bill.amount}", style = MaterialTheme.typography.bodyLarge)
                Text("Paid: ${if (bill.paid) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
                Text("Arrears: PKR ${bill.arrears}", style = MaterialTheme.typography.bodyMedium)
            }
            Icon(
                imageVector = if (bill.paid) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                contentDescription = if (bill.paid) "Paid" else "Unpaid",
                tint = if (bill.paid) Color.Green else Color.Red
            )
        }
    }
}


