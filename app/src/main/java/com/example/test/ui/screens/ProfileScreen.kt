// En ProfileScreen.kt
package com.example.test.ui.screens

import android.util.Log
import android.widget.Toast // Para mensajes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person // Icono para el campo de nombre
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.test.data.local.AppDatabase
import com.example.test.data.repository.UserRepository
import com.example.test.ui.auth.AuthViewModel
import com.example.test.ui.auth.AuthViewModelFactory
import kotlinx.coroutines.flow.collectLatest

// Importa tus colores base y específicos si no vienen del tema
// import com.example.test.ui.theme.DarkBackground
// import com.example.test.ui.theme.TextColorLight
// import com.example.test.ui.theme.IconColorLight
// import com.example.test.ui.theme.BrightAccentColor
// import com.example.test.ui.theme.FocusedInputColor
// import com.example.test.ui.theme.UnfocusedInputColor
// import com.example.test.ui.theme.ErrorColor
// import com.example.test.ui.theme.DarkerTextColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onAccountDeletedAndNavigateToInitial: () -> Unit

) {
    val context = LocalContext.current
    val currentUser by authViewModel.currentUser.collectAsState()
    Log.d("ProfileScreen", "Instancia de AuthViewModel en Profile: $authViewModel") // <-- ESTE LOG

    // Estado para el nombre editable
    var editableName by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }

    // Estados de la operación de actualización del ViewModel
    val isUpdating by authViewModel.isUpdatingProfile.collectAsState()
    val updateError by authViewModel.profileUpdateError.collectAsState()

    val isDeleting by authViewModel.isDeletingAccount.collectAsState()
    val deletionError by authViewModel.accountDeletionError.collectAsState()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Cuando currentUser (del ViewModel) cambie o se cargue por primera vez,
    // actualiza el editableName.
    LaunchedEffect(currentUser) {
        currentUser?.name?.let { currentName ->
            editableName = currentName
        }
    }

    // Observar eventos de éxito de actualización
    LaunchedEffect(Unit) {
        authViewModel.profileUpdateSuccessEvent.collectLatest { successMessage ->
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
        }
    }
    // Observar errores de actualización del ViewModel
    LaunchedEffect(updateError) {
        updateError?.let {
            // Si quieres mostrar el error del ViewModel como Toast también
            // Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            // O puedes mostrarlo como un Text en la UI
        }
    }
    LaunchedEffect(Unit) {
        authViewModel.accountDeletionSuccessEvent.collectLatest {
            Toast.makeText(context, "Cuenta eliminada exitosamente.", Toast.LENGTH_LONG).show()
            onAccountDeletedAndNavigateToInitial() // Llama a la nueva lambda para navegar
        }
    }


    fun handleSaveChanges() {
        nameError = null // Limpiar error de campo local
        if (editableName.isBlank()) {
            nameError = "El nombre no puede estar vacío."
            return
        }
        // Solo actualizar si el nombre ha cambiado realmente (opcional)
        if (editableName != currentUser?.name) {
            authViewModel.updateUserName(editableName)
        } else {
            Toast.makeText(context, "No hay cambios para guardar.", Toast.LENGTH_SHORT).show()
        }

    }
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Confirmar Eliminación", color = TextColorLight) },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.",
                    color = TextColorLight.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.deleteCurrentUserAccount()
                        showDeleteConfirmDialog = false
                    }
                ) { Text("Eliminar", color = ErrorColor) } // Botón de confirmación en rojo
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancelar", color = BrightAccentColor)
                }
            },
            containerColor = SlightlyLighterBackground // Fondo del diálogo
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = TextColorLight) },
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
                .padding(all = 24.dp) // Padding general más grande
                .verticalScroll(rememberScrollState()), // Permite scroll
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentUser == null && !isUpdating) { // Si no hay usuario Y no se está actualizando
                Text("Cargando perfil...", color = TextColorLight)
            }

            currentUser?.let { user ->
                Text(
                    "Información del Usuario",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextColorLight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Campo Nombre Editable
                OutlinedTextField(
                    value = editableName,
                    onValueChange = { editableName = it; nameError = null },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.Person, null, tint = UnfocusedInputColor) },
                    colors = customRegistrationTextFieldColors(isError = nameError != null), // Reusa los colores de Registration
                    isError = nameError != null,
                    supportingText = {
                        if (nameError != null) Text(nameError!!, color = ErrorColor)
                    }
                )

                // Email (Solo lectura por ahora)
                OutlinedTextField(
                    value = user.email,
                    onValueChange = {}, // No editable
                    readOnly = true,
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            Icons.Filled.AccountCircle,
                            null,
                            tint = UnfocusedInputColor
                        )
                    },
                    colors = customRegistrationTextFieldColors(isError = false).copy( // Hacemos que parezca deshabilitado
                        disabledTextColor = TextColorLight.copy(alpha = 0.7f),
                        disabledLabelColor = UnfocusedInputColor.copy(alpha = 0.5f),
                        disabledLeadingIconColor = UnfocusedInputColor.copy(alpha = 0.5f)
                    ),
                    enabled = false // Lo marcamos como no enabled
                )

                // Rol (Solo lectura)
                OutlinedTextField(
                    value = user.role,
                    onValueChange = {}, // No editable
                    readOnly = true,
                    label = { Text("Rol") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Check, null, tint = UnfocusedInputColor) },
                    colors = customRegistrationTextFieldColors(isError = false).copy(
                        disabledTextColor = TextColorLight.copy(alpha = 0.7f),
                        disabledLabelColor = UnfocusedInputColor.copy(alpha = 0.5f),
                        disabledLeadingIconColor = UnfocusedInputColor.copy(alpha = 0.5f)
                    ),
                    enabled = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Indicador de carga y error del ViewModel
                if (isUpdating) {
                    CircularProgressIndicator(color = BrightAccentColor)
                }
                updateError?.let { errorMsg ->
                    Text(
                        text = errorMsg,
                        color = ErrorColor,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Button(
                    onClick = { handleSaveChanges() },
                    enabled = !isUpdating && (editableName != user.name && editableName.isNotBlank()), // Habilitado si no carga Y hay cambios Y no está vacío
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = BrightAccentColor)
                ) {
                    Text(
                        "Guardar Cambios",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkerTextColor
                    )
                }

                Spacer(modifier = Modifier.height(24.dp)) // Espacio entre botones

                // --- BOTÓN ELIMINAR CUENTA ---
                OutlinedButton(
                    onClick = { showDeleteConfirmDialog = true }, // Abre el diálogo
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ErrorColor // Color del texto/icono
                    ),
                    border = BorderStroke(
                        1.dp,
                        ErrorColor.copy(alpha = 0.7f)
                    ), // Borde con color de error
                    enabled = !isDeleting && !isUpdating // Deshabilitar si otra operación está en curso
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = ErrorColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Eliminar Cuenta", fontWeight = FontWeight.Bold)
                    }
                }
                // --- FIN BOTÓN ELIMINAR CUENTA ---

                // Mostrar error de eliminación del ViewModel si existe
                deletionError?.let { errorMsg ->
                    Text(
                        text = errorMsg,
                        color = ErrorColor,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } // Fin currentUser?.let
        } // Fin Column
    } // Fin Scaffold
}

// Necesitas la función customRegistrationTextFieldColors() aquí también,
// o moverla a un archivo común e importarla.
// Por ahora, la copio de RegistrationScreen.kt (asegúrate que los colores que usa estén definidos/importados aquí)
@Composable
private fun customRegistrationTextFieldColors(isError: Boolean = false): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextColorLight,
        unfocusedTextColor = TextColorLight.copy(alpha = 0.8f),
        cursorColor = BrightAccentColor,
        focusedBorderColor = if (isError) ErrorColor else FocusedInputColor,
        unfocusedBorderColor = if (isError) ErrorColor.copy(alpha = 0.7f) else UnfocusedInputColor,
        errorBorderColor = ErrorColor,
        focusedLabelColor = if (isError) ErrorColor else FocusedInputColor,
        unfocusedLabelColor = if (isError) ErrorColor.copy(alpha = 0.7f) else UnfocusedInputColor,
        errorLabelColor = ErrorColor,
        focusedLeadingIconColor = if (isError) ErrorColor else FocusedInputColor,
        unfocusedLeadingIconColor = if (isError) ErrorColor.copy(alpha = 0.7f) else UnfocusedInputColor,
        errorLeadingIconColor = ErrorColor,
        focusedTrailingIconColor = if (isError) ErrorColor else FocusedInputColor,
        unfocusedTrailingIconColor = if (isError) ErrorColor.copy(alpha = 0.7f) else UnfocusedInputColor,
        errorTrailingIconColor = ErrorColor,
        disabledBorderColor = UnfocusedInputColor.copy(alpha = 0.5f),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1A283A)
@Composable
fun ProfileScreenPreview() {
    val context = LocalContext.current
    val previewAuthViewModel =
        AuthViewModel(UserRepository(AppDatabase.getDatabase(context).userDao()))
    MaterialTheme { // O tu tema específico
        ProfileScreen(
            onNavigateBack = {},
            authViewModel = previewAuthViewModel,
            onAccountDeletedAndNavigateToInitial = {}
        )
    }
}