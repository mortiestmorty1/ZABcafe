package com.szabist.zabcafe

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.szabist.zabcafe.repository.CartRepository
import com.szabist.zabcafe.repository.MenuRepository
import com.szabist.zabcafe.repository.OrderRepository
import com.szabist.zabcafe.repository.UserRepository
import com.szabist.zabcafe.ui.screens.AddMenuItemScreen
import com.szabist.zabcafe.ui.screens.AdminDashboard
import com.szabist.zabcafe.ui.screens.AdminRegisterUserScreen
import com.szabist.zabcafe.ui.screens.CartScreen
import com.szabist.zabcafe.ui.screens.EditMenuItemScreen
import com.szabist.zabcafe.ui.screens.EditUserScreen
import com.szabist.zabcafe.ui.screens.LoginScreen
import com.szabist.zabcafe.ui.screens.MenuManagementScreen
import com.szabist.zabcafe.ui.screens.RegisterScreen
import com.szabist.zabcafe.ui.screens.UserDashboard
import com.szabist.zabcafe.ui.screens.UserManagementScreen
import com.szabist.zabcafe.ui.theme.ZABcafeTheme
import com.szabist.zabcafe.viewmodel.AdminDashboardViewModel
import com.szabist.zabcafe.viewmodel.AdminDashboardViewModelFactory
import com.szabist.zabcafe.viewmodel.CartViewModel
import com.szabist.zabcafe.viewmodel.CartViewModelFactory
import com.szabist.zabcafe.viewmodel.LoginViewModel
import com.szabist.zabcafe.viewmodel.LoginViewModelFactory
import com.szabist.zabcafe.viewmodel.MenuViewModel
import com.szabist.zabcafe.viewmodel.MenuViewModelFactory
import com.szabist.zabcafe.viewmodel.RegisterViewModel
import com.szabist.zabcafe.viewmodel.RegisterViewModelFactory
import com.szabist.zabcafe.viewmodel.UserDashboardViewModel
import com.szabist.zabcafe.viewmodel.UserDashboardViewModelFactory
import com.szabist.zabcafe.viewmodel.UserViewModel
import com.szabist.zabcafe.viewmodel.UserViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            ZABcafeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    SetupAuthenticationListener(navController)
                    AppNavigation(navController)
                }
            }
        }
    }

    private fun SetupAuthenticationListener(navController: NavHostController) {
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                // Navigate to dashboard if not already there
                if (navController.currentDestination?.route != "dashboard") {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            } else {
                // Navigate to login if not already there
                if (navController.currentDestination?.route != "login") {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
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
        val cartRepository = CartRepository()

        val registerViewModel: RegisterViewModel =
            viewModelFactoryInstance { RegisterViewModelFactory(userRepository) }
        val loginViewModel: LoginViewModel =
            viewModelFactoryInstance { LoginViewModelFactory(userRepository) }
        val adminDashboardViewModel: AdminDashboardViewModel =
            viewModelFactoryInstance { AdminDashboardViewModelFactory(orderRepository, userRepository) }
        val userDashboardViewModel: UserDashboardViewModel =
            viewModelFactoryInstance { UserDashboardViewModelFactory(userRepository) }
        val menuViewModel: MenuViewModel =
            viewModelFactoryInstance { MenuViewModelFactory(menuRepository) }
        val userViewModel : UserViewModel =
            viewModelFactoryInstance {UserViewModelFactory(userRepository)}

        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                val loginViewModel: LoginViewModel = viewModelFactoryInstance {
                    LoginViewModelFactory(userRepository)
                }
                LoginScreen(loginViewModel = loginViewModel, navController = navController)
            }

            composable("register") {
                RegisterScreen(registerViewModel = registerViewModel, navController = navController)
            }
            composable("adminDashboard") {
                AdminDashboard(navController = navController)
            }
            composable("userDashboard/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                UserDashboard(navController, userDashboardViewModel, userId)
            }
            composable("menuManagement") {
                MenuManagementScreen(navController, menuViewModel)
            }
            composable("userManagement") {
                UserManagementScreen(navController = navController, userViewModel = userViewModel)
            }
            composable("addUser") {
                AdminRegisterUserScreen(navController = navController, userViewModel = userViewModel)
            }
            composable("editUser/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                Log.d("MainActivity", "Navigating to EditUserScreen with userId: $userId")
                EditUserScreen(navController, userId, userViewModel)
            }
            composable("menuManagement") {
                MenuManagementScreen(navController = navController, menuViewModel = menuViewModel)
            }
            composable("addMenuItem") {
                AddMenuItemScreen(navController = navController, menuViewModel = menuViewModel)
            }
            composable("editMenuItem/{menuItemId}") { backStackEntry ->
                val menuItemId = backStackEntry.arguments?.getString("menuItemId") ?: return@composable
                EditMenuItemScreen(navController, menuViewModel, menuItemId)
            }
            composable("cart") {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@composable
                val cartViewModel: CartViewModel = viewModelFactoryInstance { CartViewModelFactory(userId, cartRepository) }
                CartScreen(navController, cartViewModel)
            }
        }
    }

    @Composable
    inline fun <reified VM : ViewModel> viewModelFactoryInstance(
        noinline factory: () -> ViewModelProvider.Factory
    ): VM {
        return ViewModelProvider(LocalViewModelStoreOwner.current!!, factory())[VM::class.java]
    }
}