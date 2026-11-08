package com.tecsup.rickandmorty.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE esFavorito = 1")
    fun getFavoriteItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Int): ItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemEntity)

    @Query("UPDATE items SET esFavorito = :esFavorito WHERE id = :id")
    suspend fun updateFavorite(id: Int, esFavorito: Boolean)

    @Query("SELECT COUNT(*) FROM items")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM items WHERE esFavorito = 1")
    suspend fun getFavoriteCount(): Int

    @Query("DELETE FROM items")
    suspend fun deleteAll()
}
