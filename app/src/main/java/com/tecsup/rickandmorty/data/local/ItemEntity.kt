package com.tecsup.rickandmorty.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val image: String,
    val favorito: Boolean = false,
    val gender: String = "",
    val originName: String = "",
    val locationName: String = ""
)
