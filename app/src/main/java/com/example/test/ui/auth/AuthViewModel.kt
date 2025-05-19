// En AuthViewModel.kt
package com.example.test.ui.auth // O tu paquete

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.test.data.local.AppDatabase
import com.example.test.data.repository.UserRepository
import com.example.test.util.PasswordUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()

    private val _loginSuccessEvent = MutableSharedFlow<Unit>()
    val loginSuccessEvent = _loginSuccessEvent.asSharedFlow()

    fun loginUser(email: String, passwordRaw: String) {
        _isLoading.value = true
        _loginError.value = null

        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)

            if (user != null && PasswordUtils.verifyPassword(passwordRaw, user.hashedPassword)) {
                _loginSuccessEvent.emit(Unit) // Emite evento de éxito
            } else {
                _loginError.value = "Correo o contraseña incorrectos."
            }
            _isLoading.value = false
        }
    }
}

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