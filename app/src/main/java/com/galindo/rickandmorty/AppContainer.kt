package com.galindo.rickandmorty

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.galindo.rickandmorty.data.auth.AuthRepository
import com.galindo.rickandmorty.data.auth.FirebaseAuthRepository
import com.galindo.rickandmorty.data.local.AppDatabase
import com.galindo.rickandmorty.data.local.ItemDao
import com.galindo.rickandmorty.data.remote.ApiService
import com.galindo.rickandmorty.data.repository.ItemRepository
import com.galindo.rickandmorty.data.repository.ItemRepositoryImpl
import com.galindo.rickandmorty.notification.Notificador
import com.galindo.rickandmorty.notification.NotificadorAndroid
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class AppContainer(private val context: Context) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }.let { interceptor ->
            OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
        }
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/api/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    val itemDao: ItemDao by lazy {
        database.itemDao()
    }

    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(apiService, itemDao)
    }

    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository()
    }

    val notificador: Notificador by lazy {
        NotificadorAndroid(context)
    }
}
