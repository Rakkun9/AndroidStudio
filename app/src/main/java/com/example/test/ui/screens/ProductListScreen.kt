package com.example.test.ui.screens

import android.Manifest

import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.test.ui.theme.TestTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale // Para Geocoder
import android.util.Log // Para logs

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.test.ui.product.ProductViewModel
import com.example.test.ui.product.ProductViewModelFactory


import coil.compose.AsyncImage
import com.example.test.data.local.AppDatabase
import com.example.test.data.model.Product
import com.example.test.data.repository.UserRepository
import com.example.test.ui.auth.AuthViewModel
import java.io.File

val BrightAccentColor = Color(0xFF00BFA5)
val IconColorLight = Color.White
val SlightlyLighterBackground = Color(0xFF2A3C51)

data class RecentlyViewedItem(
    val id: Int,
    val name: String,
    val imageResId: Int,
    val iconPlaceholder: ImageVector = Icons.Filled.Check
)

// --- La Pantalla Principal ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    authViewModel: AuthViewModel,
    onNavigateToCreateProduct: () -> Unit
) {


    val currentUser by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    var locationInfo by remember { mutableStateOf<String>("Buscando ubicación...") }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val productViewModel: ProductViewModel =
        viewModel(factory = ProductViewModelFactory(context.applicationContext))
    val productsFromDb by productViewModel.products.collectAsState()
    val sampleRecentlyViewed = remember {
        listOf(
            RecentlyViewedItem(1, "Jersey Pro N1", R.drawable.login_image_png),
            RecentlyViewedItem(2, "Camiseta Equipo Z", R.drawable.login_image_png),
            RecentlyViewedItem(3, "Uniforme Gamer", R.drawable.login_image_png),
            RecentlyViewedItem(4, "Edición Limitada X", R.drawable.login_image_png),
            RecentlyViewedItem(5, "Colección Fan", R.drawable.login_image_png),
        )
    }


    fun fetchDeviceLocation() {

        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted && !coarseLocationGranted) {
            Log.d("ProductListScreen", "fetchDeviceLocation llamado sin permisos.")
            locationInfo = "Permiso de ubicación requerido."

            return
        }

        Log.d("ProductListScreen", "Intentando obtener la ubicación actual del dispositivo.")
        try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, // Puedes probar con HIGH_ACCURACY si necesitas más precisión
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    Log.d(
                        "ProductListScreen",
                        "Ubicación obtenida: Lat ${location.latitude}, Lon ${location.longitude}"
                    )
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())

                        @Suppress("DEPRECATION") // Geocoder.getFromLocation es deprecated en API 33+ pero funciona con try-catch
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (addresses != null && addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val city = address.locality ?: "Ciudad Desconocida"
                            val country = address.countryName ?: ""
                            locationInfo = "$city, $country"
                        } else {
                            locationInfo = "Lat: ${"%.2f".format(location.latitude)}, Lon: ${
                                "%.2f".format(location.longitude)
                            }"
                        }
                    } catch (e: Exception) {
                        Log.e("ProductListScreen", "Error en Geocoding", e)
                        locationInfo =
                            "Lat: ${"%.2f".format(location.latitude)}, Lon: ${"%.2f".format(location.longitude)}"
                    }
                } else {
                    Log.d("ProductListScreen", "No se pudo obtener la ubicación (resultado null).")
                    locationInfo = "Ubicación no obtenida"
                }
            }.addOnFailureListener { e ->
                Log.e(
                    "ProductListScreen",
                    "Error al obtener ubicación desde fusedLocationClient",
                    e
                )
                locationInfo = "Error al obtener ubicación"
            }
        } catch (e: SecurityException) {
            Log.e("ProductListScreen", "Excepción de seguridad al llamar a getCurrentLocation", e)
            locationInfo = "Permiso denegado (excepción de seguridad)"
        }
    }


    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->

            if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
            ) {
                Log.d("ProductListScreen", "Permiso de ubicación CONCEDIDO vía launcher.")
                fetchDeviceLocation()
            } else {
                Log.d("ProductListScreen", "Permiso de ubicación DENEGADO vía launcher.")
                locationInfo = "Permiso de ubicación denegado"
            }
        }
    )


    LaunchedEffect(Unit) {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            Log.d(
                "ProductListScreen",
                "Permisos ya concedidos al cargar la pantalla. Obteniendo ubicación."
            )
            fetchDeviceLocation()
        } else {
            Log.d("ProductListScreen", "Solicitando permisos de ubicación al cargar la pantalla.")
            locationPermissionLauncher.launch(locationPermissions)
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
        containerColor = DarkBackground,
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
                            color = TextColorLight
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Acción Buscar */ }) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Buscar",
                            tint = IconColorLight
                        )
                    }
                    IconButton(onClick = onNavigateToCart) {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = "Carrito",
                            tint = IconColorLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextColorLight,
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

                            when (item.label) {
                                "Inicio" -> {}
                                "Perfil" -> onNavigateToProfile()

                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground,
                            unselectedIconColor = TextColorLight.copy(alpha = 0.7f),
                            indicatorColor = TextColorLight
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentUser?.role == "Administrador") {
                FloatingActionButton(
                    onClick = onNavigateToCreateProduct,
                    containerColor = BrightAccentColor,
                    contentColor = DarkerTextColor
                ) {
                    Icon(Icons.Filled.Add, "Crear Producto")
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

            Text(
                text = locationInfo,
                color = TextColorLight.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall, // Un poco más grande que labelSmall
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )


            RecentlyViewedSection(items = sampleRecentlyViewed, onItemClick = { /* TODO */ })
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Nuestros Productos", // Cambiamos el título
                color = TextColorLight,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // --- USAREMOS LA LISTA productsFromDb AQUÍ ---
            if (productsFromDb.isEmpty()) {
                Text(
                    "No hay productos disponibles en este momento.",
                    color = TextColorLight.copy(alpha = 0.7f),
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                ProductsGrid(items = productsFromDb, onItemClick = { product ->
                    Log.d("ProductListScreen", "Producto clickeado: ${product.name}")
                    // TODO: Navegar a pantalla de detalle del producto si la tienes
                })
            }
        }

    }
}

@Composable
fun RecentlyViewedSection(
    items: List<RecentlyViewedItem>,
    onItemClick: (RecentlyViewedItem) -> Unit
) {
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
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(count = 6) {
                        Box(
                            modifier = Modifier
                                .size(width = 70.dp, height = 70.dp)
                                .background(DarkBackground, RoundedCornerShape(8.dp))
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = IconColorLight
                            )
                        }
                    }
                }
                IconButton(onClick = { }) {
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
fun ProductGridItem(
    product: Product, // Acepta un objeto Product
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(bottom = 8.dp)
    ) {
        AsyncImage(
            model = product.imagePath?.let { File(it) },
            contentDescription = product.name,
            placeholder = painterResource(id = R.drawable.login_image_png),
            error = painterResource(id = R.drawable.login_image_png),
            modifier = Modifier
                .size(120.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SlightlyLighterBackground),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = product.name,
            color = TextColorLight,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2
        )
        Text(
            text = "${product.price} €",
            color = BrightAccentColor,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InterestGridItem(item: InterestItemData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
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

@Composable
fun ProductsGrid(
    items: List<Product>,
    onItemClick: (Product) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { product ->
            ProductGridItem(product = product, onClick = { onItemClick(product) })
        }
    }
}

// --- Clases de datos que ya tenías ---
data class InterestItemData(val label: String, val imageResId: Int)
data class BottomNavItem(val label: String, val icon: ImageVector)


// --- Previsualización ---
@Preview(showBackground = true, backgroundColor = 0xFF1A283A)
@Composable
fun ProductListScreenPreview() {
    val context = LocalContext.current
    val previewAuthViewModel =
        AuthViewModel(UserRepository(AppDatabase.getDatabase(context).userDao()))
    TestTheme {
        ProductListScreen(
            authViewModel = previewAuthViewModel,
            onNavigateToCart = {},
            onNavigateToProfile = {},
            onNavigateToCreateProduct = {})

    }
}