// Dentro de /app/java/tu.paquete.nombre/ui/screens/LoginScreen.kt
package com.example.test.ui.screens // Asegúrate que este sea el nombre correcto de tu paquete

// --- Importaciones Necesarias ---
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.R

// Asegúrate de tener tu logo en la carpeta res/drawable
// import tu.paquete.nombre.R // Necesario para R.drawable...


// --- COLORES (Platzhalter - ¡Reemplazar con los colores reales!) ---
val DarkBackground = Color(0xFF1A283A) // Azul muy oscuro (Adivinado)
val BrightButtonColor = Color(0xFF00BFA5) // Verde azulado brillante (Adivinado)
val DullButtonColor = Color(0xFF4A6572)   // Azul grisáceo (Adivinado)
val TextColorLight = Color.White         // Texto blanco para botones y etiquetas

@Composable
fun LoginScreen(onNavigateToRegistration: () -> Unit) {
    // Columna principal que ocupa toda la pantalla
    Column(modifier = Modifier.fillMaxSize()) {

        // --- Sección Superior (Imagen Banner) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Ocupa una porción del espacio vertical
                .background(Color.LightGray), // Fondo gris claro como placeholder
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_image_png), // Reemplaza con tu ID de recurso
                contentDescription = "Banner GGDROP Be fan. Be pro.",
                modifier = Modifier.fillMaxSize(), // La imagen llena el Box
                contentScale = ContentScale.Crop // Escala la imagen para llenar el espacio, recortando si es necesario
            )
        }

        // --- Sección Inferior (Botones de Acción) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f) // Ocupa una porción MÁS GRANDE del espacio vertical
                .background(DarkBackground) // Fondo oscuro (¡Usa tu color!)
                .padding(horizontal = 32.dp, vertical = 24.dp), // Padding interno
            horizontalAlignment = Alignment.CenterHorizontally, // Centra contenido horizontalmente
            verticalArrangement = Arrangement.Center // Centra contenido verticalmente
        ) {

            // --- Bloque Botón Iniciar Sesión ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ya soy fan/pro",
                    color = TextColorLight,
                    fontSize = 12.sp // Tamaño pequeño para la etiqueta
                )
                Spacer(modifier = Modifier.height(4.dp)) // Pequeño espacio
                Button(
                    onClick = { /* TODO: Navegar a pantalla de login real o mostrar campos */ },
                    modifier = Modifier
                        .fillMaxWidth(0.85f) // Botón ocupa el 85% del ancho disponible
                        .height(50.dp), // Altura estándar del botón
                    shape = RoundedCornerShape(50), // Bordes totalmente redondeados (píldora)
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightButtonColor // Color brillante (¡Usa tu color!)
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

            Spacer(modifier = Modifier.height(24.dp)) // Espacio mayor entre los dos botones

            // --- Bloque Botón Registrarme ---
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
                        containerColor = DullButtonColor // Color más opaco (¡Usa tu color!)
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
        } // Fin Sección Inferior
    } // Fin Columna Principal
}

// --- Previsualización ---
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LoginScreenPreview_NewDesign() {
    // Puedes envolver en tu tema si tienes uno
    // TuAppTheme {
    LoginScreen(onNavigateToRegistration = {})
    // }
}