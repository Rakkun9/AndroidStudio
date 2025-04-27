// Dentro de /app/java/com.example.test/ui/screens/ShoppingCartScreen.kt
package com.example.test.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed // Usar itemsIndexed para el divisor
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Icono volver
import androidx.compose.material.icons.filled.* // Importa iconos necesarios
import androidx.compose.material3.*
import androidx.compose.runtime.* // Necesario para remember, mutableStateOf, etc. si añades estado
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Importa tus colores si están definidos centralmente
// import com.example.test.ui.theme.DarkBackground
// import com.example.test.ui.theme.TextColorLight

// --- COLORES (Define aquí los que no sean globales/importados) ---
val DarkerTextColor = Color(0xFF1A283A) // Texto oscuro para botones claros
// Asume que DarkBackground y TextColorLight vienen de otro sitio o del tema

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCartScreen(
    onNavigateBack: () -> Unit // Parámetro para volver atrás
) {
    // Datos de ejemplo (más adelante podrías usar un ViewModel)
    val sampleCartItems = remember {
        List(3) { index ->
            CartItemData(
                id = index,
                productName = "Producto Ejemplo ${index + 1}",
                productPrice = "${(15..50).random()}.99 €",
                quantity = (1..3).random(),
                imageVector = Icons.Default.ShoppingCart // Placeholder de ropa
            )
        }
    }

    Scaffold(
        containerColor = DarkBackground, // Fondo oscuro general
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
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
    ) { paddingValues ->

        // Si el carrito está vacío (añadir lógica después)
        if (sampleCartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Tu carrito está vacío",
                    color = TextColorLight.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        } else {
            // Si hay items, muestra la columna con lista, resumen y botón
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // --- Lista de Items ---
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Más espacio entre cards
                ) {
                    itemsIndexed(sampleCartItems) { index, item ->
                        CartItemCard(item = item) // Usamos el nuevo Card Item
                        // No necesitamos el divisor aquí si usamos Cards con espacio
                    }
                } // Fin LazyColumn

                // --- Separador ---
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = TextColorLight.copy(alpha = 0.2f) // Color tenue para el divisor
                )

                // --- Resumen del Carrito ---
                CartSummary(
                    subtotal = "125.97 €", // Calcular real después
                    shipping = "5.00 €",
                    total = "130.97 €"
                )

                // --- Botón de Checkout ---
                Button(
                    onClick = { /* Acción Checkout */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp) // Espacio arriba y abajo
                        .height(50.dp),
                    shape = RoundedCornerShape(50), // Botón píldora
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightAccentColor // Fondo color acento
                    )
                ) {
                    Text(
                        "Proceder al Pago",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkerTextColor // Texto oscuro sobre fondo claro
                    )
                }
            } // Fin Columna Principal (con items)
        } // Fin else (hay items)
    } // Fin Scaffold
}

// --- Estructura de datos para un item del carrito ---
data class CartItemData(
    val id: Int,
    val productName: String,
    val productPrice: String,
    var quantity: Int, // var para poder modificarla (si añades lógica)
    val imageVector: ImageVector // Placeholder, idealmente sería un URL o Res ID
)

// --- Composable MEJORADO para un Item del Carrito (dentro de un Card) ---
@Composable
fun CartItemCard(item: CartItemData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // Bordes más redondeados
        colors = CardDefaults.cardColors(
            containerColor = SlightlyLighterBackground // Fondo de tarjeta ligeramente más claro
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Padding interno de la Card
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder Imagen
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)) // Redondeado
                    .background(DarkBackground), // Fondo oscuro para el placeholder
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.imageVector, // Usa el icono del item data
                    contentDescription = null,
                    tint = IconColorLight.copy(alpha = 0.8f),
                    modifier = Modifier.size(32.dp)
                )
            }


            Spacer(modifier = Modifier.width(16.dp))

            // Columna para Nombre y Precio
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.productName,
                    style = MaterialTheme.typography.titleMedium, // Un poco más grande
                    fontWeight = FontWeight.Bold,
                    color = TextColorLight // Texto claro
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    item.productPrice,
                    style = MaterialTheme.typography.bodyLarge, // Un poco más grande
                    color = BrightAccentColor // Precio con color acento
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Columna para Cantidad (+/-) y Eliminar
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Controles de Cantidad
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* Decrementar cantidad */ }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Delete, contentDescription = "Quitar uno", tint = IconColorLight)
                    }
                    Text(
                        "${item.quantity}",
                        color = TextColorLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(onClick = { /* Incrementar cantidad */ }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Add, contentDescription = "Añadir uno", tint = IconColorLight)
                    }
                }
                // Botón Eliminar
                IconButton(
                    onClick = { /* Acción eliminar item */ },
                    modifier = Modifier.size(32.dp) // Un poco más grande
                ) {
                    Icon(
                        Icons.Default.Clear, // Usar icono Outline
                        contentDescription = "Eliminar ${item.productName}",
                        tint = TextColorLight.copy(alpha = 0.7f) // Color tenue
                    )
                }
            }
        } // Fin Row principal
    } // Fin Card
}

// --- Composable para el Resumen del Carrito (Ajustado para tema oscuro) ---
@Composable
fun CartSummary(subtotal: String, shipping: String, total: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Fila para Subtotal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Subtotal:", style = MaterialTheme.typography.bodyLarge, color = TextColorLight.copy(alpha = 0.8f))
            Text(subtotal, style = MaterialTheme.typography.bodyLarge, color = TextColorLight)
        }

        Spacer(modifier = Modifier.height(8.dp)) // Más espacio

        // Fila para Envío
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Envío:", style = MaterialTheme.typography.bodyLarge, color = TextColorLight.copy(alpha = 0.8f))
            Text(shipping, style = MaterialTheme.typography.bodyLarge, color = TextColorLight)
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp), // Más espacio
            color = TextColorLight.copy(alpha = 0.2f)
        )

        // Fila para Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total:", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextColorLight) // Más grande
            Text(total, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrightAccentColor) // Total con acento
        }
    }
}


// --- Previsualización ---
@Preview(showBackground = true, backgroundColor = 0xFF1A283A) // Fondo oscuro
@Composable
fun ShoppingCartScreenPreview() {
    MaterialTheme { // Asegura que tengas un MaterialTheme base
        ShoppingCartScreen(onNavigateBack = {}) // Pasa lambda vacía
    }
}