// En Product.kt
package com.example.test.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String, // Título del producto
    val description: String,
    val price: Double,
    val imagePath: String?, // <-- CAMBIADO de imageResId. Guardará la ruta al archivo de imagen. Nullable por si no hay imagen.
    val category: String
)