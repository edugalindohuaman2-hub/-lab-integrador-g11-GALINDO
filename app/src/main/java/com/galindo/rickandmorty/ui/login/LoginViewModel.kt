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

    private val _sesion = MutableStateFlow<Sesion?>(null)
    val sesion: StateFlow<Sesion?> = _sesion.asStateFlow()

    fun iniciarSesion(correo: String, clave: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.iniciarSesion(correo, clave)
            _isLoading.value = false
            result.onSuccess { sesion ->
                _sesion.value = sesion
                onSuccess()
            }.onFailure { e ->
                _errorMessage.value = e.localizedMessage ?: "Error al iniciar sesión"
            }
        }
    }
}
