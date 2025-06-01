// En ProductRepository.kt
package com.example.test.data.repository

import ProductDao
import com.example.test.data.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }

    fun getAllProducts(): Flow<List<Product>> {

        return productDao.getAllProducts()
    }
    // Añade aquí más funciones según necesites (getProductById, update, delete)
}