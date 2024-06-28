package com.szabist.zabcafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabcafe.model.MenuItem
import com.szabist.zabcafe.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel(private val menuRepository: MenuRepository) : ViewModel() {

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems

    init {
        loadMenuItems()
    }

    private fun loadMenuItems() {
        viewModelScope.launch {
            val items = menuRepository.fetchMenuItems()
            _menuItems.value = items
        }
    }

    fun addMenuItem(menuItem: MenuItem) {
        viewModelScope.launch {
            val success = menuRepository.addMenuItem(menuItem)
            if (success) {
                loadMenuItems() // Reload data to update UI
            }
            // Handle failure case if needed
        }
    }

    fun updateMenuItem(menuItem: MenuItem) {
        viewModelScope.launch {
            val success = menuRepository.updateMenuItem(menuItem.itemId, menuItem)
            if (success) {
                loadMenuItems() // Reload data to update UI
            }
            // Handle failure case if needed
        }
    }

    fun deleteMenuItem(itemId: String) {
        viewModelScope.launch {
            val success = menuRepository.deleteMenuItem(itemId)
            if (success) {
                loadMenuItems() // Reload data to reflect changes
            }
            // Handle failure case if needed
        }
    }
}