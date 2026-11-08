package com.tecsup.rickandmorty.data.remote

import com.tecsup.rickandmorty.data.remote.dto.CharacterResponseDto
import com.tecsup.rickandmorty.data.remote.dto.ItemDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int = 1): CharacterResponseDto

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): ItemDto
}
