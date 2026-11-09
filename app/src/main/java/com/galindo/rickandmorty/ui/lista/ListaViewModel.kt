package com.galindo.rickandmorty.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galindo.rickandmorty.data.auth.AuthRepository
import com.galindo.rickandmorty.data.repository.ItemRepository
import com.galindo.rickandmorty.domain.model.Item
import com.galindo.rickandmorty.domain.model.Rol
import com.galindo.rickandmorty.ui.UiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin.asStateFlow()

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    val uiState: StateFlow<UiState<List<Item>>> = combine(
        itemRepository.items,
        _query
    ) { items, q ->
        val filtered = if (q.isBlank()) {
            items
        } else {
            items.filter { item ->
                item.titulo.contains(q, ignoreCase = true) ||
                item.subtitulo.contains(q, ignoreCase = true) ||
                item.name.contains(q, ignoreCase = true) ||
                item.species.contains(q, ignoreCase = true) ||
                item.status.contains(q, ignoreCase = true)
            }
        }
        UiState.Exito(filtered) as UiState<List<Item>>
    }
    .catch { e ->
        emit(UiState.Error(e.localizedMessage ?: "Error al cargar personajes") as UiState<List<Item>>)
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UiState.Cargando
    )

    init {
        refrescar()
        verificarRol()
    }

    private fun verificarRol() {
        viewModelScope.launch {
            val sesion = authRepository.sesionActual()
            _esAdmin.value = sesion?.rol == Rol.ADMIN
        }
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

    fun eliminarPersonaje(id: Int) {
        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance()
                    .collection("personajes")
                    .document(id.toString())
                    .delete()
                    .await()
                refrescar()
            } catch (_: Exception) {}
        }
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
    }
}
