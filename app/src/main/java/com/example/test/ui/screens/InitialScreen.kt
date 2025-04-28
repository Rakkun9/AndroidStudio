
package com.example.test.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape // Para bordes redondeados
import androidx.compose.material3.* // O androidx.compose.material.* si usas Material 2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Para definir colores personalizados
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // Para cargar imágenes de tus recursos (drawable)
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.R




val DarkBackground = Color(0xFF1A283A)
val BrightButtonColor = Color(0xFF00BFA5)
val DullButtonColor = Color(0xFF4A6572)
val TextColorLight = Color.White

@Composable
fun InitialScreen(
    onNavigateToRegistration: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    // Columna principal que ocupa toda la pantalla
    Column(modifier = Modifier.fillMaxSize()) {

        // --- Sección Superior (Imagen Banner) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.LightGray), // Fondo gris claro como placeholder
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_image_png),
                contentDescription = "Banner GGDROP Be fan. Be pro.",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)
                .background(DarkBackground)
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // --- Bloque Botón Iniciar Sesión ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ya soy fan/pro",
                    color = TextColorLight,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightButtonColor
                    )
                ) {
                    Text(
                        text = "Iniciar sesión",
                        color = TextColorLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Deseo ser fan/pro",
                    color = TextColorLight,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onNavigateToRegistration,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DullButtonColor
                    )
                ) {
                    Text(
                        text = "Registrarme",
                        color = TextColorLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- Previsualización ---
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun InitialScreenPreview_NewDesign() {

    InitialScreen(onNavigateToRegistration = {}, onNavigateToLogin = {})
    // }
}