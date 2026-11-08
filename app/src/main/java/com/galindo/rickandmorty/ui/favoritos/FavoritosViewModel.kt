package com.galindo.rickandmorty.ui.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galindo.rickandmorty.data.auth.AuthRepository
import com.galindo.rickandmorty.data.repository.ItemRepository
import com.galindo.rickandmorty.domain.model.Item
import com.galindo.rickandmorty.domain.model.Rol
import com.galindo.rickandmorty.ui.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritosViewModel(
    private val itemRepository: ItemRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin.asStateFlow()

    init {
        verificarRol()
    }

    private fun verificarRol() {
        viewModelScope.launch {
            val sesion = authRepository.sesionActual()
            _esAdmin.value = sesion?.rol == Rol.ADMIN
        }
    }

    val estado: StateFlow<UiState<List<Item>>> = itemRepository.favoritos
        .map<List<Item>, UiState<List<Item>>> { favoritos ->
            UiState.Exito(favoritos)
        }
        .catch { e ->
            emit(UiState.Error(e.localizedMessage ?: "Error al cargar favoritos"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Cargando
        )

    val uiState: StateFlow<UiState<List<Item>>> = estado

    fun alternarFavorito(id: Int) {
        viewModelScope.launch {
            itemRepository.alternarFavorito(id)
        }
    }
}
