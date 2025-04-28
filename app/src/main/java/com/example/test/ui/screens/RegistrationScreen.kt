package com.example.test.ui.screens

// --- Importaciones Necesarias ---
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


val FocusedInputColor = BrightAccentColor

val UnfocusedInputColor = TextColorLight.copy(alpha = 0.7f)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = DarkBackground,
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
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Nombre(s)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { // Icono Opcional
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = null,
                        tint = UnfocusedInputColor
                    )
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
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = null,
                        tint = UnfocusedInputColor
                    )
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
                colors = customTextFieldColors()
            )

            Button(
                onClick = { /* Acción de registro */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrightAccentColor
                )
            ) {
                Text(
                    "Registrarse",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkerTextColor
                )
            }
            TextButton(onClick = { /* Acción ir a Login */ }) {
                Text(
                    "¿Ya tienes cuenta? Inicia Sesión",
                    color = BrightAccentColor
                )
            }
        }
    }
}

@Composable
private fun customTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(

        focusedTextColor = TextColorLight,
        unfocusedTextColor = TextColorLight.copy(alpha = 0.8f),
        cursorColor = BrightAccentColor,
        focusedBorderColor = FocusedInputColor,
        unfocusedBorderColor = UnfocusedInputColor,
        focusedLabelColor = FocusedInputColor,
        unfocusedLabelColor = UnfocusedInputColor,
        focusedLeadingIconColor = FocusedInputColor,
        unfocusedLeadingIconColor = UnfocusedInputColor
    )
}


// --- Previsualización ---
@Preview(showBackground = true, backgroundColor = 0xFF1A283A) // Fondo oscuro
@Composable
fun RegistrationScreenPreview() {
    MaterialTheme {
        RegistrationScreen(onNavigateBack = {}) // Pasa lambda vacía
    }
}