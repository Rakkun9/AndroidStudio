package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.test.ui.navegation.AppRoutes
import com.example.test.ui.screens.InitialScreen
import com.example.test.ui.screens.LoginScreen
import com.example.test.ui.screens.ProductListScreen
import com.example.test.ui.screens.RegistrationScreen
import com.example.test.ui.screens.ShoppingCartScreen

import com.example.test.ui.theme.TestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                val navController = rememberNavController()


                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.INITIAL
                ) {

                    composable(route = AppRoutes.INITIAL) {
                        InitialScreen( // Llama a tu pantalla inicial
                            onNavigateToRegistration = {
                                navController.navigate(AppRoutes.REGISTRATION) // Navega a Registro
                            },
                            onNavigateToLogin = {
                                navController.navigate(AppRoutes.LOGIN) // Navega al formulario de Login
                            }
                        )
                    }

                    composable(route = AppRoutes.REGISTRATION) {
                        RegistrationScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(route = AppRoutes.LOGIN) {
                        LoginScreen( // Llama a la pantalla de formulario
                            onNavigateBack = { navController.popBackStack() },
                            onLoginSuccess = {
                                // Acción al hacer login exitoso:
                                navController.navigate(AppRoutes.PRODUCT_LIST) {
                                    popUpTo(AppRoutes.INITIAL) {
                                        inclusive = true
                                    }
                                    // 3. Evita lanzar múltiples copias si se pulsa rápido
                                    launchSingleTop = true
                                }
                            }, // <-- FIN DE LA LAMBDA onLoginSuccess
                            onForgotPassword = {

                            }
                        )
                    }

                    composable(route = AppRoutes.PRODUCT_LIST) {
                        ProductListScreen(
                            onNavigateToCart = {
                                navController.navigate(AppRoutes.SHOPPING_CART)
                            }
                        )
                    }
                    composable(route = AppRoutes.SHOPPING_CART) {
                        ShoppingCartScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                }
            }
        }
    }
}

