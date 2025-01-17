package com.szabist.zabapp1.ui.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.szabist.zabapp1.data.model.MenuItem
import com.szabist.zabapp1.viewmodel.MenuViewModel

@Composable
fun EditMenuItemScreen(
    navController: NavController,
    menuItemId: String,
    menuViewModel: MenuViewModel = viewModel()
) {
    var menuItem by remember { mutableStateOf<MenuItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(menuItemId) {
        menuViewModel.getMenuItemById(menuItemId) { item ->
            menuItem = item
            isLoading = false
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(32.dp)
            )
        }
    } else {
        menuItem?.let { item ->
            EditMenuItemForm(item, navController, menuViewModel)
        }
    }
}

@Composable
fun EditMenuItemForm(
    menuItem: MenuItem,
    navController: NavController,
    menuViewModel: MenuViewModel
) {
    var name by remember { mutableStateOf(menuItem.name) }
    var description by remember { mutableStateOf(menuItem.description) }
    var price by remember { mutableStateOf(menuItem.price.toString()) }
    var available by remember { mutableStateOf(menuItem.available) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var currentCategory by remember { mutableStateOf(menuItem.categoryId) }
    val categories by menuViewModel.categories.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> selectedImageUri = uri }
    )

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(
                text = "Edit Menu Item",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Name Icon")
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Description Icon")
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Price Input
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = price.toDoubleOrNull() == null,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Price Icon")
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Availability Checkbox
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = available,
                    onCheckedChange = { available = it }
                )
                Text("Available", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Dropdown
            Text("Category", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentCategory?.let {
                    Text(
                        text = it.ifEmpty { "Select a category" },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            currentCategory = category
                            expanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Image Picker Button
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Select Image",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select Image")
            }

            // Existing Image Preview
            menuItem.imageUrl?.let {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = it,
                    contentDescription = "Existing Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            // Selected Image Preview
            selectedImageUri?.let {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = it,
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Update Button
            Button(
                onClick = {
                    isLoading = true
                    val updatedItem = menuItem.copy(
                        name = name,
                        description = description,
                        price = price.toDoubleOrNull() ?: 0.0,
                        available = available,
                        categoryId = currentCategory
                    )
                    menuViewModel.updateMenuItem(updatedItem, selectedImageUri) { success ->
                        isLoading = false
                        if (success) navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading && name.isNotBlank() && description.isNotBlank() && price.toDoubleOrNull() != null
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Update Icon",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isLoading) "Updating..." else "Update")
            }
        }
    }
}
