package com.szabist.zabapp1.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.szabist.zabapp1.data.model.MenuItem
import com.szabist.zabapp1.viewmodel.MenuViewModel

@Composable
fun EditMenuItemScreen(navController: NavController, menuItemId: String, menuViewModel: MenuViewModel = viewModel()) {
    var menuItem by remember { mutableStateOf<MenuItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("") }

    LaunchedEffect(menuItemId) {
        menuViewModel.getMenuItemById(menuItemId) { item ->
            menuItem = item
            selectedCategory = item?.categoryId ?: ""
            isLoading = false
        }
    }

    if (isLoading) {
       // CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        menuItem?.let { item ->
            EditMenuItemForm(item,selectedCategory, navController, menuViewModel)
        }
    }
}

@Composable
fun EditMenuItemForm(
    menuItem: MenuItem,
    selectedCategory: String,
    navController: NavController,
    menuViewModel: MenuViewModel
) {
    var name by remember { mutableStateOf(menuItem.name) }
    var description by remember { mutableStateOf(menuItem.description) }
    var price by remember { mutableStateOf(menuItem.price.toString()) }
    var available by remember { mutableStateOf(menuItem.available) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }  // For category dropdown
    var currentCategory by remember { mutableStateOf(selectedCategory) }  // Track selected category
    val categories by menuViewModel.categories.collectAsState()

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> selectedImageUri = uri }
    )

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = available,
                    onCheckedChange = { available = it }
                )
                Text("Available")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category selection using radio buttons
            Text("Select Category", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            categories.forEach { category ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = (currentCategory == category),
                        onClick = { currentCategory = category },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = category)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select New Image")
            }

            // Show existing image preview if available
            menuItem.imageUrl?.let {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = it,
                    contentDescription = "Existing Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            // Show new image preview if an image is selected
            selectedImageUri?.let {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = it,
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    isLoading = true
                    val updatedItem = menuItem.copy(
                        name = name,
                        description = description,
                        price = price.toDouble(),
                        available = available,
                        categoryId = currentCategory
                    )

                    // Update menu item and ensure popBackStack runs on the main thread
                    menuViewModel.updateMenuItem(updatedItem, selectedImageUri) { success ->
                        isLoading = false
                        if (success) {
                            // Ensure navigation is done on the main thread
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && name.isNotBlank() && description.isNotBlank() && price.isNotBlank()
            ) {
                Text("Update Menu Item")
            }

        }
    }
}