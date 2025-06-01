// En ProductViewModel.kt
package com.example.test.ui.product // O el paquete que elijas

import android.content.Context
import android.net.Uri // Para el URI de la imagen
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test.data.local.AppDatabase
import com.example.test.data.model.Product
import com.example.test.data.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File // Para manejar archivos de imagen
import java.io.FileOutputStream
import java.io.InputStream


class ProductViewModel(
    private val productRepository: ProductRepository,
    private val applicationContext: Context // Necesario para guardar imágenes
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _productCreationError = MutableStateFlow<String?>(null)
    val productCreationError = _productCreationError.asStateFlow()

    private val _productCreationSuccessEvent = MutableSharedFlow<Unit>()
    val productCreationSuccessEvent = _productCreationSuccessEvent.asSharedFlow()

    // Para la lista de productos
    val products: StateFlow<List<Product>> = productRepository.getAllProducts()
        .catch { e ->
            Log.e("ProductViewModel", "Error obteniendo productos", e)
            emit(emptyList()) // Emite lista vacía en caso de error
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun createProduct(
        name: String,
        description: String,
        priceText: String,
        category: String,
        imageUri: Uri?
    ) {
        _isLoading.value = true
        _productCreationError.value = null

        // Validación
        if (name.isBlank() || description.isBlank() || priceText.isBlank() || category.isBlank()) {
            _productCreationError.value = "Todos los campos son obligatorios."
            _isLoading.value = false
            return
        }
        val price = priceText.toDoubleOrNull()
        if (price == null || price <= 0) {
            _productCreationError.value = "Precio inválido."
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                var imagePath: String? = null
                if (imageUri != null) {
                    imagePath = saveImageToInternalStorage(imageUri)
                    if (imagePath == null) {
                        _productCreationError.value = "Error al guardar la imagen."
                        _isLoading.value = false
                        return@launch
                    }
                }

                val newProduct = Product(
                    name = name,
                    description = description,
                    price = price,
                    category = category,
                    imagePath = imagePath
                )
                productRepository.insertProduct(newProduct)
                _productCreationSuccessEvent.emit(Unit)
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error al crear producto", e)
                _productCreationError.value = "Error al crear el producto: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    // Función para guardar la imagen en almacenamiento interno y devolver la ruta
    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = applicationContext.contentResolver.openInputStream(uri)
            val fileName = "product_image_${System.currentTimeMillis()}.jpg"
            val file = File(
                applicationContext.filesDir,
                fileName
            ) // Guarda en el directorio de archivos internos de la app
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            Log.d("ProductViewModel", "Imagen guardada en: ${file.absolutePath}")
            file.absolutePath // Devuelve la ruta absoluta del archivo guardado
        } catch (e: Exception) {
            Log.e("ProductViewModel", "Error al guardar imagen", e)
            null
        }
    }
}

class ProductViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            val productDao = AppDatabase.getDatabase(context.applicationContext).productDao()
            val repository = ProductRepository(productDao)
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository, context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}