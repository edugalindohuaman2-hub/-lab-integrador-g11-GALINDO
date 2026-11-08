package com.tecsup.rickandmorty.domain.model

data class Item(
    val id: Int,
    val titulo: String,       // Nombre del personaje
    val subtitulo: String,    // Especie y estado (ej. "Human - Alive")
    val imagenUrl: String?,   // Avatar del personaje
    val esFavorito: Boolean = false,
    val gender: String = "",
    val originName: String = "",
    val locationName: String = ""
) {
    val name: String get() = titulo
    val species: String get() = subtitulo.split(" - ").firstOrNull() ?: ""
    val status: String get() = subtitulo.split(" - ").getOrNull(1) ?: ""
    val image: String get() = imagenUrl ?: ""
    val favorito: Boolean get() = esFavorito
}
