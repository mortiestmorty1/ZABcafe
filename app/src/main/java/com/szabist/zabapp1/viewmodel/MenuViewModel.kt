package com.szabist.zabapp1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.szabist.zabapp1.data.model.MenuItem
import com.szabist.zabapp1.data.repository.MenuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    private val menuRepository = MenuRepository()
    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems

    init {
        loadMenuItems()
    }

    fun loadMenuItems() {
        viewModelScope.launch(Dispatchers.IO) {
            menuRepository.getMenuItems { items ->
                _menuItems.value = items
            }
        }
    }


    fun addMenuItem(menuItem: MenuItem) {
        viewModelScope.launch(Dispatchers.IO) {
            menuRepository.addMenuItem(menuItem)
            loadMenuItems()
        }
    }

    fun updateMenuItem(menuItem: MenuItem) {
        viewModelScope.launch(Dispatchers.IO) {
            menuRepository.updateMenuItem(menuItem)
            loadMenuItems()
        }
    }

    fun deleteMenuItem(menuItemId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            menuRepository.deleteMenuItem(menuItemId)
            loadMenuItems()
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