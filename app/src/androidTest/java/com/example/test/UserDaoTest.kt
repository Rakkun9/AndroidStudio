// En app/src/androidTest/java/com/example/test/data/dao/UserDaoTest.kt
package com.example.test.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.test.data.local.AppDatabase // Importa tu AppDatabase
import com.example.test.data.model.User       // Importa tu User
import com.example.test.util.PasswordUtils    // Importa tu PasswordUtils
import kotlinx.coroutines.flow.first // Para obtener el primer valor de un Flow
import kotlinx.coroutines.runBlocking // Para ejecutar suspend functions en pruebas
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Assert.* // Asegúrate de que esta importación esté para assertEquals, assertNotNull, etc.

@RunWith(AndroidJUnit4::class) // Ejecutor para pruebas de instrumentación
class UserDaoTest {

    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    // Usuarios de ejemplo para las pruebas
    private val user1 = User(id = 1, name = "Test User One", email = "test1@example.com", hashedPassword = PasswordUtils.hashPassword("password123"), role = "Cliente")



    @Before // Se ejecuta antes de cada prueba
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
    }

    @After // Se ejecuta después de cada prueba
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertUserAndGetByEmail_returnsCorrectUser() = runBlocking {
        userDao.insertUser(user1)
        val retrievedUser = userDao.getUserByEmail(user1.email)

        assertNotNull("El usuario recuperado no debería ser null", retrievedUser)
        assertEquals("El email no coincide", user1.email, retrievedUser?.email)
        assertEquals("El nombre no coincide", user1.name, retrievedUser?.name)
        assertEquals("El rol no coincide", user1.role, retrievedUser?.role)
        assertEquals("El ID no coincide", user1.id, retrievedUser?.id)
        assertTrue("La verificación de contraseña falló", PasswordUtils.verifyPassword("password123", retrievedUser!!.hashedPassword))
    }

    @Test
    @Throws(Exception::class)
    fun insertUserWithConflict_ignoresNewUserIfEmailExists() = runBlocking {
        userDao.insertUser(user1)
        val conflictingUser = User(name = "Conflicting User", email = user1.email, hashedPassword = PasswordUtils.hashPassword("newpass"), role = "Cliente")
        val resultId = userDao.insertUser(conflictingUser) // onConflict = OnConflictStrategy.IGNORE

        assertEquals("El ID resultante debería ser -1L para inserción ignorada", -1L, resultId)

        val retrievedUser = userDao.getUserByEmail(user1.email)
        assertEquals("El nombre del usuario no debería haber cambiado", user1.name, retrievedUser?.name)
    }


    @Test
    @Throws(Exception::class)
    fun getUserById_whenUserExists_returnsCorrectUser() = runBlocking {
        userDao.insertUser(user1)
        val retrievedUserFlow = userDao.getUserById(user1.id)
        val retrievedUser = retrievedUserFlow.first()

        assertNotNull("El usuario recuperado no debería ser null", retrievedUser)
        assertEquals("El ID no coincide", user1.id, retrievedUser?.id)
        assertEquals("El nombre no coincide", user1.name, retrievedUser?.name)
    }

    @Test
    @Throws(Exception::class)
    fun getUserById_whenUserNotExists_returnsNull() = runBlocking {
        val retrievedUserFlow = userDao.getUserById(999) // ID que no existe
        val retrievedUser = retrievedUserFlow.first()
        assertNull("El usuario debería ser null para un ID no existente", retrievedUser)
    }

    @Test
    @Throws(Exception::class)
    fun updateUser_updatesExistingUserDetails() = runBlocking {
        userDao.insertUser(user1)
        val userToUpdate = userDao.getUserByEmail(user1.email)!!

        val updatedUser = userToUpdate.copy(name = "Updated Test User", role = "SuperCliente")
        userDao.updateUser(updatedUser)

        val retrievedUserFlow = userDao.getUserById(userToUpdate.id)
        val retrievedUser = retrievedUserFlow.first()

        assertEquals("El nombre debería haberse actualizado", "Updated Test User", retrievedUser?.name)
        assertEquals("El rol debería haberse actualizado", "SuperCliente", retrievedUser?.role)
        assertEquals("El email no debería haber cambiado", user1.email, retrievedUser?.email)
    }

    @Test
    @Throws(Exception::class)
    fun deleteUserById_removesUserFromDatabase() = runBlocking {
        userDao.insertUser(user1)
        var retrievedUser = userDao.getUserByEmail(user1.email)
        assertNotNull("El usuario debería existir antes de borrarlo", retrievedUser)

        val deleteResult = userDao.deleteUserById(user1.id)
        assertEquals("El resultado de la eliminación debería ser 1 (fila afectada)", 1, deleteResult)

        retrievedUser = userDao.getUserByEmail(user1.email)
        assertNull("El usuario debería ser null después de borrarlo", retrievedUser)
    }

    @Test
    @Throws(Exception::class)
    fun deleteUserById_nonExistentUser_returnsZeroRowsAffected() = runBlocking {
        val deleteResult = userDao.deleteUserById(999) // ID que no existe
        assertEquals("El resultado de la eliminación debería ser 0 para un ID no existente", 0, deleteResult)
    }
}