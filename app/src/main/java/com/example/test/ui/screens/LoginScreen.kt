// Dentro de /app/java/com.example.test/ui/screens/LoginScreen.kt
package com.example.test.ui.screens

// --- Importaciones Necesarias ---
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.R


val TextFieldBackgroundColor = Color(0xFF2A3C51)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit, // Para el botón de volver
    onLoginSuccess: () -> Unit, // Acción al hacer login (aún no implementada)
    onForgotPassword: () -> Unit, // Acción al olvidar contraseña (aún no implementada)
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground
    ) { paddingValues -> // No se usa
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()), // Hace scroll si el contenido es largo
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = IconColorLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(SlightlyLighterBackground, CircleShape), // Placeholder
                contentAlignment = Alignment.Center
            ) {


                Image(
                    painter = painterResource(id = R.drawable.logo_bg_removed),
                    contentDescription = "Logo GGDROP",
                    modifier = Modifier.size(80.dp)
                )

            }


            Spacer(modifier = Modifier.height(24.dp))

            // --- Título ---
            Text(
                text = "Inicio de sesión",
                color = TextColorLight,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))


            Text(
                text = "Digita tu correo",
                color = UnfocusedInputColor,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Correo", color = UnfocusedInputColor) },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Email,
                        contentDescription = null,
                        tint = UnfocusedInputColor
                    )
                },
                shape = RoundedCornerShape(50), // Píldora
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = customLoginTextFieldColors(),
                visualTransformation = VisualTransformation.None
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "Digita tu contraseña",
                color = UnfocusedInputColor,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Contraseña", color = UnfocusedInputColor) },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = null,
                        tint = UnfocusedInputColor
                    )
                },
                shape = RoundedCornerShape(50),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = customLoginTextFieldColors(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Lock else Icons.Filled.Check
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = UnfocusedInputColor
                        )
                    }
                }
            )


            TextButton(
                onClick = onForgotPassword,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp)
            ) {
                Text(
                    "¿Olvidaste tu contraseña?",
                    color = UnfocusedInputColor, // Color tenue
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            FloatingActionButton(
                onClick = onLoginSuccess, // Llama a la acción de login exitoso
                shape = CircleShape,
                containerColor = BrightAccentColor,
                contentColor = DarkerTextColor, // Color del icono
                modifier = Modifier.size(60.dp) // Tamaño del botón
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Iniciar Sesión",
                    modifier = Modifier.size(32.dp) // Tamaño del icono
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // Espacio al final

        } // Fin Column principal
    } // Fin Scaffold
}

// --- Función auxiliar para colores de TextField (para TextField normal) ---
@Composable
private fun customLoginTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        // Colores del texto y cursor
        focusedTextColor = TextColorLight,
        unfocusedTextColor = TextColorLight.copy(alpha = 0.8f),
        cursorColor = BrightAccentColor,
        // Colores de contenedor (fondo del campo)
        focusedContainerColor = TextFieldBackgroundColor,
        unfocusedContainerColor = TextFieldBackgroundColor,
        disabledContainerColor = TextFieldBackgroundColor.copy(alpha = 0.5f),
        // Colores de línea indicadora (abajo) - la hacemos transparente porque usamos shape
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        // Colores de label/placeholder (si usáramos label)
        // focusedLabelColor = FocusedInputColor,
        // unfocusedLabelColor = UnfocusedInputColor,
        // Colores del icono principal (leading)
        focusedLeadingIconColor = FocusedInputColor,
        unfocusedLeadingIconColor = UnfocusedInputColor,
        // Colores del icono final (trailing)
        focusedTrailingIconColor = FocusedInputColor,
        unfocusedTrailingIconColor = UnfocusedInputColor
    )
}

// --- Previsualización ---
@Preview(showBackground = true, backgroundColor = 0xFF1A283A) // Fondo oscuro
@Composable
fun LoginScreenPreview() { // Cambiado el nombre para evitar conflicto si tenías otro
    MaterialTheme {
        LoginScreen(
            onNavigateBack = {},
            onLoginSuccess = {},
            onForgotPassword = {}
        )
    }
}