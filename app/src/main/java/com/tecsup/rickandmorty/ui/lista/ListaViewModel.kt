package com.tecsup.rickandmorty.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.rickandmorty.data.repository.ItemRepository
import com.tecsup.rickandmorty.domain.model.Item
import com.tecsup.rickandmorty.notification.Notificador
import com.tecsup.rickandmorty.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListaViewModel(
    private val itemRepository: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Item>>> = _estado.asStateFlow()
    val uiState: StateFlow<UiState<List<Item>>> = _estado.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    init {
        viewModelScope.launch {
            itemRepository.items.collect { items ->
                if (items.isNotEmpty()) {
                    _estado.value = UiState.Exito(items)
                }
            }
        }
        refrescar()
    }

    fun refrescar() {
        viewModelScope.launch {
            if (_estado.value !is UiState.Exito || (_estado.value as UiState.Exito).datos.isEmpty()) {
                _estado.value = UiState.Cargando
            }
            _aviso.value = null
            val result = itemRepository.refrescar()
            result.onSuccess { count ->
                if (count > 0) {
                    notificador.mostrar("Datos actualizados", "Se guardaron $count elementos")
                }
            }.onFailure { _ ->
                val guardadas = itemRepository.cantidadGuardada()
                if (guardadas == 0) {
                    _estado.value = UiState.Error("No hay conexión a internet")
                } else {
                    _aviso.value = "Sin conexión: mostrando datos guardados"
                }
            }
        }
    }

    fun alternarFavorito(id: Int) {
        viewModelScope.launch {
            itemRepository.alternarFavorito(id)
        }
    }
}
