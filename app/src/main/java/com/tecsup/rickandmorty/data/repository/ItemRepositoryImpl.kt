package com.tecsup.rickandmorty.data.repository

import com.tecsup.rickandmorty.data.local.ItemDao
import com.tecsup.rickandmorty.data.mapper.toDomain
import com.tecsup.rickandmorty.data.mapper.toEntity
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

    override val items: Flow<List<Item>> = itemDao.todos().map { entities ->
        entities.map { it.toDomain() }
    }

    override val favoritos: Flow<List<Item>> = itemDao.favoritos().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun refrescar(): Result<Int> {
        return try {
            val response = apiService.listar("alive")
            val remoteDtos = response.results
            val remoteEntities = remoteDtos.map { dto ->
                val existing = itemDao.getItemById(dto.id)
                dto.toEntity(favorito = existing?.esFavorito ?: false)
            }
            itemDao.guardarTodos(remoteEntities)
            Result.success(remoteEntities.size)
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
                Result.success(localEntity.toDomain())
            } else {
                val dto = apiService.obtenerDetalle(id)
                val entity = dto.toEntity(false)
                itemDao.insert(entity)
                Result.success(entity.toDomain())
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
