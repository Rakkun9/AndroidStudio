package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.test.ui.auth.AuthViewModel
import com.example.test.ui.auth.AuthViewModelFactory
import com.example.test.ui.navegation.AppRoutes
import com.example.test.ui.screens.InitialScreen
import com.example.test.ui.screens.LoginScreen
import com.example.test.ui.screens.ProductListScreen
import com.example.test.ui.screens.ProfileScreen
import com.example.test.ui.screens.RegistrationScreen
import com.example.test.ui.screens.ShoppingCartScreen
import com.example.test.ui.theme.TestTheme
import com.example.test.ui.product.ProductViewModel
import com.example.test.ui.product.ProductViewModelFactory
import com.example.test.ui.screens.CreateProductScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(applicationContext)
                )
                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.INITIAL
                ) {

                    composable(route = AppRoutes.INITIAL) {
                        InitialScreen(
                            authViewModel = authViewModel,
                            onNavigateToRegistration = {
                                navController.navigate(AppRoutes.REGISTRATION)
                            },
                            onNavigateToLogin = {
                                navController.navigate(AppRoutes.LOGIN)
                            },
                            onGoogleLoginSuccess = {
                                navController.navigate(AppRoutes.PRODUCT_LIST) {

                                    popUpTo(AppRoutes.INITIAL) {
                                        inclusive = true
                                    }

                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(route = AppRoutes.REGISTRATION) {
                        RegistrationScreen(
                            authViewModel = authViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToLogin = {
                                navController.navigate(AppRoutes.LOGIN) {
                                    popUpTo(AppRoutes.REGISTRATION) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }

                            }
                        )
                    }
                    composable(route = AppRoutes.LOGIN) {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onLoginSuccess = { userId, userRole ->
                                android.util.Log.d(
                                    "MainActivity",
                                    "Login Exitoso. UserID: $userId, Role: $userRole. Navegando a ProductList."
                                )

                                navController.navigate(AppRoutes.PRODUCT_LIST) {
                                    popUpTo(AppRoutes.INITIAL) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            },
                            onForgotPassword = {

                            }
                        )
                    }
                    composable(route = AppRoutes.PRODUCT_LIST) {
                        ProductListScreen(
                            onNavigateToCart = {
                                navController.navigate(AppRoutes.SHOPPING_CART)
                            },
                            onNavigateToProfile = {
                                navController.navigate(AppRoutes.PROFILE)
                            },
                            onNavigateToCreateProduct = { // <-- AÑADE ESTO
                                navController.navigate(AppRoutes.CREATE_PRODUCT)
                            },
                            authViewModel = authViewModel,
                        )
                    }
                    composable(route = AppRoutes.SHOPPING_CART) {
                        ShoppingCartScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(route = AppRoutes.PROFILE) {
                        ProfileScreen(
                            authViewModel = authViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onAccountDeletedAndNavigateToInitial = {
                                navController.navigate(AppRoutes.INITIAL) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(route = AppRoutes.CREATE_PRODUCT) {
                        val productViewModel: ProductViewModel =
                            viewModel(factory = ProductViewModelFactory(applicationContext))
                        CreateProductScreen(
                            productViewModel = productViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

