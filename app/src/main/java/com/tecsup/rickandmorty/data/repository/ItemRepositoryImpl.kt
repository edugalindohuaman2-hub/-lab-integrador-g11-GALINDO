package com.tecsup.rickandmorty.data.repository

import com.tecsup.rickandmorty.data.local.ItemDao
import com.tecsup.rickandmorty.data.mapper.aEntity
import com.tecsup.rickandmorty.data.mapper.aModelo
import com.tecsup.rickandmorty.data.remote.ApiService
import com.tecsup.rickandmorty.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

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
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
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
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
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
