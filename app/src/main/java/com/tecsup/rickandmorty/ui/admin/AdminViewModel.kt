package com.tecsup.rickandmorty.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.rickandmorty.data.repository.ItemRepository
import com.tecsup.rickandmorty.notification.Notificador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val itemRepository: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _stats = MutableStateFlow(Pair(0, 0))
    val stats: StateFlow<Pair<Int, Int>> = _stats.asStateFlow()

    init {
        cargarEstadisticas()
    }

    fun cargarEstadisticas() {
        viewModelScope.launch {
            val guardados = itemRepository.cantidadGuardada()
            val favoritos = itemRepository.cantidadFavoritos()
            _stats.value = Pair(guardados, favoritos)
        }
    }

    fun sincronizar() {
        viewModelScope.launch {
            val result = itemRepository.refrescar()
            result.onSuccess { count ->
                cargarEstadisticas()
                notificador.mostrar("Administración", "Sincronización exitosa: $count elementos.")
            }.onFailure { e ->
                notificador.mostrar("Administración", "Error al sincronizar: ${e.localizedMessage}")
            }
        }
    }

    fun limpiarCache() {
        viewModelScope.launch {
            itemRepository.limpiarCache()
            cargarEstadisticas()
            notificador.mostrar("Administración", "Caché limpiada correctamente.")
        }
    }
}
