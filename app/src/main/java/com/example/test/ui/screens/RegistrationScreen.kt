// Dentro de /app/java/com.example.test/ui/screens/RegistrationScreen.kt
// (Asegúrate que 'com.example.test' sea tu paquete real)

package com.example.test.ui.screens

// --- Importaciones Necesarias ---
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // Para hacer la columna scrollable si hay muchos campos
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.* // O androidx.compose.material.* si usas Material 2
import androidx.compose.runtime.* // Necesario para 'remember' si usamos estado más adelante
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
// Importa PasswordVisualTransformation si quieres ocultar la contraseña (opcional por ahora)
// import androidx.compose.ui.text.input.PasswordVisualTransformation

// --- La Pantalla Principal ---
@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar en Material 3
@Composable
fun RegistrationScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            // Barra superior con el título
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = { // <-- AÑADE ESTO
                    IconButton(onClick = onNavigateBack) { // Llama a la función para volver
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver" // Descripción para accesibilidad
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
                // Puedes añadir un botón de navegación "Atrás" aquí si quieres
                // navigationIcon = { IconButton(onClick = { /* Volver atrás */ }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }
            )
        }
    ) { paddingValues -> // Padding del Scaffold

        // Columna que permite scroll si el contenido es muy largo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplica padding del Scaffold
                .padding(16.dp) // Padding interno de la columna
                .verticalScroll(rememberScrollState()), // Habilita el scroll vertical
            horizontalAlignment = Alignment.CenterHorizontally, // Centra elementos horizontalmente
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio uniforme entre elementos
        ) {

            // --- Campos de Texto ---

            OutlinedTextField(
                value = "", // Vacío por ahora
                onValueChange = {}, // No hace nada por ahora
                label = { Text("Nombre(s)") },
                modifier = Modifier.fillMaxWidth(), // Ocupa todo el ancho
                singleLine = true // Evita saltos de línea
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
                // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email) // Para teclado de email
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
                // visualTransformation = PasswordVisualTransformation() // Para ocultar contraseña
                // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password) // Para teclado de contraseña
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
                // visualTransformation = PasswordVisualTransformation()
                // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // --- Botón de Registro ---
            Button(
                onClick = { /* Acción de registro (ninguna por ahora) */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp) // Un poco más de espacio arriba del botón
            ) {
                Text("Registrarse")
            }

            // --- Enlace a Login (Opcional) ---
            TextButton(onClick = { /* Ir a la pantalla de Login (ninguna acción por ahora) */ }) {
                Text("¿Ya tienes cuenta? Inicia Sesión")
            }
        }
    }
}

// --- Previsualización ---
@Preview(showBackground = true, widthDp = 360, heightDp = 700) // Ajusta altura si es necesario
@Composable
fun RegistrationScreenPreview() {
    MaterialTheme { // Aplica el tema para la preview
        RegistrationScreen(onNavigateBack = {})
    }
}