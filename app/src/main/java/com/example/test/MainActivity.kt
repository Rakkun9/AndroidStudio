// Dentro de /app/java/com.example.test/MainActivity.kt
package com.example.test // Asegúrate que este es tu paquete base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
// --- IMPORTACIONES NUEVAS PARA NAVEGACIÓN ---
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// --- IMPORTACIONES DE TUS PANTALLAS Y RUTAS ---
import com.example.test.ui.navegation.AppRoutes // Importa tus rutas desde Navigation.kt
import com.example.test.ui.screens.LoginScreen // Importa tu pantalla de Login
import com.example.test.ui.screens.RegistrationScreen // Importa tu pantalla de Registro
// --- IMPORTACIÓN DE TU TEMA ---
import com.example.test.ui.theme.TestTheme // Importa tu tema

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Esto es para usar pantalla completa, puedes mantenerlo
        setContent {
            TestTheme { // Envuelve todo en tu tema como ya lo tenías
                // --- ¡AQUÍ EMPIEZA LA CONFIGURACIÓN DE NAVEGACIÓN! ---

                // 1. Creamos el controlador que manejará la navegación
                val navController = rememberNavController()

                // 2. Creamos el NavHost, que es el contenedor de tus pantallas navegables
                NavHost(
                    navController = navController, // Le pasamos el controlador
                    startDestination = AppRoutes.LOGIN // Le decimos que empiece en la pantalla de Login
                ) {
                    // 3. Definimos cada pantalla (Composable) asociada a una ruta

                    // Pantalla de Login
                    composable(route = AppRoutes.LOGIN) {
                        LoginScreen(
                            // Cuando LoginScreen quiera navegar al registro...
                            onNavigateToRegistration = {
                                // ...le decimos al navController que navegue a la ruta REGISTRATION
                                navController.navigate(AppRoutes.REGISTRATION)
                            }
                            // Aquí podrías añadir la acción para el botón "Iniciar Sesión" si tuvieras otra pantalla
                            // onNavigateToLoginReal = { navController.navigate("login_real") }
                        )
                    }

                    // Pantalla de Registro
                    composable(route = AppRoutes.REGISTRATION) {
                        RegistrationScreen(
                            // Cuando RegistrationScreen quiera volver atrás...
                            onNavigateBack = {
                                // ...le decimos al navController que vuelva a la pantalla anterior en la pila
                                navController.popBackStack()
                            }
                        )
                    }

                    // Aquí añadirías las otras pantallas (ProductList, ShoppingCart) si las conectas
                    /*
                    composable(route = AppRoutes.PRODUCT_LIST) {
                        ProductListScreen(...)
                    }
                    composable(route = AppRoutes.SHOPPING_CART) {
                        ShoppingCartScreen(...)
                    }
                    */
                } // --- FIN DEL NavHost ---
            } // Fin TestTheme
        } // Fin setContent
    } // Fin onCreate
}

// --- El Composable Greeting y GreetingPreview ya no son necesarios aquí ---
// Puedes borrarlos de este archivo si quieres, ya que NavHost maneja qué pantalla mostrar.
/*
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) { ... }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() { ... }
*/