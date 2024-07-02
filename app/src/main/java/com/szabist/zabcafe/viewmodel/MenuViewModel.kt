package com.szabist.zabcafe.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabcafe.model.MenuItem
import com.szabist.zabcafe.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel(private val menuRepository: MenuRepository) : ViewModel() {

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadMenuItems()
    }
    fun fetchMenuItemById(menuItemId: String, onResult: (MenuItem?) -> Unit) {
        viewModelScope.launch {
            try {
                val menuItem = menuRepository.fetchMenuItemById(menuItemId)
                onResult(menuItem)
            } catch (e: Exception) {
                Log.e("MenuViewModel", "Error fetching menu item: ${e.message}")
                onResult(null)
            }
        }
    }

    fun loadMenuItems() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val items = menuRepository.fetchMenuItems()
                _menuItems.emit(items)
            } catch (e: Exception) {
                _errorMessage.emit("Failed to fetch menu items: ${e.message}")
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    fun addMenuItem(menuItem: MenuItem) {
        viewModelScope.launch {
            val result = menuRepository.addMenuItem(menuItem)
            if (result) {
                loadMenuItems()  // Refresh the list of menu items
            } else {
                _errorMessage.value = "Failed to add menu item."
            }
        }
    }

    fun updateMenuItem(menuItem: MenuItem) {
        viewModelScope.launch {
            val result = menuRepository.updateMenuItem(menuItem.itemId, menuItem)
            if (result) {
                loadMenuItems()  // Refresh the list of menu items
            } else {
                _errorMessage.emit("Failed to update menu item.")
            }
        }
    }

    fun deleteMenuItem(menuItemId: String) {
        viewModelScope.launch {
            val result = menuRepository.deleteMenuItem(menuItemId)
            if (result) {
                _menuItems.value = _menuItems.value.filter { it.itemId != menuItemId }
            } else {
                _errorMessage.value = "Failed to delete menu item."
            }
        }
    }
}