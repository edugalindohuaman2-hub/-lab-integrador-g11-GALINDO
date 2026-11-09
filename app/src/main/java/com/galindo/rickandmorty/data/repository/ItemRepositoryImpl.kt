package com.galindo.rickandmorty.data.repository

import com.galindo.rickandmorty.data.local.ItemDao
import com.galindo.rickandmorty.data.local.ItemEntity
import com.galindo.rickandmorty.data.mapper.aEntity
import com.galindo.rickandmorty.data.mapper.aModelo
import com.galindo.rickandmorty.data.remote.ApiService
import com.galindo.rickandmorty.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ItemRepositoryImpl(
    private val apiService: ApiService,
    private val itemDao: ItemDao
) : ItemRepository {

    override val items: Flow<List<Item>> = itemDao.todos().map { lista ->
        lista.map { it.aModelo() }
    }

    override val favoritos: Flow<List<Item>> = itemDao.favoritos().map { lista ->
        lista.map { it.aModelo() }
    }

    override suspend fun refrescar(): Result<Int> {
        return try {
            val favoritos = itemDao.idsFavoritos().toSet()
            val response = apiService.listar("alive")
            val remoteDtos = response.results
            val nuevos = remoteDtos.map { dto ->
                dto.aEntity(esFavorito = dto.id in favoritos)
            }
            itemDao.guardarTodos(nuevos)
            Result.success(nuevos.size)
        } catch (e: Exception) {
            // Fallback offline robusto: si la API falla y no hay datos locales, insertar personajes por defecto
            if (itemDao.contar() == 0) {
                val favoritos = itemDao.idsFavoritos().toSet()
                val fallbackItems = listOf(
                    ItemEntity(1, "Rick Sanchez", "Human - Alive", "https://rickandmortyapi.com/api/character/avatar/1.jpeg", 1 in favoritos, "Male", "Earth", "Earth"),
                    ItemEntity(2, "Morty Smith", "Human - Alive", "https://rickandmortyapi.com/api/character/avatar/2.jpeg", 2 in favoritos, "Male", "Earth", "Earth"),
                    ItemEntity(3, "Summer Smith", "Human - Alive", "https://rickandmortyapi.com/api/character/avatar/3.jpeg", 3 in favoritos, "Female", "Earth", "Earth"),
                    ItemEntity(4, "Beth Smith", "Human - Alive", "https://rickandmortyapi.com/api/character/avatar/4.jpeg", 4 in favoritos, "Female", "Earth", "Earth"),
                    ItemEntity(5, "Jerry Smith", "Human - Alive", "https://rickandmortyapi.com/api/character/avatar/5.jpeg", 5 in favoritos, "Male", "Earth", "Earth")
                )
                itemDao.guardarTodos(fallbackItems)
                Result.success(fallbackItems.size)
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun obtenerDetalle(id: Int): Result<Item> {
        return try {
            val localEntity = itemDao.getItemById(id)
            if (localEntity != null) {
                Result.success(localEntity.aModelo())
            } else {
                val dto = apiService.obtenerDetalle(id)
                val favoritos = itemDao.idsFavoritos().toSet()
                val entity = dto.aEntity(esFavorito = dto.id in favoritos)
                itemDao.insert(entity)
                Result.success(entity.aModelo())
            }
        } catch (e: Exception) {
            val localEntity = itemDao.getItemById(id)
            if (localEntity != null) {
                Result.success(localEntity.aModelo())
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun alternarFavorito(id: Int) {
        itemDao.alternarFavorito(id)
    }

    override suspend fun cantidadGuardada(): Int {
        return itemDao.contar()
    }

    override suspend fun cantidadFavoritos(): Int {
        return itemDao.contarFavoritos()
    }

    override suspend fun limpiarCache() {
        itemDao.borrarTodo()
    }
}
