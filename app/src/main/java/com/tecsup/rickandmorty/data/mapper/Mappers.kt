package com.tecsup.rickandmorty.data.mapper

import com.tecsup.rickandmorty.data.local.ItemEntity
import com.tecsup.rickandmorty.data.remote.dto.ItemDto
import com.tecsup.rickandmorty.domain.model.Item

fun ItemDto.toEntity(favorito: Boolean = false): ItemEntity {
    return ItemEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        image = image,
        favorito = favorito,
        gender = gender ?: "",
        originName = origin?.name ?: "",
        locationName = location?.name ?: ""
    )
}

fun ItemEntity.toDomain(): Item {
    return Item(
        id = id,
        name = name,
        status = status,
        species = species,
        image = image,
        favorito = favorito,
        gender = gender,
        originName = originName,
        locationName = locationName
    )
}

fun ItemDto.toDomain(favorito: Boolean = false): Item {
    return Item(
        id = id,
        name = name,
        status = status,
        species = species,
        image = image,
        favorito = favorito,
        gender = gender ?: "",
        originName = origin?.name ?: "",
        locationName = location?.name ?: ""
    )
}

fun Item.toEntity(): ItemEntity {
    return ItemEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        image = image,
        favorito = favorito,
        gender = gender,
        originName = originName,
        locationName = locationName
    )
}
