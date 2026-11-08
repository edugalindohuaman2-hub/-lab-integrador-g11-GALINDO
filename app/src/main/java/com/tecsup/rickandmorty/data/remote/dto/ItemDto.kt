package com.tecsup.rickandmorty.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponseDto(
    val info: InfoDto? = null,
    val results: List<ItemDto> = emptyList()
)

@Serializable
data class InfoDto(
    val count: Int = 0,
    val pages: Int = 0,
    val next: String? = null,
    val prev: String? = null
)

@Serializable
data class ItemDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String? = null,
    val gender: String? = null,
    val origin: OriginDto? = null,
    val location: LocationDto? = null,
    val image: String,
    val episode: List<String>? = null,
    val url: String? = null,
    val created: String? = null
)

@Serializable
data class OriginDto(
    val name: String,
    val url: String
)

@Serializable
data class LocationDto(
    val name: String,
    val url: String
)
