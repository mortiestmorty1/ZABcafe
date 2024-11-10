package com.szabist.zabapp1.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.szabist.zabapp1.data.model.MenuItem
import com.szabist.zabapp1.data.repository.MenuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID


class MenuViewModel : ViewModel() {
    private val predefinedCategories = listOf("Ready to Eat", "Ready to Cook", "Beverages", "Dessert")
    private val menuRepository = MenuRepository()
    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems
    private val _categories = MutableStateFlow(predefinedCategories)
    val categories: StateFlow<List<String>> = _categories.asStateFlow()
    private val storageReference = FirebaseStorage.getInstance().reference
    init {
        loadMenuItems()
        Log.d("MenuViewModel", "Categories initialized: ${_categories.value}")
    }
    fun getCategoryNameById(categoryId: String?): String {
        return predefinedCategories.find { it == categoryId } ?: "Unknown Category"
    }

    fun loadMenuItems() {
        viewModelScope.launch(Dispatchers.IO) {
            menuRepository.getMenuItems { items ->
                _menuItems.value = items
            }
        }
    }

    fun addMenuItem(menuItem: MenuItem, imageUri: Uri?, onComplete: (Boolean) -> Unit) {
        if (imageUri != null) {
            uploadImage(imageUri) { imageUrl ->
                if (imageUrl != null) {
                    val newItem = menuItem.copy(imageUrl = imageUrl)
                    viewModelScope.launch(Dispatchers.IO) {
                        menuRepository.addMenuItem(newItem)
                        loadMenuItems()
                        onComplete(true)
                    }
                } else {
                    onComplete(false)
                }
            }
        } else {
            // If no image is provided, add the menu item directly
            viewModelScope.launch(Dispatchers.IO) {
                menuRepository.addMenuItem(menuItem)
                loadMenuItems()
                onComplete(true)
            }
        }
    }

    // Update menu item with image functionality
    fun updateMenuItem(menuItem: MenuItem, newImageUri: Uri?, onComplete: (Boolean) -> Unit) {
        if (newImageUri != null) {
            updateImage(menuItem.imageUrl, newImageUri) { newImageUrl ->
                if (newImageUrl != null) {
                    val updatedItem = menuItem.copy(imageUrl = newImageUrl)
                    viewModelScope.launch(Dispatchers.IO) {
                        menuRepository.updateMenuItem(updatedItem)
                        loadMenuItems()
                        viewModelScope.launch(Dispatchers.Main) { onComplete(true) } // Move callback to main thread
                    }
                } else {
                    viewModelScope.launch(Dispatchers.Main) { onComplete(false) }
                }
            }
        } else {
            // If no new image is provided, update the menu item directly
            viewModelScope.launch(Dispatchers.IO) {
                menuRepository.updateMenuItem(menuItem)
                loadMenuItems()
                viewModelScope.launch(Dispatchers.Main) { onComplete(true) } // Move callback to main thread
            }
        }
    }

    // Delete menu item with image functionality
    fun deleteMenuItem(menuItem: MenuItem, onComplete: (Boolean) -> Unit) {
        if (menuItem.imageUrl != null) {
            // Delete the image from Firebase Storage first
            deleteImage(menuItem.imageUrl) { success ->
                if (success) {
                    viewModelScope.launch(Dispatchers.IO) {
                        menuRepository.deleteMenuItem(menuItem.id)
                        loadMenuItems()
                        onComplete(true)
                    }
                } else {
                    onComplete(false)
                }
            }
        } else {
            // If no image is associated, delete the menu item directly
            viewModelScope.launch(Dispatchers.IO) {
                menuRepository.deleteMenuItem(menuItem.id)
                loadMenuItems()
                onComplete(true)
            }
        }
    }

    fun uploadImage(imageUri: Uri, onComplete: (String?) -> Unit) {
        val fileName = "menu_images/${UUID.randomUUID()}.jpg"
        val ref = storageReference.child(fileName)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                ref.putFile(imageUri).await()  // Upload file
                val downloadUrl = ref.downloadUrl.await()  // Get the download URL
                onComplete(downloadUrl.toString())  // Return the download URL
            } catch (e: Exception) {
                onComplete(null)
            }
        }
    }

    // Update an image (upload new one and delete old one)
    private fun updateImage(oldImageUrl: String?, newImageUri: Uri, onComplete: (String?) -> Unit) {
        if (oldImageUrl != null) {
            deleteImage(oldImageUrl) {
                uploadImage(newImageUri, onComplete)
            }
        } else {
            uploadImage(newImageUri, onComplete)
        }
    }

    // Firebase Storage image delete
    private fun deleteImage(imageUrl: String, onComplete: (Boolean) -> Unit) {
        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                ref.delete().await()
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }


    fun generateMenuItemId(): String {
        return menuRepository.generateMenuItemId()
    }

    fun getMenuItemById(menuItemId: String, callback: (MenuItem?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            menuRepository.getMenuItemById(menuItemId, callback)
        }
    }
}