package com.galindo.rickandmorty.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galindo.rickandmorty.data.auth.AuthRepository
import com.galindo.rickandmorty.data.repository.ItemRepository
import com.galindo.rickandmorty.domain.model.Item
import com.galindo.rickandmorty.domain.model.Rol
import com.galindo.rickandmorty.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleViewModel(
    val id: Int,
    private val itemRepository: ItemRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Item>>(UiState.Cargando)
    val uiState: StateFlow<UiState<Item>> = _uiState.asStateFlow()

    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin.asStateFlow()

    init {
        verificarRol()
        cargarDetalle()
    }

    private fun verificarRol() {
        viewModelScope.launch {
            val sesion = authRepository.sesionActual()
            _esAdmin.value = sesion?.rol == Rol.ADMIN
        }
    }

    fun cargarDetalle() {
        viewModelScope.launch {
            _uiState.value = UiState.Cargando
            val result = itemRepository.obtenerDetalle(id)
            result.onSuccess { item ->
                _uiState.value = UiState.Exito(item)
            }.onFailure { e ->
                _uiState.value = UiState.Error(e.localizedMessage ?: "Error al obtener detalle")
            }
        }
    }

    fun alternarFavorito() {
        viewModelScope.launch {
            itemRepository.alternarFavorito(id)
            cargarDetalle()
        }
    }
}
