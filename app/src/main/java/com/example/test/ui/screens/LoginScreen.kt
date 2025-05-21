package com.example.test.ui.screens

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.test.data.local.AppDatabase
import com.example.test.data.repository.UserRepository
import com.example.test.ui.auth.AuthViewModel
import com.example.test.ui.auth.AuthViewModelFactory
import kotlinx.coroutines.flow.collectLatest


val TextFieldBackgroundColor = Color(0xFF2A3C51)


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit, // Para el botón de volver
    onLoginSuccess: (userId: Int, userRole: String) -> Unit,
    onForgotPassword: () -> Unit,
) {
    Log.d("LoginScreen", "Instancia de AuthViewModel en Login: $authViewModel")

    val isLoading by authViewModel.isLoading.collectAsState()
    val generalLoginError by authViewModel.loginError.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        authViewModel.loginSuccessEvent.collectLatest { (userId, userRole) ->
            Log.d("LoginScreen", "Login local exitoso para usuario ID: $userId, Rol: $userRole")
            onLoginSuccess(userId, userRole) // Asegúrate que onLoginSuccess acepte estos
        }
    }

    fun handleLoginAttempt() {
        emailError = null
        passwordError = null

        var isValid = true
        if (email.isBlank()) {
            emailError = "El correo no puede estar vacío"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Formato de correo inválido"
            isValid = false
        }

        if (password.isBlank()) {
            passwordError = "La contraseña no puede estar vacía"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "La contraseña debe tener al menos 6 caracteres"
            isValid = false
        }

        if (isValid) {
            authViewModel.loginUser(email, password)
        }
    }

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
                onValueChange = { email = it; emailError = null },
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) {
                        Text(emailError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
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
                onValueChange = { password = it; passwordError = null },
                isError = passwordError != null,
                supportingText = {
                    if (passwordError != null) {
                        Text(passwordError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
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

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = BrightAccentColor
                )
            }

            generalLoginError?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            FloatingActionButton(
                onClick = {
                    if (!isLoading) { // Evita múltiples clics mientras carga
                        handleLoginAttempt()
                    }
                }, // Llama a la acción de login exitoso
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

@Preview(showBackground = true, backgroundColor = 0xFF1A283A)
@Composable
fun LoginScreenPreview() {
    val context = LocalContext.current
    val previewAuthViewModel = AuthViewModel(UserRepository(AppDatabase.getDatabase(context).userDao()))
    MaterialTheme {
        LoginScreen(
            onNavigateBack = {},
            onLoginSuccess = { userId, userRole ->
                // En la preview, no hacemos nada con userId o userRole
                Log.d("LoginPreview", "Login success con ID: $userId, Rol: $userRole")
            },
            onForgotPassword = {},
            authViewModel = previewAuthViewModel,
        )
    }
}