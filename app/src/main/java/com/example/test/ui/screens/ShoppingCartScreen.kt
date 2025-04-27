// Dentro de /app/java/com.example.test/ui/screens/ShoppingCartScreen.kt
// (Asegúrate que 'com.example.test' sea tu paquete real)

package com.example.test.ui.screens

// --- Importaciones Necesarias ---
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Para usar 'items' en LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle // Icono ejemplo
import androidx.compose.material.icons.filled.Delete // Icono para eliminar
import androidx.compose.material3.* // O androidx.compose.material.* si usas Material 2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- La Pantalla Principal ---
@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar en Material 3
@Composable
fun ShoppingCartScreen() {
    Scaffold(
        topBar = {
            // Barra superior con el título
            TopAppBar(
                title = { Text("Carrito de Compras") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues -> // Padding del Scaffold

        // Columna principal que contiene la lista, el resumen y el botón
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplica padding del Scaffold
                .padding(horizontal = 16.dp, vertical = 8.dp) // Padding general
        ) {
            // --- Lista de Items en el Carrito ---
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Ocupa el espacio disponible, dejando sitio para el resumen/botón
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre items
            ) {
                // Ejemplo: Mostramos 3 items fijos en el carrito
                items(count = 3) { index ->
                    CartItem(
                        productName = "Producto ${index + 1}",
                        productPrice = "${(15..50).random()}.99 €",
                        quantity = (1..3).random() // Cantidad aleatoria
                    )
                    if (index < 2) { // Añade un divisor entre items, excepto después del último
                        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                    }
                }
            } // Fin LazyColumn

            // --- Separador antes del Resumen ---
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // --- Resumen del Carrito ---
            CartSummary(
                subtotal = "125.97 €", // Valor de ejemplo
                shipping = "5.00 €",  // Valor de ejemplo
                total = "130.97 €"     // Valor de ejemplo
            )

            // --- Botón de Checkout ---
            Button(
                onClick = { /* Acción de proceder al pago (ninguna por ahora) */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp) // Espacio arriba del botón
            ) {
                Text("Proceder al Pago", fontSize = 16.sp)
            }
        } // Fin Columna Principal
    } // Fin Scaffold
}

// --- Composable para un Item del Carrito ---
@Composable
fun CartItem(
    productName: String,
    productPrice: String,
    quantity: Int,
    imageVector: ImageVector = Icons.Default.AddCircle // Icono placeholder
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Placeholder Imagen
        Image(
            imageVector = imageVector,
            contentDescription = "Imagen de $productName",
            modifier = Modifier
                .size(60.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(4.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Columna para Nombre y Precio
        Column(modifier = Modifier.weight(1f)) {
            Text(productName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(productPrice, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Columna para Cantidad y Eliminar
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Cant: $quantity", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            // Botón Eliminar (usamos IconButton por ser más compacto)
            IconButton(
                onClick = { /* Acción eliminar item (ninguna por ahora) */ },
                modifier = Modifier.size(24.dp) // Tamaño pequeño para el botón icono
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar $productName",
                    tint = MaterialTheme.colorScheme.error // Color rojo para eliminar
                )
            }
        }
    }
}

// --- Composable para el Resumen del Carrito ---
@Composable
fun CartSummary(subtotal: String, shipping: String, total: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Fila para Subtotal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // Separa los textos
        ) {
            Text("Subtotal:", style = MaterialTheme.typography.bodyMedium)
            Text(subtotal, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Fila para Envío
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Envío:", style = MaterialTheme.typography.bodyMedium)
            Text(shipping, style = MaterialTheme.typography.bodyMedium)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // Separador antes del total

        // Fila para Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total:", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(total, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}


// --- Previsualización ---
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ShoppingCartScreenPreview() {
    MaterialTheme { // Aplica tema
        ShoppingCartScreen()
    }
}