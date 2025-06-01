// En AppDatabase.kt
package com.example.test.data.local // O tu paquete

import ProductDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase // Asegúrate de importar esta
import com.example.test.data.dao.UserDao
import com.example.test.data.model.Product
import com.example.test.data.model.User
import com.example.test.util.PasswordUtils // Importa tu PasswordUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Product::class],
    version = 4,
    exportSchema = false
) // <-- VERSIÓN INCREMENTADA
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.userDao())
                    }
                }
            }

            // Esta función se llama solo cuando la BD se crea. Si se destruye por fallbackToDestructiveMigration,
            // se volverá a llamar.
            suspend fun populateDatabase(userDao: UserDao) {
                val user1 = User(
                    name = "Usuario Uno",
                    email = "user1@example.com",
                    hashedPassword = PasswordUtils.hashPassword("password123"),
                    role = "Cliente" // <-- ROL AÑADIDO
                )
                val user2 = User(
                    name = "Admin Dos",
                    email = "admin@example.com",
                    hashedPassword = PasswordUtils.hashPassword("adminpass"),
                    role = "Administrador" // <-- ROL AÑADIDO
                )
                userDao.insertUser(user1)
                userDao.insertUser(user2)
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(AppDatabaseCallback(CoroutineScope(Dispatchers.IO)))
                    .fallbackToDestructiveMigration() // <-- AÑADE ESTO PARA SIMPLIFICAR MIGRACIONES EN DESARROLLO
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}