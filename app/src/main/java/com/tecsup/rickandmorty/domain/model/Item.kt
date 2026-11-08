package com.tecsup.rickandmorty.domain.model

data class Item(
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
