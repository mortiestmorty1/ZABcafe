// UserListScreen.kt
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.szabist.zabapp1.data.model.User
import com.szabist.zabapp1.viewmodel.UserViewModel

@Composable
fun UserListScreen(navController: NavController, userViewModel: UserViewModel = viewModel()) {
    val users by userViewModel.users.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.fetchUsers()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select a User", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            // Filter out users with role "admin" and "student"
            items(users.filter { it.role != "admin" && it.role != "student" }) { user ->
                UserRow(user, onClick = { navController.navigate("user_month_list/${user.id}") })
            }
        }
    }
}

@Composable
fun UserRow(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Username: ${user.username}", style = MaterialTheme.typography.bodyLarge)
            Text("Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
