// Dentro de /app/java/com.example.test/ui/screens/ProductListScreen.kt
// (Asegúrate que 'com.example.test' sea tu paquete real)

package com.example.test.ui.screens

// --- Importaciones Necesarias ---
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Necesario para usar 'items' en LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart // Icono de ejemplo
import androidx.compose.material3.* // O androidx.compose.material.* si usas Material 2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // Para el icono de placeholder
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Para tamaño de fuente

// --- La Pantalla Principal ---
@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar en Material 3
@Composable
fun ProductListScreen() {
    Scaffold(
        topBar = {
            // Barra superior con el título
            TopAppBar(
                title = { Text("Productos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer, // Color de fondo
                    titleContentColor = MaterialTheme.colorScheme.primary // Color del texto
                )
            )
        }
    ) { paddingValues -> // El contenido de la pantalla va aquí, 'paddingValues' contiene el padding necesario por el TopAppBar

        // LazyColumn para mostrar la lista eficientemente
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplica el padding del Scaffold
                .padding(horizontal = 8.dp), // Padding horizontal para la lista
            verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre items de la lista
        ) {
            // --- Elementos de la lista ---
            // Como no tenemos datos reales, vamos a repetir un item de ejemplo 10 veces
            items(count = 10) { index ->
                // Llama a la función que dibuja cada item de producto
                ProductItem(
                    productName = "Producto Ejemplo ${index + 1}",
                    productPrice = "${(20..100).random()}.99 €", // Precio aleatorio de ejemplo
                    // Puedes añadir un parámetro para una imagen real aquí si quieres
                )
            }
        }
    }
}

// --- Composable para un solo Item de Producto ---
@Composable
fun ProductItem(
    productName: String,
    productPrice: String,
    imageVector: ImageVector = Icons.Default.ShoppingCart // Icono placeholder por defecto
) {
    Card( // Usamos Card para darle un contenedor bonito a cada producto
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Sombra ligera
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically // Alinea verticalmente los elementos en la fila
        ) {
            // Placeholder para la Imagen del Producto
            Image(
                imageVector = imageVector, // Usa el icono como placeholder
                contentDescription = "Imagen de $productName",
                modifier = Modifier
                    .size(70.dp) // Tamaño fijo para la imagen/placeholder
                    .background(MaterialTheme.colorScheme.secondaryContainer) // Fondo para el placeholder
                    .padding(8.dp), // Padding dentro del fondo
                contentScale = ContentScale.Fit // Escala del contenido (icono)
            )

            Spacer(modifier = Modifier.width(16.dp)) // Espacio horizontal

            // Columna para Nombre y Precio
            Column(
                modifier = Modifier.weight(1f) // Ocupa el espacio restante flexible
            ) {
                Text(
                    text = productName,
                    style = MaterialTheme.typography.titleMedium, // Estilo para el nombre
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp)) // Espacio vertical
                Text(
                    text = productPrice,
                    style = MaterialTheme.typography.bodyLarge, // Estilo para el precio
                    color = MaterialTheme.colorScheme.primary // Color distintivo para el precio
                )
            }

            Spacer(modifier = Modifier.width(8.dp)) // Espacio horizontal

            // Botón para añadir al carrito
            Button(
                onClick = { /* No hace nada todavía */ },
                modifier = Modifier.wrapContentWidth() // Ajusta el ancho al contenido
            ) {
                Text("Añadir")
            }
        }
    }
}


// --- Previsualización ---
@Preview(showBackground = true, widthDp = 360, heightDp = 640) // Definimos tamaño de preview
@Composable
fun ProductListScreenPreview() {
    // Es bueno envolver el preview en un tema si lo tienes definido
    // Asumiendo un tema Material 3 básico:
    MaterialTheme { // Quita esto si no tienes tema o usa el tuyo
        ProductListScreen()
    }
}