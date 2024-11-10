// MonthListScreen.kt
package com.szabist.zabapp1.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.YearMonth

@Composable
fun MonthListScreen(navController: NavController, userId: String) {
    // List of months with their numeric equivalents
    val months = listOf(
        "January" to "01", "February" to "02", "March" to "03", "April" to "04",
        "May" to "05", "June" to "06", "July" to "07", "August" to "08",
        "September" to "09", "October" to "10", "November" to "11", "December" to "12"
    )

    // Get the current year
    val currentYear = YearMonth.now().year

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select a Month", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(months) { (monthName, monthNumber) ->
                MonthRow(monthName) {
                    // Format yearMonth as "YYYY-MM"
                    val yearMonth = "$currentYear-$monthNumber"
                    navController.navigate("user_bills/$userId/$yearMonth")
                }
            }
        }
    }
}

@Composable
fun MonthRow(month: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(month, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
