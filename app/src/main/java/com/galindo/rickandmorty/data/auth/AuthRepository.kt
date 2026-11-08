package com.galindo.rickandmorty.data.auth

import com.galindo.rickandmorty.domain.model.Sesion

interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>
    suspend fun registrarUsuario(correo: String, clave: String): Result<Unit>
    suspend fun enviarRecuperacionPassword(correo: String): Result<Unit>
    suspend fun confirmarRecuperacionPassword(codigo: String, nuevaContrasena: String): Result<Unit>
    suspend fun verificarYActualizarPassword(correo: String, codigo: String, nuevaContrasena: String): Result<Unit>
    suspend fun sesionActual(): Sesion?
    fun cerrarSesion()
    val isLoggedIn: Boolean
}
