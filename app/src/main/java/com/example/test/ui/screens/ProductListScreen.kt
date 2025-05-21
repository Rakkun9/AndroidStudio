// Dentro de /app/java/com.example.test/ui/screens/ProductListScreen.kt
package com.example.test.ui.screens

// --- Importaciones Necesarias ---
import android.Manifest // Para los nombres de los permisos
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder // Para convertir coordenadas a dirección (opcional)
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.* // Importa LaunchedEffect, getValue, setValue, mutableStateOf, remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.test.R
import com.example.test.ui.theme.TestTheme // Asegúrate que esta ruta sea correcta a tu tema
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority // Para la precisión de la ubicación
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale // Para Geocoder
import android.util.Log // Para logs

// --- COLORES (Asegúrate que estén accesibles o defínelos/impórtalos) ---
// Asumiendo que DarkBackground y TextColorLight son globales o importados desde tu tema
// Ejemplo: import com.example.test.ui.theme.DarkBackground
// Ejemplo: import com.example.test.ui.theme.TextColorLight
val BrightAccentColor = Color(0xFF00BFA5)
val IconColorLight = Color.White
val SlightlyLighterBackground = Color(0xFF2A3C51)


// --- La Pantalla Principal ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(onNavigateToCart: () -> Unit, onNavigateToProfile: () -> Unit) {


    val context = LocalContext.current
    var locationInfo by remember { mutableStateOf<String>("Buscando ubicación...") }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // --- 1. Función separada para obtener la ubicación ---
    fun fetchDeviceLocation() {
        // Volvemos a verificar permisos aquí por seguridad, aunque usualmente se llama después de concederlos.
        val fineLocationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted && !coarseLocationGranted) {
            Log.d("ProductListScreen", "fetchDeviceLocation llamado sin permisos.")
            locationInfo = "Permiso de ubicación requerido." // Actualiza la UI si es necesario
            // Podrías volver a lanzar el permission launcher aquí si la lógica lo requiere,
            // pero cuidado con los bucles. Por ahora, asumimos que se llama cuando hay permisos.
            // locationPermissionLauncher.launch(locationPermissions)
            return
        }

        Log.d("ProductListScreen", "Intentando obtener la ubicación actual del dispositivo.")
        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, // Puedes probar con HIGH_ACCURACY si necesitas más precisión
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("ProductListScreen", "Ubicación obtenida: Lat ${location.latitude}, Lon ${location.longitude}")
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        @Suppress("DEPRECATION") // Geocoder.getFromLocation es deprecated en API 33+ pero funciona con try-catch
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (addresses != null && addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val city = address.locality ?: "Ciudad Desconocida"
                            val country = address.countryName ?: ""
                            locationInfo = "$city, $country"
                        } else {
                            locationInfo = "Lat: ${"%.2f".format(location.latitude)}, Lon: ${"%.2f".format(location.longitude)}"
                        }
                    } catch (e: Exception) {
                        Log.e("ProductListScreen", "Error en Geocoding", e)
                        locationInfo = "Lat: ${"%.2f".format(location.latitude)}, Lon: ${"%.2f".format(location.longitude)}"
                    }
                } else {
                    Log.d("ProductListScreen", "No se pudo obtener la ubicación (resultado null).")
                    locationInfo = "Ubicación no obtenida"
                }
            }.addOnFailureListener { e ->
                Log.e("ProductListScreen", "Error al obtener ubicación desde fusedLocationClient", e)
                locationInfo = "Error al obtener ubicación"
            }
        } catch (e: SecurityException) {
            Log.e("ProductListScreen", "Excepción de seguridad al llamar a getCurrentLocation", e)
            locationInfo = "Permiso denegado (excepción de seguridad)"
        }
    }


    // --- 2. Launcher de permisos actualizado ---
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // Verifica si AL MENOS UNO de los permisos fue concedido (COARSE o FINE)
            if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
            ) {
                Log.d("ProductListScreen", "Permiso de ubicación CONCEDIDO vía launcher.")
                fetchDeviceLocation() // Llama a la función separada
            } else {
                Log.d("ProductListScreen", "Permiso de ubicación DENEGADO vía launcher.")
                locationInfo = "Permiso de ubicación denegado"
            }
        }
    )

    // --- 3. LaunchedEffect actualizado ---
    LaunchedEffect(Unit) {
        val fineLocationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            Log.d("ProductListScreen", "Permisos ya concedidos al cargar la pantalla. Obteniendo ubicación.")
            fetchDeviceLocation() // Llama directamente a la función si ya hay permisos
        } else {
            Log.d("ProductListScreen", "Solicitando permisos de ubicación al cargar la pantalla.")
            locationPermissionLauncher.launch(locationPermissions) // Lanza la solicitud de permisos
        }
    }
    // --- Fin Lógica Permisos y Ubicación ---

    var selectedBottomNavItem by remember { mutableStateOf(0) }
    val bottomNavItems = listOf(
        BottomNavItem("Inicio", Icons.Filled.Home),
        BottomNavItem("Amigos", Icons.Filled.AccountBox),
        BottomNavItem("Comunidad", Icons.Filled.Face),
        BottomNavItem("Favoritos", Icons.Filled.Favorite),
        BottomNavItem("Perfil", Icons.Filled.Person)
    )

    Scaffold(
        containerColor = DarkBackground, // Asume que DarkBackground está definido/importado
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(BrightAccentColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Bienvenido usuario",
                            fontSize = 18.sp,
                            color = TextColorLight // Asegura color de texto
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Acción Buscar */ }) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Buscar",
                            tint = IconColorLight // Asegura color de icono
                        )
                    }
                    IconButton(onClick = onNavigateToCart) {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = "Carrito",
                            tint = IconColorLight // Asegura color de icono
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground, // Asume que DarkBackground está definido/importado
                    titleContentColor = TextColorLight, // Asume que TextColorLight está definido/importado
                    actionIconContentColor = IconColorLight
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
                        onClick = {
                            selectedBottomNavItem = index
                            // --- LÓGICA DE NAVEGACIÓN DEL BOTTOM BAR ---
                            when (item.label) { // O usa el índice si prefieres
                                "Inicio" -> { /* Podrías recargar o no hacer nada si ya estás aquí */ }
                                "Perfil" -> onNavigateToProfile() // <-- LLAMA A LA NUEVA LAMBDA
                                // Añade casos para otros items si es necesario
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground, // Asume que DarkBackground está definido/importado
                            unselectedIconColor = TextColorLight.copy(alpha = 0.7f), // Asume que TextColorLight está definido/importado
                            indicatorColor = TextColorLight // Asume que TextColorLight está definido/importado
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
            // --- Mostrar Información de Ubicación ---
            Text(
                text = locationInfo,
                color = TextColorLight.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall, // Un poco más grande que labelSmall
                modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                textAlign = TextAlign.Center // Centrar el texto de ubicación
            )
            // --- Fin Mostrar Ubicación ---

            RecentlyViewedSection() // Asumimos que esta función está definida como antes

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Tus Intereses",
                color = TextColorLight, // Asume que TextColorLight está definido/importado
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            InterestsGrid() // Asumimos que esta función está definida como antes
        }
    }
}

// --- Tus otros Composables (RecentlyViewedSection, InterestsGrid, InterestGridItem, data classes) ---
// ... (Los mantienes como estaban en tu código anterior, o los adaptas si es necesario)
// Por ejemplo, si `DarkBackground` o `TextColorLight` se usan dentro de ellos, asegúrate de que sean accesibles.

@Composable
fun RecentlyViewedSection() { // Ejemplo de cómo estaba antes, asegúrate que los colores sean accesibles
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Anterior", tint = IconColorLight)
                }
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(count = 6) {
                        Box(
                            modifier = Modifier
                                .size(width = 70.dp, height = 70.dp)
                                .background(DarkBackground, RoundedCornerShape(8.dp)) // Usa DarkBackground
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = IconColorLight) // Cambiado de Done a Checkroom
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

@Composable
fun InterestsGrid() { // Ejemplo de cómo estaba antes
    val interestItems = listOf(
        InterestItemData("Oleksandr \"s1mple\" Kostyljev", R.drawable.s1mple_mi_dios),
        InterestItemData("Teclado Razer", R.drawable.product_keyboard_razer),
        InterestItemData("Mouse Razer", R.drawable.mouse_razer),
        InterestItemData("Valorant", R.drawable.valorant_logo)
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(), // Considera darle una altura fija o .weight si está dentro de una columna con scroll
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
fun InterestGridItem(item: InterestItemData) { // Ejemplo de cómo estaba antes
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().clickable {  }
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

// --- Clases de datos que ya tenías ---
data class InterestItemData(val label: String, val imageResId: Int) // Ya la tienes arriba
data class BottomNavItem(val label: String, val icon: ImageVector) // Ya la tienes arriba


// --- Previsualización ---
@Preview(showBackground = true, backgroundColor = 0xFF1A283A)
@Composable
fun ProductListScreenPreview() {
    // Asegúrate que TestTheme esté disponible/importado, o usa MaterialTheme
    TestTheme {
        ProductListScreen(onNavigateToCart = {} , onNavigateToProfile = {})

    }
}