package com.tecsup.rickandmorty.data.mapper

import com.tecsup.rickandmorty.data.local.ItemEntity
import com.tecsup.rickandmorty.data.remote.dto.ItemDto
import com.tecsup.rickandmorty.domain.model.Item

fun ItemDto.toEntity(favorito: Boolean = false): ItemEntity {
    return ItemEntity(
        id = id,
        titulo = name,
        subtitulo = "$species - $status",
        imagenUrl = image,
        esFavorito = favorito,
        gender = gender ?: "",
        originName = origin?.name ?: "",
        locationName = location?.name ?: ""
    )
}

fun ItemDto.aModelo(favorito: Boolean = false): Item {
    return Item(
        id = id,
        titulo = name,
        subtitulo = "$species - $status",
        imagenUrl = image,
        esFavorito = favorito,
        gender = gender ?: "",
        originName = origin?.name ?: "",
        locationName = location?.name ?: ""
    )
}

fun ItemEntity.toDomain(): Item {
    return Item(
        id = id,
        titulo = titulo,
        subtitulo = subtitulo,
        imagenUrl = imagenUrl,
        esFavorito = esFavorito,
        gender = gender,
        originName = originName,
        locationName = locationName
    )
}

fun ItemDto.toDomain(favorito: Boolean = false): Item {
    return aModelo(favorito)
}

fun Item.toEntity(): ItemEntity {
    return ItemEntity(
        id = id,
        titulo = titulo,
        subtitulo = subtitulo,
        imagenUrl = imagenUrl,
        esFavorito = esFavorito,
        gender = gender,
        originName = originName,
        locationName = locationName
    )
}
