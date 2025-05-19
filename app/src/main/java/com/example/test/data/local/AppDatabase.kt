// En AppDatabase.kt
package com.example.test.data.local // O tu paquete

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase // Asegúrate de importar esta
import com.example.test.data.dao.UserDao
import com.example.test.data.model.User
import com.example.test.util.PasswordUtils // Importa tu PasswordUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

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

            suspend fun populateDatabase(userDao: UserDao) {

                // Usuarios de ejemplo
                val user1 = User(
                    name = "Usuario Uno",
                    email = "user1@example.com",
                    hashedPassword = PasswordUtils.hashPassword("password123")
                )
                val user2 = User(
                    name = "Usuario Dos",
                    email = "user2@example.com",
                    hashedPassword = PasswordUtils.hashPassword("securepass")
                )

                userDao.insertUser(user1)
                userDao.insertUser(user2)
            }
        }
        // --- Fin del Callback ---


        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(AppDatabaseCallback(CoroutineScope(Dispatchers.IO))) // <-- AÑADE EL CALLBACK AQUÍ
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}