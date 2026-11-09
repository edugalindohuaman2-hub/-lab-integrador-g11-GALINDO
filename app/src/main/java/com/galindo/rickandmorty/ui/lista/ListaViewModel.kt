package com.galindo.rickandmorty.ui.lista

import android.content.Context
import androidx.core.content.edit
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
import java.io.IOException

data class AppNotification(
    val id: String = System.currentTimeMillis().toString(),
    val mensaje: String,
    val idPersonaje: Int? = null,
    val leida: Boolean = false
)

class ListaViewModel(
    private val itemRepository: ItemRepository,
    private val authRepository: AuthRepository,
    val context: Context,
    private val db: FirebaseFirestore? = null
) : ViewModel() {

    private val prefs = context.getSharedPreferences("notif_prefs", Context.MODE_PRIVATE)

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

    // Sistema de notificaciones persistidas con SharedPreferences para mantener estado leída/no leída para siempre
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    val unreadCount: StateFlow<Int> = _notifications.map { list -> list.count { !it.leida } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

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
        cargarNotificacionesIniciales()
        refrescar()
        verificarRol()
        escucharCambiosFirestore()
    }

    private fun cargarNotificacionesIniciales() {
        val leidasSet = prefs.getStringSet("leidas_ids", emptySet()) ?: emptySet()
        val listaInicial = listOf(
            AppNotification(id = "notif_1", mensaje = "Sincronización inicial completada", idPersonaje = 1, leida = "notif_1" in leidasSet),
            AppNotification(id = "notif_2", mensaje = "¡Bienvenido al catálogo multiversal!", idPersonaje = 2, leida = "notif_2" in leidasSet)
        )
        _notifications.value = listaInicial
    }

    private fun verificarRol() {
        viewModelScope.launch {
            val sesion = authRepository.sesionActual()
            _esAdmin.value = sesion?.rol == Rol.ADMIN
        }
    }

    private fun escucharCambiosFirestore() {
        val firestore = db ?: try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
        firestore?.collection("notificaciones")
            ?.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            ?.limit(30)
            ?.addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val leidasSet = prefs.getStringSet("leidas_ids", emptySet()) ?: emptySet()
                    val listaFirestore = snapshot.documents.map { doc ->
                        val notifId = doc.id
                        val msg = doc.getString("mensaje") ?: "Actualización en el multiverso"
                        val idPers = doc.getLong("idPersonaje")?.toInt() ?: 1
                        val yaLeida = notifId in leidasSet
                        AppNotification(id = notifId, mensaje = msg, idPersonaje = idPers, leida = yaLeida)
                    }

                    val defaults = listOf(
                        AppNotification(id = "notif_1", mensaje = "Sincronización inicial completada", idPersonaje = 1, leida = "notif_1" in leidasSet),
                        AppNotification(id = "notif_2", mensaje = "¡Bienvenido al catálogo multiversal!", idPersonaje = 2, leida = "notif_2" in leidasSet)
                    )

                    val combined = (listaFirestore + defaults).distinctBy { it.id }
                    _notifications.value = combined
                }
            }
    }

    fun refrescar() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _aviso.value = null
            val result = itemRepository.refrescar()
            result.onSuccess { count ->
                if (count > 0) {
                    val notifId = "sync_${System.currentTimeMillis()}"
                    val leidasSet = prefs.getStringSet("leidas_ids", emptySet()) ?: emptySet()
                    agregarNotificacion(AppNotification(id = notifId, mensaje = "Sincronización exitosa: $count elementos.", idPersonaje = 1, leida = notifId in leidasSet))
                }
            }.onFailure { e ->
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

    fun agregarNotificacion(notif: AppNotification) {
        if (_notifications.value.any { it.id == notif.id }) return
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun marcarNotificacionesComoLeidas() {
        val allIds = _notifications.value.map { it.id }.toSet()
        val leidasSet = (prefs.getStringSet("leidas_ids", emptySet()) ?: emptySet()) + allIds
        prefs.edit { putStringSet("leidas_ids", leidasSet) }

        _notifications.value = _notifications.value.map { it.copy(leida = true) }
    }

    fun marcarComoLeida(id: String) {
        val leidasSet = (prefs.getStringSet("leidas_ids", emptySet()) ?: emptySet()) + id
        prefs.edit { putStringSet("leidas_ids", leidasSet) }

        _notifications.value = _notifications.value.map { if (it.id == id) it.copy(leida = true) else it }
    }
}
