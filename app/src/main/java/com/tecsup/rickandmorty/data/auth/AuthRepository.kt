package com.tecsup.rickandmorty.data.auth

import com.tecsup.rickandmorty.domain.model.Sesion

interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>
    suspend fun sesionActual(): Sesion?
    fun cerrarSesion()
}
