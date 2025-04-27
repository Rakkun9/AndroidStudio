// Dentro de /app/java/com.example.test/ui/screens/ProductListScreen.kt
package com.example.test.ui.screens

// --- Importaciones Necesarias ---
import com.example.test.ui.theme.TestTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow // Para la fila horizontal
import androidx.compose.foundation.lazy.grid.GridCells // Para definir columnas en la grid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid // Para la cuadrícula vertical
import androidx.compose.foundation.lazy.grid.items // Para items en la grid
import androidx.compose.foundation.lazy.items // Para items en LazyRow
import androidx.compose.foundation.shape.CircleShape // Para imágenes circulares si se desea
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.* // Importa todos los iconos filled básicos
import androidx.compose.material3.*
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // Para cargar imágenes reales después
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.R // Asumiendo que tu paquete es com.example.test

// --- COLORES (Usando los de LoginScreen, ¡ajusta si es necesario!) ---
val BrightAccentColor = Color(0xFF00BFA5) // Verde azulado brillante
val IconColorLight = Color.White
val SlightlyLighterBackground = Color(0xFF2A3C51) // Un azul un poco más claro (Adivinado)

// --- La Pantalla Principal ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen() { // Por ahora no necesita parámetros de navegación

    // Estado para saber qué item del BottomNavBar está seleccionado
    var selectedBottomNavItem by remember { mutableStateOf(0) }
    val bottomNavItems = listOf(
        BottomNavItem("Inicio", Icons.Filled.Home),
        BottomNavItem("Amigos", Icons.Filled.AccountBox), // O People
        BottomNavItem("Comunidad", Icons.Filled.Face), // O Forum, Groups
        BottomNavItem("Favoritos", Icons.Filled.Favorite), // O FavoriteBorder
        BottomNavItem("Perfil", Icons.Filled.Person)
    )

    Scaffold(
        containerColor = DarkBackground, // Fondo oscuro para toda la pantalla
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Placeholder para el Logo GGDROP
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(BrightAccentColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bienvenido usuario", fontSize = 18.sp) // Texto ejemplo
                    }
                },
                actions = {
                    IconButton(onClick = { /* Acción Buscar */ }) {
                        Icon(Icons.Filled.Search, contentDescription = "Buscar")
                    }
                    IconButton(onClick = { /* Acción Carrito */ }) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground, // Fondo oscuro
                    titleContentColor = TextColorLight, // Texto claro
                    actionIconContentColor = IconColorLight // Iconos claros
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BrightAccentColor // Fondo verde/azul para la barra inferior
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedBottomNavItem == index,
                        onClick = { selectedBottomNavItem = index /* Acción al seleccionar */ },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        // label = { Text(item.label) } // El diseño no parece tener etiquetas
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground, // Icono seleccionado oscuro sobre fondo claro
                            unselectedIconColor = TextColorLight.copy(alpha = 0.7f), // Icono no seleccionado claro/transparente
                            indicatorColor = TextColorLight // Color del círculo indicador
                        )
                    )
                }
            }
        }
    ) { paddingValues -> // Padding proporcionado por Scaffold (TopBar, BottomBar)

        // Columna principal para el contenido, aplicando el padding del Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // ¡Importante aplicar este padding!
                .padding(horizontal = 16.dp) // Padding lateral general
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Espacio inicial

            // --- Sección "Vistos recientemente" ---
            RecentlyViewedSection()

            Spacer(modifier = Modifier.height(24.dp)) // Espacio antes de la cuadrícula

            // --- Sección "Tus Intereses" (Cuadrícula) ---
            Text(
                text = "Tus Intereses",
                color = TextColorLight,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            InterestsGrid()

        } // Fin Columna principal
    } // Fin Scaffold
}

// --- Composable para la sección "Vistos recientemente" ---
@Composable
fun RecentlyViewedSection() {
    Column {
        Text(
            text = "Vistos recientemente",
            color = TextColorLight.copy(alpha = 0.8f), // Color un poco más tenue
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card( // Contenedor con bordes redondeados
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlightlyLighterBackground), // Fondo ligeramente más claro
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Acción Flecha Izquierda */ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Anterior", tint = IconColorLight)
                }
                LazyRow( // Lista horizontal scrollable
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre items
                ) {
                    items(count = 6) { // Mostrar 6 items de ejemplo
                        // Placeholder para las camisetas
                        Box(
                            modifier = Modifier
                                .size(width = 70.dp, height = 70.dp)
                                .background(DarkBackground, RoundedCornerShape(8.dp))
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Done, contentDescription = null, tint = IconColorLight) // Icono de ropa
                        }
                    }
                }
                IconButton(onClick = { /* Acción Flecha Derecha */ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Siguiente", tint = IconColorLight)
                }
            }
        }
    }
}

// --- Composable para la Cuadrícula de Intereses ---
@Composable
fun InterestsGrid() {
    // Datos de ejemplo para la cuadrícula
    val interestItems = listOf(
        InterestItemData("Oleksandr \"s1mple\" Kostyljev", R.drawable.login_image_png), // Reusa imagen login por ahora
        InterestItemData("Natus Vincere", R.drawable.login_image_png), // Reusa imagen login por ahora
        InterestItemData("Counter-Strike 2", R.drawable.login_image_png), // Reusa imagen login por ahora
        InterestItemData("Valorant", R.drawable.login_image_png) // Reusa imagen login por ahora
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Dos columnas
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp), // Espacio horizontal entre columnas
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio vertical entre filas
    ) {
        items(interestItems) { item ->
            InterestGridItem(item = item)
        }
    }
}

// --- Composable para un Item Individual de la Cuadrícula ---
@Composable
fun InterestGridItem(item: InterestItemData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Acción al hacer clic en item */ }
    ) {
        // Placeholder para la Imagen del Item
        Image(
            // Idealmente usarías painterResource(id = item.imageResId) cuando tengas las imágenes
            painter = painterResource(id = item.imageResId), // Placeholder usando la de login
            contentDescription = item.label,
            modifier = Modifier
                .size(100.dp) // Tamaño de la imagen
                .clip(RoundedCornerShape(12.dp)) // Bordes redondeados para la imagen
                .background(SlightlyLighterBackground), // Fondo por si la imagen no carga/tiene transparencia
            contentScale = ContentScale.Crop // Escala para llenar el espacio
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.label,
            color = TextColorLight,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 2 // Permitir hasta 2 líneas para el texto
        )
    }
}

// --- Clases de Datos Auxiliares ---
data class InterestItemData(val label: String, val imageResId: Int) // Para la cuadrícula
data class BottomNavItem(val label: String, val icon: ImageVector) // Para la barra inferior

// --- Composable `ProductItem` anterior (ya no se usa en este diseño) ---
/*
@Composable
fun ProductItem(...) { ... }
*/


// --- Previsualización ---
@Preview(showBackground = true, backgroundColor = 0xFF1A283A) // Fondo oscuro para preview
@Composable
fun ProductListScreenPreview() {
    TestTheme { // Asegúrate de usar tu tema
        ProductListScreen()
    }
}