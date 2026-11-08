package com.galindo.rickandmorty.data.auth

import com.galindo.rickandmorty.domain.model.Sesion

interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>
    suspend fun sesionActual(): Sesion?
    fun cerrarSesion()
    val isLoggedIn: Boolean
}
