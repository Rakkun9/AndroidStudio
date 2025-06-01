// En CreateProductScreen.kt
package com.example.test.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.data.local.AppDatabase
import com.example.test.data.repository.ProductRepository
import com.example.test.ui.product.ProductViewModel // Importa tu ProductViewModel
import com.example.test.ui.theme.TestTheme
import kotlinx.coroutines.flow.collectLatest
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage // Para Coil
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.layout.ContentScale

private const val TAG_CREATE_PRODUCT = "CreateProductScreen"

// Función auxiliar para crear URI para la cámara (como la definimos antes)
fun Context.createImageFileUri(): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    // Usaremos el directorio de archivos internos para más persistencia que el caché externo
    val storageDir = File(filesDir, "images")
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }
    val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
    return FileProvider.getUriForFile(
        this,
        "${packageName}.provider", // Debe coincidir con android:authorities en AndroidManifest.xml
        imageFile
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    productViewModel: ProductViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productCategory by remember { mutableStateOf("") }

    // URI de la imagen que se muestra en la vista previa y se envía al ViewModel
    var imageUriForSubmission by remember { mutableStateOf<Uri?>(null) }
    // URI temporal solo para la salida de la cámara
    var tempCameraImageUri by remember { mutableStateOf<Uri?>(null) }


    val isLoading by productViewModel.isLoading.collectAsState()
    val creationError by productViewModel.productCreationError.collectAsState()

    LaunchedEffect(Unit) {
        productViewModel.productCreationSuccessEvent.collectLatest {
            Toast.makeText(context, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    // --- Launchers para permisos y selección de imagen ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Log.d(TAG_CREATE_PRODUCT, "Imagen de galería seleccionada: $it")
            imageUriForSubmission = it
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            Log.d(TAG_CREATE_PRODUCT, "Foto tomada exitosamente, URI: $tempCameraImageUri")
            imageUriForSubmission =
                tempCameraImageUri // Usamos la URI donde la cámara guardó la foto
        } else {
            Log.d(TAG_CREATE_PRODUCT, "Toma de foto cancelada o fallida.")
            tempCameraImageUri = null
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG_CREATE_PRODUCT, "Permiso de cámara concedido.")
            val newUri = context.createImageFileUri()
            tempCameraImageUri = newUri // Guardamos la URI para el resultado de la cámara
            cameraLauncher.launch(newUri)
        } else {
            Log.d(TAG_CREATE_PRODUCT, "Permiso de cámara denegado.")
            Toast.makeText(context, "Permiso de cámara denegado.", Toast.LENGTH_SHORT).show()
        }
    }
    // --- Fin Launchers ---

    fun handleCreateProduct() {
        productViewModel.createProduct(
            name = productName,
            description = productDescription,
            priceText = productPrice,
            category = productCategory,
            imageUri = imageUriForSubmission
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Crear Nuevo Producto", color = TextColorLight) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = IconColorLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextColorLight,
                    navigationIconContentColor = IconColorLight
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Información del Producto",
                style = MaterialTheme.typography.titleLarge,
                color = TextColorLight
            )

            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = {
                    Text(
                        "Nombre del Producto",
                        color = UnfocusedInputColor
                    )
                }, // Color para label
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                // Aplicamos colores personalizados
                textStyle = LocalTextStyle.current.copy(color = TextColorLight) // Color para el texto ingresado
            )
            OutlinedTextField(
                value = productDescription,
                onValueChange = { productDescription = it },
                label = { Text("Descripción", color = UnfocusedInputColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4,

                textStyle = LocalTextStyle.current.copy(color = TextColorLight)
            )
            OutlinedTextField(
                value = productPrice,
                onValueChange = { productPrice = it },
                label = { Text("Precio (ej: 25.99)", color = UnfocusedInputColor) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),

                textStyle = LocalTextStyle.current.copy(color = TextColorLight)
            )
            OutlinedTextField(
                value = productCategory,
                onValueChange = { productCategory = it },
                label = { Text("Categoría", color = UnfocusedInputColor) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,

                textStyle = LocalTextStyle.current.copy(color = TextColorLight)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Imagen del Producto",
                style = MaterialTheme.typography.titleMedium,
                color = TextColorLight
            )

            // --- Vista Previa de la Imagen ---
            if (imageUriForSubmission != null) {
                AsyncImage(
                    model = imageUriForSubmission,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, UnfocusedInputColor, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(vertical = 8.dp)
                        .background(SlightlyLighterBackground, RoundedCornerShape(8.dp))
                        .border(1.dp, UnfocusedInputColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ninguna imagen seleccionada", color = TextColorLight.copy(alpha = 0.7f))
                }
            }
            // --- Fin Vista Previa ---

            // --- Botones para Imagen ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) -> {
                                val newUri = context.createImageFileUri()
                                tempCameraImageUri = newUri
                                cameraLauncher.launch(newUri)
                            }

                            else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SlightlyLighterBackground)
                ) { Text("Tomar Foto", color = TextColorLight) }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = SlightlyLighterBackground)
                ) { Text("Galería", color = TextColorLight) }
            }
            // --- Fin Botones Imagen ---

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = BrightAccentColor
                )
            }
            creationError?.let {
                Text(it, color = ErrorColor, modifier = Modifier.padding(vertical = 8.dp))
            }

            Button(
                onClick = { handleCreateProduct() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = BrightAccentColor)
            ) {
                Text("Guardar Producto", color = DarkerTextColor, fontWeight = FontWeight.Bold)
            }
        } // Fin Column
    } // Fin Scaffold
}

// Preview para CreateProductScreen
@Preview(showBackground = true, backgroundColor = 0xFF1A283A)
@Composable
fun CreateProductScreenPreview() {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val productRepository = ProductRepository(db.productDao())
    val previewProductViewModel = ProductViewModel(productRepository, context.applicationContext)
    TestTheme { // Asegúrate que TestTheme esté disponible/importado o usa MaterialTheme
        CreateProductScreen(
            productViewModel = previewProductViewModel,
            onNavigateBack = {}
        )
    }
}