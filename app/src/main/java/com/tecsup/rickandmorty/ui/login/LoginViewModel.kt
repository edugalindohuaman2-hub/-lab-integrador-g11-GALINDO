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

    fun iniciarSesion(correo: String, clave: String, onExito: (Sesion) -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState.Cargando
            val result = authRepository.iniciarSesion(correo, clave)
            result.onSuccess { sesion ->
                _uiState.value = UiState.Exito(sesion)
                onExito(sesion)
            }.onFailure { exception ->
                _uiState.value = UiState.Error(exception.localizedMessage ?: "Error al iniciar sesión")
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
}
