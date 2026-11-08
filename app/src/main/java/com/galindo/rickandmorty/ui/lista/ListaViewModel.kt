package com.galindo.rickandmorty.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galindo.rickandmorty.data.auth.AuthRepository
import com.galindo.rickandmorty.data.repository.ItemRepository
import com.galindo.rickandmorty.domain.model.Item
import com.galindo.rickandmorty.ui.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

class ListaViewModel(
    private val itemRepository: ItemRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val uiState: StateFlow<UiState<List<Item>>> = itemRepository.items
        .map<List<Item>, UiState<List<Item>>> { items ->
            UiState.Exito(items)
        }
        .catch { e ->
            emit(UiState.Error(e.localizedMessage ?: "Error al cargar personajes"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UiState.Cargando
        )

    init {
        refrescar()
    }

    fun refrescar() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _aviso.value = null
            val result = itemRepository.refrescar()
            result.onFailure { e ->
                if (e is IOException) {
                    val count = itemRepository.cantidadGuardada()
                    if (count > 0) {
                        _aviso.value = "Sin conexión: mostrando datos guardados"
                    } else {
                        _errorMessage.value = e.localizedMessage
                    }
                } else {
                    _errorMessage.value = e.localizedMessage
                }
            }
            _isRefreshing.value = false
        }
    }

    fun alternarFavorito(id: Int) {
        viewModelScope.launch {
            itemRepository.alternarFavorito(id)
        }
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
    }
}
