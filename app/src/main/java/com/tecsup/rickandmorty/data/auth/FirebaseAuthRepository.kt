package com.tecsup.rickandmorty.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.rickandmorty.domain.model.Rol
import com.tecsup.rickandmorty.domain.model.Sesion
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository : AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(correo, clave).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val uid = firebaseUser.uid
                val email = firebaseUser.email ?: correo

                val role = try {
                    val doc = firestore.collection("users").document(uid).get().await()
                    val roleStr = doc.getString("rol") ?: doc.getString("role")
                    if (roleStr != null && roleStr.uppercase() == "ADMIN") Rol.ADMIN else Rol.USUARIO
                } catch (e: Exception) {
                    if (email.contains("admin", ignoreCase = true)) Rol.ADMIN else Rol.USUARIO
                }

                Result.success(Sesion(uid = uid, correo = email, rol = role))
            } else {
                Result.failure(Exception("No se pudo obtener el usuario de Firebase"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sesionActual(): Sesion? {
        val currentUser = auth.currentUser ?: return null
        val uid = currentUser.uid
        val email = currentUser.email ?: "usuario@tecsup.edu.pe"
        val role = try {
            val doc = firestore.collection("users").document(uid).get().await()
            val roleStr = doc.getString("rol") ?: doc.getString("role")
            if (roleStr != null && roleStr.uppercase() == "ADMIN") Rol.ADMIN else Rol.USUARIO
        } catch (e: Exception) {
            if (email.contains("admin", ignoreCase = true)) Rol.ADMIN else Rol.USUARIO
        }
        return Sesion(uid = uid, correo = email, rol = role)
    }

    override fun cerrarSesion() {
        auth.signOut()
    }
}
