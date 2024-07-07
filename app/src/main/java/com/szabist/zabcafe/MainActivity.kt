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
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.szabist.zabcafe.repository.CartRepository
import com.szabist.zabcafe.repository.MenuRepository
import com.szabist.zabcafe.repository.OrderRepository
import com.szabist.zabcafe.repository.UserRepository
import com.szabist.zabcafe.ui.navigation.AppNavigation
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
                    AppContent(navController)
                }
            }
        }
    }

    private fun SetupAuthenticationListener(navController: NavHostController) {
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                if (navController.currentDestination?.route != "dashboard") {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            } else {
                if (navController.currentDestination?.route != "login") {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            }
        }
    }

    @Composable
    fun AppContent(navController: NavHostController) {
        val userRepository = UserRepository()
        val orderRepository = OrderRepository()
        val menuRepository = MenuRepository()
        val cartRepository = CartRepository()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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
        val userViewModel: UserViewModel =
            viewModelFactoryInstance { UserViewModelFactory(userRepository) }
        val cartViewModel: CartViewModel =
            viewModelFactoryInstance { CartViewModelFactory(userId, cartRepository) }

        AppNavigation(
            navController = navController,
            registerViewModel = registerViewModel,
            loginViewModel = loginViewModel,
            adminDashboardViewModel = adminDashboardViewModel,
            userDashboardViewModel = userDashboardViewModel,
            menuViewModel = menuViewModel,
            userViewModel = userViewModel,
            cartViewModel = cartViewModel
        )
    }

    @Composable
    inline fun <reified VM : ViewModel> viewModelFactoryInstance(
        noinline factory: () -> ViewModelProvider.Factory
    ): VM {
        return ViewModelProvider(LocalViewModelStoreOwner.current!!, factory())[VM::class.java]
    }
}