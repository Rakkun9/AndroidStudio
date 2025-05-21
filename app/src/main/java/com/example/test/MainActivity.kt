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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(applicationContext) // Usa applicationContext
                )
                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.INITIAL
                ) {

                    composable(route = AppRoutes.INITIAL) {
                        InitialScreen( // Llama a tu pantalla inicial
                            authViewModel = authViewModel,
                            onNavigateToRegistration = {
                                navController.navigate(AppRoutes.REGISTRATION)
                            },
                            onNavigateToLogin = {
                                navController.navigate(AppRoutes.LOGIN)
                            },
                            onGoogleLoginSuccess = { // <-- AÑADE ESTA SECCIÓN
                                // Acción al hacer login exitoso con Google:
                                navController.navigate(AppRoutes.PRODUCT_LIST) {
                                    // Elimina del historial hasta INITIAL (inclusive)
                                    popUpTo(AppRoutes.INITIAL) {
                                        inclusive = true
                                    }
                                    // Evita lanzar múltiples copias si se pulsa rápido
                                    launchSingleTop = true
                                }
                            } // <-- FIN DE LA LAMBDA onGoogleLoginSuccess
                        )
                    }
                    composable(route = AppRoutes.REGISTRATION) {
                        RegistrationScreen(
                            authViewModel = authViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToLogin = { // <-- AÑADE ESTA LAMBDA
                                // Navega a la pantalla de login, y si vienes del registro,
                                // quizás quieras limpiar el backstack para que no vuelva al registro
                                // al presionar "atrás" desde el login.
                                navController.navigate(AppRoutes.LOGIN) {
                                    popUpTo(AppRoutes.REGISTRATION) { inclusive = true } // Opción 1: Quita el registro del stack
                                    // O si quieres que pueda volver al registro desde login:
                                    // navController.navigate(AppRoutes.LOGIN)
                                    launchSingleTop = true // Evita múltiples instancias de login
                                }

                            }
                        )
                    }
                    composable(route = AppRoutes.LOGIN) {
                        LoginScreen( // Llama a la pantalla de formulario
                            authViewModel = authViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onLoginSuccess = { userId, userRole -> // <<--- MODIFICADO AQUÍ para aceptar los parámetros
                                android.util.Log.d("MainActivity", "Login Exitoso. UserID: $userId, Role: $userRole. Navegando a ProductList.")

                                navController.navigate(AppRoutes.PRODUCT_LIST) {
                                    popUpTo(AppRoutes.INITIAL) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }, // <-- FIN DE LA LAMBDA onLoginSuccess
                            onForgotPassword = {
                                // Lógica para olvidar contraseña si la implementas
                            }
                        )
                    }
                    composable(route = AppRoutes.PRODUCT_LIST) {
                        ProductListScreen(
                            onNavigateToCart = {
                                navController.navigate(AppRoutes.SHOPPING_CART)
                            },
                            onNavigateToProfile = { // <-- AÑADE ESTO
                                // Aquí necesitamos saber el ID del usuario actual.
                                // Lo ideal es que AuthViewModel ya tenga _currentUserId seteado.
                                // ProfileScreen tomará ese ID del AuthViewModel.
                                navController.navigate(AppRoutes.PROFILE)
                            }
                            // , onRecentlyViewedItemClick = { ... } // Si los tienes
                            // , onInterestItemClick = { ... }
                        )
                    }
                    composable(route = AppRoutes.SHOPPING_CART) {
                        ShoppingCartScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(route = AppRoutes.PROFILE) {
                        ProfileScreen(
                            authViewModel = authViewModel, // Esto ya está bien, se pasa la instancia única
                            onNavigateBack = { navController.popBackStack() },
                            onAccountDeletedAndNavigateToInitial = { // <-- AÑADE ESTA LAMBDA COMPLETA
                                // Acción después de que la cuenta es eliminada exitosamente:
                                // Navega a la pantalla inicial y limpia TODO el historial de atrás.
                                navController.navigate(AppRoutes.INITIAL) {
                                    // Pop up to the start destination of the graph
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true // Incluye el start destination en el pop
                                    }
                                    // Evita lanzar múltiples instancias de la pantalla inicial
                                    launchSingleTop = true
                                }
                            } // <-- FIN DE LA LAMBDA onAccountDeletedAndNavigateToInitial
                        )
                    }
                }
            }
        }
    }
}

