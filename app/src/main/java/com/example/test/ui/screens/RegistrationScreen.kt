// Dentro de /app/java/com.example.test/ui/screens/RegistrationScreen.kt
package com.example.test.ui.screens

// --- Importaciones Necesarias ---
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape // Para botón redondeado
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.* // Importar iconos para TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight // Para botón
// Importaciones para ocultar contraseña y tipos de teclado (opcional por ahora)
// import androidx.compose.ui.text.input.PasswordVisualTransformation
// import androidx.compose.ui.text.input.KeyboardType
// import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Para botón

// Importa tus colores si están definidos centralmente
// import com.example.test.ui.theme.DarkBackground
// import com.example.test.ui.theme.TextColorLight

// --- COLORES (Define aquí los que no sean globales/importados) ---
val FocusedInputColor = BrightAccentColor // Color para elementos enfocados (usa el acento)

val UnfocusedInputColor = TextColorLight.copy(alpha = 0.7f)
// Asume que DarkBackground y TextColorLight vienen de otro sitio o del tema

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = DarkBackground, // Fondo oscuro para toda la pantalla
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = IconColorLight // Asegura color claro
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground, // Fondo oscuro
                    titleContentColor = TextColorLight, // Texto claro
                    navigationIconContentColor = IconColorLight // Icono claro
                )
            )
        }
    ) { paddingValues -> // Padding del Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp) // Ajusta padding si es necesario
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp) // Reduce un poco el espacio si prefieres
        ) {

            // --- Campos de Texto Estilizados ---

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Nombre(s)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { // Icono Opcional
                    Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = UnfocusedInputColor)
                },
                colors = customTextFieldColors() // Aplicar colores personalizados
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { // Icono Opcional
                    Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = UnfocusedInputColor)
                },
                colors = customTextFieldColors()
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { // Icono Opcional
                    Icon(Icons.Filled.Email, contentDescription = null, tint = UnfocusedInputColor)
                },
                // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), // Descomentar si manejas estado
                colors = customTextFieldColors()
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { // Icono Opcional
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = UnfocusedInputColor)
                },
                // visualTransformation = PasswordVisualTransformation(), // Descomentar si manejas estado
                // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), // Descomentar si manejas estado
                colors = customTextFieldColors()
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { // Icono Opcional
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = UnfocusedInputColor)
                },
                // visualTransformation = PasswordVisualTransformation(),
                // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = customTextFieldColors()
            )

            // --- Botón de Registro Estilizado ---
            Button(
                onClick = { /* Acción de registro */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp) // Más espacio arriba
                    .height(50.dp),
                shape = RoundedCornerShape(50), // Píldora
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrightAccentColor // Fondo de acento
                )
            ) {
                Text(
                    "Registrarse",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkerTextColor // Texto oscuro
                )
            }

            // --- Enlace a Login Estilizado (Opcional) ---
            TextButton(onClick = { /* Acción ir a Login */ }) {
                Text(
                    "¿Ya tienes cuenta? Inicia Sesión",
                    color = BrightAccentColor // Color de acento para el enlace
                )
            }
        } // Fin Column
    } // Fin Scaffold
}

// --- Función auxiliar para colores de TextField (para no repetir) ---
@Composable
private fun customTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        // Colores del texto y cursor
        focusedTextColor = TextColorLight,
        unfocusedTextColor = TextColorLight.copy(alpha = 0.8f),
        cursorColor = BrightAccentColor,
        // Colores de borde
        focusedBorderColor = FocusedInputColor,
        unfocusedBorderColor = UnfocusedInputColor,
        // Colores de label (etiqueta)
        focusedLabelColor = FocusedInputColor,
        unfocusedLabelColor = UnfocusedInputColor,
        // Colores del icono principal (leading)
        focusedLeadingIconColor = FocusedInputColor,
        unfocusedLeadingIconColor = UnfocusedInputColor
        // Puedes añadir más personalizaciones si quieres (fondo, icono trailing, etc.)
    )
}


// --- Previsualización ---
@Preview(showBackground = true, backgroundColor = 0xFF1A283A) // Fondo oscuro
@Composable
fun RegistrationScreenPreview() {
    MaterialTheme { // Usa MaterialTheme o tu tema específico
        RegistrationScreen(onNavigateBack = {}) // Pasa lambda vacía
    }
}