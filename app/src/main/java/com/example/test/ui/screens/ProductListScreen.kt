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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test.R

val BrightAccentColor = Color(0xFF00BFA5)
val IconColorLight = Color.White
val SlightlyLighterBackground = Color(0xFF2A3C51)

// --- La Pantalla Principal ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen( onNavigateToCart: () -> Unit) {

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
                    IconButton(onClick =  onNavigateToCart) {
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
                containerColor = BrightAccentColor
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedBottomNavItem == index,
                        onClick = { selectedBottomNavItem = index },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground,
                            unselectedIconColor = TextColorLight.copy(alpha = 0.7f),
                            indicatorColor = TextColorLight
                        )
                    )
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            RecentlyViewedSection()

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Tus Intereses",
                color = TextColorLight,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            InterestsGrid()

        }
    }
}

@Composable
fun RecentlyViewedSection() {
    Column {
        Text(
            text = "Vistos recientemente",
            color = TextColorLight.copy(alpha = 0.8f),
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlightlyLighterBackground),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Anterior",
                        tint = IconColorLight
                    )
                }
                LazyRow( // Lista horizontal scrollable
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(count = 6) { // Mostrar 6 items
                        Box(
                            modifier = Modifier
                                .size(width = 70.dp, height = 70.dp)
                                .background(DarkBackground, RoundedCornerShape(8.dp))
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Done,
                                contentDescription = null,
                                tint = IconColorLight
                            )
                        }
                    }
                }
                IconButton(onClick = { /* Acción Flecha Derecha */ }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Siguiente",
                        tint = IconColorLight
                    )
                }
            }
        }
    }
}


@Composable
fun InterestsGrid() {

    val interestItems = listOf(
        InterestItemData(
            "Oleksandr \"s1mple\" Kostyljev",
            R.drawable.s1mple_mi_dios
        ),
        InterestItemData(
            "Teclado Razer",
            R.drawable.product_keyboard_razer
        ),
        InterestItemData("Mouse Razer", R.drawable.mouse_razer),
        InterestItemData("Valorant", R.drawable.valorant_logo)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(interestItems) { item ->
            InterestGridItem(item = item)
        }
    }
}

@Composable
fun InterestGridItem(item: InterestItemData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {  }
    ) {
        Image(

            painter = painterResource(id = item.imageResId),
            contentDescription = item.label,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SlightlyLighterBackground),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.label,
            color = TextColorLight,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

data class InterestItemData(val label: String, val imageResId: Int)
data class BottomNavItem(val label: String, val icon: ImageVector)


@Preview(showBackground = true, backgroundColor = 0xFF1A283A)
@Composable
fun ProductListScreenPreview() {
    TestTheme {
        ProductListScreen(onNavigateToCart = {})
    }
}