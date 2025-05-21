// En AuthViewModel.kt
package com.example.test.ui.auth // O tu paquete

import android.content.Context
import android.util.Log // Para logs de depuración
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test.data.local.AppDatabase
import com.example.test.data.model.User // Asegúrate que User esté importado
import com.example.test.data.repository.UserRepository
import com.example.test.util.PasswordUtils
import kotlinx.coroutines.flow.* // Importa flatMapLatest, flowOf, stateIn, SharingStarted, etc.
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    // --- Estados de Login ---
    private val _isLoadingLogin = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoadingLogin

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()

    private val _loginSuccessEvent = MutableSharedFlow<Pair<Int, String>>()
    val loginSuccessEvent = _loginSuccessEvent.asSharedFlow()

    // --- Estados de Registro ---
    private val _isLoadingRegistration = MutableStateFlow(false)
    val isLoadingRegistration = _isLoadingRegistration.asStateFlow()

    private val _registrationError = MutableStateFlow<String?>(null)
    val registrationError = _registrationError.asStateFlow()

    private val _registrationSuccessEvent = MutableSharedFlow<Unit>()
    val registrationSuccessEvent = _registrationSuccessEvent.asSharedFlow()

    private val _isDeletingAccount = MutableStateFlow(false)
    val isDeletingAccount = _isDeletingAccount.asStateFlow()

    private val _accountDeletionError = MutableStateFlow<String?>(null)
    val accountDeletionError = _accountDeletionError.asStateFlow()

    private val _accountDeletionSuccessEvent = MutableSharedFlow<Unit>()
    val accountDeletionSuccessEvent = _accountDeletionSuccessEvent.asSharedFlow()

    private val _currentUserId = MutableStateFlow<Int?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentUser: StateFlow<User?> = _currentUserId.flatMapLatest { userId ->
        if (userId != null) {
            Log.d("AuthViewModel", "currentUser Flow: _currentUserId cambió a $userId. Obteniendo usuario del repo.") // Ya lo tenías
            userRepository.getUserById(userId)
        } else {
            Log.d("AuthViewModel", "currentUser Flow: No hay ID de usuario, emitiendo null.") // Ya lo tenías
            flowOf(null)
        }
    }.catch { e ->
        Log.e("AuthViewModel", "Error en el Flow de currentUser", e) // Ya lo tenías
        emit(null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = null
    )

    private val _isUpdatingProfile = MutableStateFlow(false)
    val isUpdatingProfile = _isUpdatingProfile.asStateFlow()

    private val _profileUpdateError = MutableStateFlow<String?>(null)
    val profileUpdateError = _profileUpdateError.asStateFlow()

    private val _profileUpdateSuccessEvent = MutableSharedFlow<String>()
    val profileUpdateSuccessEvent = _profileUpdateSuccessEvent.asSharedFlow()
    // --- FIN ESTADOS PARA PERFIL ---


    fun loginUser(email: String, passwordRaw: String) {
        Log.d("AuthViewModel", "loginUser llamado con email: $email") // <-- LOG AÑADIDO/VERIFICADO
        _isLoadingLogin.value = true
        _loginError.value = null
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            Log.d("AuthViewModel", "loginUser: Resultado de getUserByEmail para '$email': ${if (user != null) "Usuario encontrado (ID: ${user.id})" else "Usuario NO encontrado"}") // <-- LOG AÑADIDO/VERIFICADO

            if (user != null && PasswordUtils.verifyPassword(passwordRaw, user.hashedPassword)) {
                Log.d("AuthViewModel", "Login LOCAL exitoso. Usuario: ${user.name}, ID: ${user.id}, Rol: ${user.role}") // Ya lo tenías, ajustado para ser más claro
                _currentUserId.value = user.id
                Log.d("AuthViewModel", "_currentUserId establecido a: ${_currentUserId.value} tras login local.") // <-- LOG AÑADIDO/VERIFICADO
                _loginSuccessEvent.emit(Pair(user.id, user.role))
            } else {
                val reason = if (user == null) "usuario no encontrado" else "contraseña incorrecta"
                Log.d("AuthViewModel", "Login LOCAL fallido para $email. Razón: $reason") // Ya lo tenías, ajustado
                _loginError.value = "Correo o contraseña incorrectos."
            }
            _isLoadingLogin.value = false
        }
    }

    fun registerUser(name: String, email: String, passwordRaw: String, role: String) {
        // ... (tu función registerUser con sus logs, que ya se ven bien)
        _isLoadingRegistration.value = true
        _registrationError.value = null
        viewModelScope.launch {
            if (name.isBlank() || email.isBlank() || passwordRaw.isBlank() || role.isBlank()) {
                _registrationError.value = "Todos los campos son obligatorios."
                _isLoadingRegistration.value = false
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _registrationError.value = "Formato de correo inválido."
                _isLoadingRegistration.value = false
                return@launch
            }
            if (passwordRaw.length < 6) {
                _registrationError.value = "La contraseña debe tener al menos 6 caracteres."
                _isLoadingRegistration.value = false
                return@launch
            }
            val existingUser = userRepository.getUserByEmail(email)
            if (existingUser != null) {
                _registrationError.value = "Este correo electrónico ya está registrado."
                _isLoadingRegistration.value = false
                return@launch
            }
            val hashedPassword = PasswordUtils.hashPassword(passwordRaw)
            val newUser = User(name = name, email = email, hashedPassword = hashedPassword, role = role)
            val resultId = userRepository.insertUser(newUser)
            if (resultId > -1) {
                Log.d("AuthViewModel", "Usuario registrado con ID: $resultId, Email: $email") // Ya lo tenías
                _registrationSuccessEvent.emit(Unit)
            } else {
                Log.d("AuthViewModel", "Error al registrar usuario con email: $email") // Ya lo tenías
                _registrationError.value = "Error al registrar el usuario. Inténtalo de nuevo."
            }
            _isLoadingRegistration.value = false
        }
    }

    // --- FUNCIONES PARA PERFIL ---
    fun updateUserName(newName: String) {
        // ... (tu función updateUserName con sus logs, que ya se ven bien)
        val userToUpdate = currentUser.value
        if (userToUpdate == null) {
            _profileUpdateError.value = "No hay un usuario activo para actualizar."
            Log.w("AuthViewModel", "Intento de actualizar nombre sin usuario actual.") // Ya lo tenías
            return
        }
        if (newName.isBlank()) {
            _profileUpdateError.value = "El nuevo nombre no puede estar vacío."
            return
        }
        _isUpdatingProfile.value = true
        _profileUpdateError.value = null
        viewModelScope.launch {
            try {
                val updatedUserInstance = userToUpdate.copy(name = newName)
                userRepository.updateUser(updatedUserInstance)
                Log.d("AuthViewModel", "Nombre de usuario actualizado a: $newName para ID: ${userToUpdate.id}") // Ya lo tenías
                _profileUpdateSuccessEvent.emit("Nombre actualizado correctamente a '$newName'")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al actualizar nombre de usuario", e) // Ya lo tenías
                _profileUpdateError.value = "Ocurrió un error al actualizar el nombre."
            }
            _isUpdatingProfile.value = false
        }
    }

    fun logoutUser() { // Si tenías esta función, asegúrate que _currentUserId se ponga a null
        Log.d("AuthViewModel", "logoutUser INVOCADO. Estableciendo _currentUserId a null.")
        _currentUserId.value = null
    }

    fun deleteCurrentUserAccount() {
        val userIdToDelete = _currentUserId.value // Usamos el ID del usuario actualmente logueado
        if (userIdToDelete == null) {
            _accountDeletionError.value = "No hay un usuario activo para eliminar."
            Log.w("AuthViewModel", "Intento de eliminar cuenta sin usuario actual.")
            return
        }

        _isDeletingAccount.value = true
        _accountDeletionError.value = null
        viewModelScope.launch {
            // kotlinx.coroutines.delay(1000) // Simulación opcional
            try {
                val rowsAffected = userRepository.deleteUserById(userIdToDelete)
                if (rowsAffected > 0) {
                    Log.d("AuthViewModel", "Usuario ID: $userIdToDelete eliminado correctamente de Room.")
                    _currentUserId.value = null // Importante: Desloguea al usuario en el ViewModel
                    _accountDeletionSuccessEvent.emit(Unit)
                } else {
                    Log.w("AuthViewModel", "No se eliminó ningún usuario de Room con ID: $userIdToDelete (quizás ya no existía).")
                    _accountDeletionError.value = "No se pudo eliminar la cuenta o ya no existía."
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al eliminar cuenta de Room", e)
                _accountDeletionError.value = "Ocurrió un error al eliminar la cuenta."
            }
            _isDeletingAccount.value = false
        }
    }

    fun setCurrentUserById(userId: Int?) {
        Log.d("AuthViewModel", "setCurrentUserById llamado con ID: $userId") // Ya lo tenías, ajustado
        _currentUserId.value = userId
        if (userId == null) {
            Log.d("AuthViewModel", "Usuario deslogueado (_currentUserId es null vía setCurrentUserById).") // Ya lo tenías
        }
    }

    // --- FUNCIÓN PARA MANEJAR LOGIN GOOGLE (RECUERDA LLAMARLA DESDE InitialScreen) ---
    fun handleGoogleSignInSuccess(firebaseEmail: String?, firebaseDisplayName: String?, callbackForUI: (userId: Int?, userRole: String?) -> Unit) {
        Log.d("AuthViewModel", "handleGoogleSignInSuccess llamado con email: $firebaseEmail") // <-- LOG
        _isLoadingLogin.value = true // Reutilizamos el estado de carga del login
        viewModelScope.launch {
            if (firebaseEmail == null) {
                Log.w("AuthViewModel", "GoogleSignIn: Email de Firebase es null.") // <-- LOG
                _loginError.value = "No se pudo obtener el email de Google."
                _isLoadingLogin.value = false
                callbackForUI(null, null) // Informa a la UI
                return@launch
            }

            val userFromDb = userRepository.getUserByEmail(firebaseEmail)
            if (userFromDb != null) {
                _currentUserId.value = userFromDb.id
                Log.d("AuthViewModel", "GoogleSignIn: Usuario encontrado en Room. _currentUserId: ${_currentUserId.value}, Nombre: ${userFromDb.name}, Rol: ${userFromDb.role}") // <-- LOG
                _isLoadingLogin.value = false
                callbackForUI(userFromDb.id, userFromDb.role) // Pasa ID y Rol a la UI
            } else {
                Log.w("AuthViewModel", "GoogleSignIn: Usuario con email $firebaseEmail NO encontrado en Room.") // <-- LOG
                // Estrategia actual: si no existe en Room, es un error para el login en *nuestro sistema*.
                // Podrías decidir crear un usuario aquí si esa es la lógica deseada.
                _loginError.value = "Usuario de Google no está registrado en nuestra base de datos."
                _isLoadingLogin.value = false
                callbackForUI(null, null)
            }
        }
    }
    // --- FIN FUNCIONES PARA PERFIL ---
}
// La AuthViewModelFactory sigue igual, no necesita cambios.
class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val userDao = AppDatabase.getDatabase(context.applicationContext).userDao()
            val repository = UserRepository(userDao)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}