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

    private val mockItems = listOf(
        Item(1, "Rick Sanchez", "Human - Alive", "https://rickandmortyapi.com/api/character/avatar/1.jpeg", false, "Male", "Earth", "Citadel"),
        Item(2, "Morty Smith", "Human - Alive", "https://rickandmortyapi.com/api/character/avatar/2.jpeg", true, "Male", "Earth", "Citadel"),
        Item(3, "Summer Smith", "Human - Alive", "https://rickandmortyapi.com/api/character/avatar/3.jpeg", false, "Female", "Earth", "Earth"),
        Item(4, "Beth Smith", "Human - Alive", "https://rickandmortyapi.com/api/character/avatar/4.jpeg", false, "Female", "Earth", "Earth"),
        Item(5, "Jerry Smith", "Human - Alive", "https://rickandmortyapi.com/api/character/avatar/5.jpeg", false, "Male", "Earth", "Earth")
    )

    val estado: StateFlow<UiState<List<Item>>> = itemRepository.items
        .map<List<Item>, UiState<List<Item>>> { items ->
            if (items.isEmpty()) UiState.Exito(mockItems) else UiState.Exito(items)
        }
        .catch {
            emit(UiState.Exito(mockItems))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Exito(mockItems)
        )

    val uiState: StateFlow<UiState<List<Item>>> = estado

    init {
        refrescar()
    }

    fun refrescar() {
        viewModelScope.launch {
            val result = itemRepository.refrescar()
            result.onSuccess { count ->
                if (count > 0) {
                    notificador.mostrar("Rick & Morty", "Se sincronizaron $count personajes correctamente.")
                }
            }.onFailure {
                // Keep mock items on failure
            }
        }
    }

    fun alternarFavorito(id: Int) {
        viewModelScope.launch {
            itemRepository.alternarFavorito(id)
        }
    }
}
