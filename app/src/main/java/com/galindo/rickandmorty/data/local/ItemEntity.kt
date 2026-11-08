package com.galindo.rickandmorty.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
    val id: Int,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val esFavorito: Boolean = false,
    val gender: String = "",
    val originName: String = "",
    val locationName: String = ""
)
