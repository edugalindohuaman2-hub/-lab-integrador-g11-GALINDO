package com.galindo.rickandmorty.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galindo.rickandmorty.data.auth.AuthRepository
import com.galindo.rickandmorty.domain.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _sesion = MutableStateFlow<Sesion?>(null)
    val sesion: StateFlow<Sesion?> = _sesion.asStateFlow()

    fun iniciarSesion(correo: String, clave: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            val result = authRepository.iniciarSesion(correo.trim(), clave)
            _isLoading.value = false
            result.onSuccess { sesionObj ->
                _sesion.value = sesionObj
                _successMessage.value = "¡Bienvenido al portal!"
                onSuccess()
            }.onFailure { e ->
                _errorMessage.value = e.localizedMessage ?: "Error al iniciar sesión"
            }
        }
    }

    fun registrarUsuario(correo: String, clave: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            val result = authRepository.registrarUsuario(correo.trim(), clave)
            _isLoading.value = false
            result.onSuccess {
                _successMessage.value = "¡Cuenta creada con éxito! Rol: USUARIO"
                onSuccess()
            }.onFailure { e ->
                _errorMessage.value = e.localizedMessage ?: "Error al registrar cuenta"
            }
        }
    }

    fun enviarRecuperacionPassword(correo: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            val result = authRepository.enviarRecuperacionPassword(correo.trim())
            _isLoading.value = false
            result.onSuccess {
                _successMessage.value = "Código de verificación de 5 dígitos generado. Revisa tu correo."
                onSuccess()
            }.onFailure { e ->
                _errorMessage.value = e.localizedMessage ?: "Error al generar código de recuperación"
            }
        }
    }

    fun verificarYActualizarPassword(correo: String, codigo: String, nuevaContrasena: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            val result = authRepository.verificarYActualizarPassword(correo.trim(), codigo.trim(), nuevaContrasena)
            _isLoading.value = false
            result.onSuccess {
                _successMessage.value = "¡Contraseña actualizada con éxito! Ya puedes iniciar sesión."
                onSuccess()
            }.onFailure { e ->
                _errorMessage.value = e.localizedMessage ?: "Código incorrecto, expirado o correo no registrado."
            }
        }
    }

    fun limpiarMensajes() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
