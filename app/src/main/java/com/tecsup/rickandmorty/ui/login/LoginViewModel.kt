package com.tecsup.rickandmorty.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.rickandmorty.data.auth.AuthRepository
import com.tecsup.rickandmorty.domain.model.Sesion
import com.tecsup.rickandmorty.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Sesion?>>(UiState.Exito(null))
    val uiState: StateFlow<UiState<Sesion?>> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun iniciarSesion(correo: String, clave: String, onExito: (Sesion) -> Unit) {
        if (correo.isBlank() || clave.isBlank()) {
            val msg = "El correo y la contraseña no pueden estar vacíos"
            _errorMessage.value = msg
            _uiState.value = UiState.Error(msg)
            return
        }
        _errorMessage.value = null
        viewModelScope.launch {
            _uiState.value = UiState.Cargando
            val result = authRepository.iniciarSesion(correo, clave)
            result.onSuccess { sesion ->
                _uiState.value = UiState.Exito(sesion)
                onExito(sesion)
            }.onFailure { exception ->
                val msg = exception.localizedMessage ?: "Credenciales incorrectas o error de autenticación"
                _uiState.value = UiState.Error(msg)
                _errorMessage.value = msg
            }
        }
    }

    fun verificarSesionActual(onSesion: (Sesion) -> Unit) {
        viewModelScope.launch {
            val sesion = authRepository.sesionActual()
            if (sesion != null) {
                onSesion(sesion)
            }
        }
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
        _uiState.value = UiState.Exito(null)
        _errorMessage.value = null
    }
}
