package com.szabist.zabcafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.szabist.zabcafe.repository.MenuRepository
import com.szabist.zabcafe.repository.OrderRepository
import com.szabist.zabcafe.repository.UserRepository
import com.szabist.zabcafe.ui.screens.*
import com.szabist.zabcafe.ui.theme.ZABcafeTheme
import com.szabist.zabcafe.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZABcafeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val userRepository = UserRepository()
    val orderRepository = OrderRepository()
    val menuRepository = MenuRepository()

    val userViewModel: UserViewModel = viewModelInstance { UserViewModel(userRepository) }
    val loginViewModel: LoginViewModel = viewModelInstance { LoginViewModel(userRepository) }
    val cartViewModel: CartViewModel = viewModelInstance { CartViewModel(orderRepository) }
    val menuViewModel: MenuViewModel = viewModelInstance { MenuViewModel(menuRepository) }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(loginViewModel = loginViewModel) {
                navController.navigate("dashboard")
            }
        }
        composable("dashboard") {
            DashboardScreen()
        }
        composable("register") {
            RegisterScreen(registerViewModel = userViewModel) {
                navController.navigate("login")
            }
        }
        composable("menu") {
            MenuScreen(menuViewModel = menuViewModel)
        }
        composable("cart") {
            CartScreen(cartViewModel = cartViewModel) {
                navController.navigate("checkout")
            }
        }
        composable("checkout") {
            CheckoutScreen()
        }
        composable("userManagement") {
            UserManagementScreen(userViewModel = userViewModel)
        }
    }
}

@Composable
inline fun <VM : ViewModel> viewModelInstance(crossinline factory: () -> VM): VM {
    return ViewModelProvider(LocalViewModelStoreOwner.current!!, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return factory() as T
        }
    })[factory::class.java]
}