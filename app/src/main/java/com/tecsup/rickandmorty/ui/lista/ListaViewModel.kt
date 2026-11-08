package com.tecsup.rickandmorty.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.rickandmorty.data.repository.ItemRepository
import com.tecsup.rickandmorty.domain.model.Item
import com.tecsup.rickandmorty.notification.Notificador
import com.tecsup.rickandmorty.ui.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListaViewModel(
    private val itemRepository: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    val uiState: StateFlow<UiState<List<Item>>> = itemRepository.items
        .map<List<Item>, UiState<List<Item>>> { items ->
            UiState.Exito(items)
        }
        .catch { e ->
            emit(UiState.Error(e.localizedMessage ?: "Error al cargar personajes"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Cargando
        )

    init {
        refrescar()
    }

    fun refrescar() {
        viewModelScope.launch {
            val result = itemRepository.refrescar()
            result.onSuccess { count ->
                notificador.mostrar("Rick & Morty", "Se sincronizaron $count personajes correctamente.")
            }.onFailure {
                // Ignore or handle
            }
        }
    }

    fun alternarFavorito(id: Int) {
        viewModelScope.launch {
            itemRepository.alternarFavorito(id)
        }
    }
}
