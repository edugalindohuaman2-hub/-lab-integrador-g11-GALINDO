package com.galindo.rickandmorty.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galindo.rickandmorty.data.auth.AuthRepository
import com.galindo.rickandmorty.data.repository.ItemRepository
import com.galindo.rickandmorty.domain.model.Rol
import com.galindo.rickandmorty.notification.Notificador
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AdminCharacter(
    val id: String = "",
    val nombre: String = "",
    val especie: String = "",
    val genero: String = "",
    val estado: String = "",
    val imagenUrl: String = ""
)

class AdminViewModel(
    private val itemRepository: ItemRepository,
    private val notificador: Notificador,
    private val authRepository: AuthRepository,
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _stats = MutableStateFlow(Pair(0, 0))
    val stats: StateFlow<Pair<Int, Int>> = _stats.asStateFlow()

    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin.asStateFlow()

    private val _isLoadingSession = MutableStateFlow(true)
    val isLoadingSession: StateFlow<Boolean> = _isLoadingSession.asStateFlow()

    private val _personajesAdmin = MutableStateFlow<List<AdminCharacter>>(emptyList())
    val personajesAdmin: StateFlow<List<AdminCharacter>> = _personajesAdmin.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        verificarRol()
        cargarEstadisticas()
        cargarPersonajesFirestore()
    }

    private fun verificarRol() {
        viewModelScope.launch {
            val sesion = authRepository.sesionActual()
            _esAdmin.value = sesion?.rol == Rol.ADMIN
            _isLoadingSession.value = false
        }
    }

    fun cargarEstadisticas() {
        viewModelScope.launch {
            val guardados = itemRepository.cantidadGuardada()
            val favoritos = itemRepository.cantidadFavoritos()
            _stats.value = Pair(guardados, favoritos)
        }
    }

    fun cargarPersonajesFirestore() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("personajes").get().await()
                val lista = snapshot.documents.map { doc ->
                    AdminCharacter(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: doc.getString("titulo") ?: "",
                        especie = doc.getString("especie") ?: "",
                        genero = doc.getString("genero") ?: "",
                        estado = doc.getString("estado") ?: "",
                        imagenUrl = doc.getString("imagenUrl") ?: doc.getString("imagen") ?: ""
                    )
                }
                _personajesAdmin.value = lista
            } catch (e: Exception) {
                notificador.mostrar("Admin", "Error al cargar personajes de Firestore: ${e.localizedMessage}")
            }
        }
    }

    fun cargarPersonajesIniciales(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val personajesIniciales = listOf(
                    mapOf("id" to 1, "nombre" to "Rick Sanchez", "especie" to "Human", "genero" to "Male", "estado" to "Alive", "imagen" to "https://rickandmortyapi.com/api/character/avatar/1.jpeg"),
                    mapOf("id" to 2, "nombre" to "Morty Smith", "especie" to "Human", "genero" to "Male", "estado" to "Alive", "imagen" to "https://rickandmortyapi.com/api/character/avatar/2.jpeg"),
                    mapOf("id" to 3, "nombre" to "Summer Smith", "especie" to "Human", "genero" to "Female", "estado" to "Alive", "imagen" to "https://rickandmortyapi.com/api/character/avatar/3.jpeg"),
                    mapOf("id" to 4, "nombre" to "Beth Smith", "especie" to "Human", "genero" to "Female", "estado" to "Alive", "imagen" to "https://rickandmortyapi.com/api/character/avatar/4.jpeg"),
                    mapOf("id" to 5, "nombre" to "Jerry Smith", "especie" to "Human", "genero" to "Male", "estado" to "Alive", "imagen" to "https://rickandmortyapi.com/api/character/avatar/5.jpeg")
                )
                for (p in personajesIniciales) {
                    db.collection("personajes").document(p["id"].toString()).set(p).await()
                }
                notificador.mostrar("Admin", "¡Base de datos poblada con éxito (5 personajes)!")
                cargarPersonajesFirestore()
                itemRepository.refrescar()
                onSuccess()
            } catch (e: Exception) {
                notificador.mostrar("Admin", "Error al poblar BD: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun guardarPersonaje(id: String?, nombre: String, especie: String, genero: String, estado: String, imagenUrl: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = mapOf(
                    "nombre" to nombre,
                    "especie" to especie,
                    "genero" to genero,
                    "estado" to estado,
                    "imagen" to imagenUrl,
                    "imagenUrl" to imagenUrl
                )
                if (id.isNullOrBlank()) {
                    db.collection("personajes").add(data).await()
                    notificador.mostrar("¡Nuevo Personaje Publicado!", "Se ha añadido a $nombre")
                } else {
                    db.collection("personajes").document(id).set(data).await()
                    notificador.mostrar("¡Personaje Actualizado!", "Se modificaron los datos de $nombre")
                }
                cargarPersonajesFirestore()
                itemRepository.refrescar()
                onComplete()
            } catch (e: Exception) {
                notificador.mostrar("Admin", "Error al guardar: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarPersonaje(id: String) {
        viewModelScope.launch {
            try {
                db.collection("personajes").document(id).delete().await()
                notificador.mostrar("¡Personaje Eliminado!", "El personaje ha sido removido")
                cargarPersonajesFirestore()
                itemRepository.refrescar()
            } catch (e: Exception) {
                notificador.mostrar("Admin", "Error al eliminar: ${e.localizedMessage}")
            }
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
