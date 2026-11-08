package com.galindo.rickandmorty.data.mapper

import com.galindo.rickandmorty.data.local.ItemEntity
import com.galindo.rickandmorty.data.remote.dto.ItemDto
import com.galindo.rickandmorty.domain.model.Item

fun ItemDto.aEntity(esFavorito: Boolean = false): ItemEntity {
    return ItemEntity(
        id = id,
        titulo = name,
        subtitulo = "$species - $status",
        imagenUrl = image,
        esFavorito = esFavorito,
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

fun ItemEntity.aModelo(): Item {
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

fun ItemEntity.toDomain(): Item = aModelo()
fun ItemDto.toEntity(favorito: Boolean = false): ItemEntity = aEntity(favorito)
fun ItemDto.toDomain(favorito: Boolean = false): Item = aModelo(favorito)

fun Item.aEntity(): ItemEntity {
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

fun Item.toEntity(): ItemEntity = aEntity()
