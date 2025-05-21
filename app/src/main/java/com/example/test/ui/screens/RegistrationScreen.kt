// Dentro de /app/java/com.example.test/ui/screens/RegistrationScreen.kt
package com.example.test.ui.screens

// --- Importaciones Necesarias ---
import android.widget.Toast // Para mensajes de éxito/error
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Para obtener el ViewModel
import com.example.test.data.local.AppDatabase
import com.example.test.data.repository.UserRepository
import com.example.test.ui.auth.AuthViewModel // Tu ViewModel
import com.example.test.ui.auth.AuthViewModelFactory // Tu Factory
import kotlinx.coroutines.flow.collectLatest // Para observar SharedFlow
// Importa tus colores si están definidos centralmente
// import com.example.test.ui.theme.DarkBackground
// import com.example.test.ui.theme.TextColorLight
// import com.example.test.ui.theme.BrightAccentColor
// import com.example.test.ui.theme.IconColorLight
// import com.example.test.ui.theme.DarkerTextColor
// import com.example.test.ui.theme.FocusedInputColor
// import com.example.test.ui.theme.UnfocusedInputColor
// import com.example.test.ui.theme.ErrorColor


// --- COLORES (Define aquí los que no sean globales/importados) ---
// Asegúrate que DarkBackground y TextColorLight estén disponibles globalmente o impórtalos/defínelos
// val DarkBackground = Color(0xFF1A283A)
// val TextColorLight = Color.White

val FocusedInputColor = BrightAccentColor // Usando el que ya tenías
val UnfocusedInputColor = TextColorLight.copy(alpha = 0.7f) // Usando el que ya tenías
val ErrorColor = Color(0xFFB00020) // Un color de error estándar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit ,
    authViewModel: AuthViewModel,
) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    // Estados para los campos del formulario
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Estados para errores de validación de campos
    var nameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var roleError by remember { mutableStateOf<String?>(null) }


    // Estados para selección de Rol
    val roles = listOf("Cliente", "Administrador") // Define los roles disponibles
    var selectedRole by remember { mutableStateOf(roles[0]) } // Rol por defecto
    var expandedRoleDropdown by remember { mutableStateOf(false) }

    // Observar estados del ViewModel
    val isLoading by authViewModel.isLoadingRegistration.collectAsState()
    val generalRegistrationError by authViewModel.registrationError.collectAsState()

    // Observar evento de éxito de registro
    LaunchedEffect(Unit) {
        authViewModel.registrationSuccessEvent.collectLatest {
            Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
            onNavigateToLogin() // Navega a la pantalla de login después del registro exitoso
        }
    }

    // Función para manejar el intento de registro
    fun handleRegistrationAttempt() {
        // Limpiar errores previos
        nameError = null; lastNameError = null; emailError = null; passwordError = null; confirmPasswordError = null; roleError = null;
        // authViewModel.clearRegistrationError() // Necesitarías una función para esto en el ViewModel si el error es persistente

        var isValid = true
        if (name.isBlank()) { nameError = "El nombre es obligatorio"; isValid = false }
        if (lastName.isBlank()) { lastNameError = "El apellido es obligatorio"; isValid = false }
        if (email.isBlank()) {
            emailError = "El correo es obligatorio"; isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Formato de correo inválido"; isValid = false
        }
        if (password.isBlank()) {
            passwordError = "La contraseña es obligatoria"; isValid = false
        } else if (password.length < 6) {
            passwordError = "Mínimo 6 caracteres"; isValid = false
        }
        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Confirma la contraseña"; isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordError = "Las contraseñas no coinciden"; isValid = false
        }
        if (selectedRole.isBlank()) { roleError = "Selecciona un rol"; isValid = false} // Aunque siempre hay uno por defecto

        if (isValid) {
            authViewModel.registerUser(name = "$name $lastName", email = email, passwordRaw = password, role = selectedRole)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Crear Nueva Cuenta", color = TextColorLight) }, // Asegura color
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = IconColorLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextColorLight,
                    navigationIconContentColor = IconColorLight
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp) // Un poco menos de espacio
        ) {
            // Nombre
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                label = { Text("Nombre(s)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Person, null, tint = UnfocusedInputColor) },
                colors = customRegistrationTextFieldColors(isError = nameError != null),
                isError = nameError != null,
                supportingText = { if (nameError != null) Text(nameError!!, color = ErrorColor) }
            )
            // Apellidos
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it; lastNameError = null },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Face, null, tint = UnfocusedInputColor) },
                colors = customRegistrationTextFieldColors(isError = lastNameError != null),
                isError = lastNameError != null,
                supportingText = { if (lastNameError != null) Text(lastNameError!!, color = ErrorColor) }
            )
            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { Icon(Icons.Filled.Email, null, tint = UnfocusedInputColor) },
                colors = customRegistrationTextFieldColors(isError = emailError != null),
                isError = emailError != null,
                supportingText = { if (emailError != null) Text(emailError!!, color = ErrorColor) }
            )
            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Filled.Lock, null, tint = UnfocusedInputColor) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.PlayArrow else Icons.Filled.Close
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, if (passwordVisible) "Ocultar" else "Mostrar", tint = UnfocusedInputColor)
                    }
                },
                colors = customRegistrationTextFieldColors(isError = passwordError != null),
                isError = passwordError != null,
                supportingText = { if (passwordError != null) Text(passwordError!!, color = ErrorColor) }
            )
            // Confirmar Contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmPasswordError = null },
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Filled.PlayArrow, null, tint = UnfocusedInputColor) },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Person else Icons.Filled.Close
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(image, if (confirmPasswordVisible) "Ocultar" else "Mostrar", tint = UnfocusedInputColor)
                    }
                },
                colors = customRegistrationTextFieldColors(isError = confirmPasswordError != null),
                isError = confirmPasswordError != null,
                supportingText = { if (confirmPasswordError != null) Text(confirmPasswordError!!, color = ErrorColor) }
            )

            // --- Selección de Rol ---
            Spacer(modifier = Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = expandedRoleDropdown,
                onExpandedChange = { expandedRoleDropdown = !expandedRoleDropdown },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedRole,
                    onValueChange = {}, // No se cambia directamente
                    readOnly = true,
                    label = { Text("Rol de Usuario") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRoleDropdown) },
                    colors = customRegistrationTextFieldColors(isError = roleError != null),
                    isError = roleError != null,
                    modifier = Modifier.menuAnchor().fillMaxWidth() // Importante para el ancla
                )
                ExposedDropdownMenu(
                    expanded = expandedRoleDropdown,
                    onDismissRequest = { expandedRoleDropdown = false },
                    modifier = Modifier.background(SlightlyLighterBackground) // Fondo para el menú desplegable
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role, color = TextColorLight) },
                            onClick = {
                                selectedRole = role
                                expandedRoleDropdown = false
                                roleError = null
                            }
                        )
                    }
                }
            }
            if (roleError != null) {
                Text(roleError!!, color = ErrorColor, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().padding(start = 16.dp))
            }
            // --- Fin Selección de Rol ---


            // --- Indicador de Carga y Error General del ViewModel ---
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 16.dp),
                    color = BrightAccentColor
                )
            }
            generalRegistrationError?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = ErrorColor, // Usar color de error
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            // --- Fin Indicador y Error ---

            // Botón de Registro
            Button(
                onClick = { if (!isLoading) handleRegistrationAttempt() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = BrightAccentColor),
                enabled = !isLoading // Deshabilita el botón mientras carga
            ) {
                Text("Crear Cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkerTextColor)
            }

            // Enlace a Login
            TextButton(onClick = { if(!isLoading) onNavigateToLogin() }) {
                Text("¿Ya tienes cuenta? Inicia Sesión", color = BrightAccentColor)
            }
            Spacer(modifier = Modifier.height(8.dp)) // Espacio al final
        }
    }
}

// Función auxiliar para colores de TextField, adaptada para RegistrationScreen
@Composable
private fun customRegistrationTextFieldColors(isError: Boolean = false): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextColorLight,
        unfocusedTextColor = TextColorLight.copy(alpha = 0.8f),
        cursorColor = BrightAccentColor,
        focusedBorderColor = if (isError) ErrorColor else FocusedInputColor,
        unfocusedBorderColor = if (isError) ErrorColor.copy(alpha = 0.7f) else UnfocusedInputColor,
        errorBorderColor = ErrorColor, // Color del borde cuando isError es true
        focusedLabelColor = if (isError) ErrorColor else FocusedInputColor,
        unfocusedLabelColor = if (isError) ErrorColor.copy(alpha = 0.7f) else UnfocusedInputColor,
        errorLabelColor = ErrorColor, // Color del label cuando isError es true
        focusedLeadingIconColor = if (isError) ErrorColor else FocusedInputColor,
        unfocusedLeadingIconColor = if (isError) ErrorColor.copy(alpha = 0.7f) else UnfocusedInputColor,
        errorLeadingIconColor = ErrorColor, // Color del icono cuando isError es true
        focusedTrailingIconColor = if (isError) ErrorColor else FocusedInputColor, // Para el ojo de la contraseña
        unfocusedTrailingIconColor = if (isError) ErrorColor.copy(alpha = 0.7f) else UnfocusedInputColor, // Para el ojo
        errorTrailingIconColor = ErrorColor
    )
}

// --- Previsualización ---
@Preview(showBackground = true, backgroundColor = 0xFF1A283A)
@Composable
fun RegistrationScreenPreview() {
    val context = LocalContext.current
    val previewAuthViewModel =
        AuthViewModel(UserRepository(AppDatabase.getDatabase(context).userDao()))
    MaterialTheme {
        RegistrationScreen(
            onNavigateBack = {},
            onNavigateToLogin = {},
            authViewModel = previewAuthViewModel,
        )
    }
}