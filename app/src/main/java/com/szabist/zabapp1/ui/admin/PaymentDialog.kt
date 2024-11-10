// PaymentDialog.kt
package com.szabist.zabapp1.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PaymentDialog(
    totalAmount: Double,
    arrears: Double,
    onConfirmFullPayment: () -> Unit,
    onConfirmPartialPayment: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Payment Type") },
        text = {
            Column {
                Text("Total Amount: $$totalAmount")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Current Arrears: $$arrears")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onConfirmFullPayment, modifier = Modifier.fillMaxWidth()) {
                    Text("Mark as Fully Paid")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    val partialAmount = totalAmount * 0.5 // Example: 50% partial payment
                    onConfirmPartialPayment(partialAmount)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Mark as Partially Paid (50%)")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
