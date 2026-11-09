package com.galindo.rickandmorty.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.galindo.rickandmorty.domain.model.Rol
import com.galindo.rickandmorty.domain.model.Sesion
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> = try {
        val authResult = auth.signInWithEmailAndPassword(correo, clave).await()
        val user = authResult.user ?: throw IllegalStateException("Usuario nulo tras autenticación")
        val uid = user.uid
        val email = user.email ?: correo

        val doc = db.collection("usuarios").document(uid).get().await()
        val rolStr = doc.getString("rol") ?: "USUARIO"
        val rol = if (rolStr.equals("admin", ignoreCase = true)) Rol.ADMIN else Rol.USUARIO

        Result.success(Sesion(uid, email, rol))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun registrarUsuario(correo: String, clave: String): Result<Unit> = try {
        val authResult = auth.createUserWithEmailAndPassword(correo, clave).await()
        val uid = authResult.user?.uid ?: throw IllegalStateException("UID nulo tras registro")

        val userMap = mapOf(
            "correo" to correo,
            "rol" to "USUARIO"
        )
        db.collection("usuarios").document(uid).set(userMap).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun enviarRecuperacionPassword(correo: String): Result<Unit> = try {
        val codigo5Digitos = (10000..99999).random().toString()
        val expiracion = System.currentTimeMillis() + (10 * 60 * 1000) // 10 minutos

        val codigoMap = mapOf(
            "correo" to correo.trim().lowercase(),
            "codigo" to codigo5Digitos,
            "expiracion" to expiracion
        )

        db.collection("codigos_recuperacion").document(correo.trim().lowercase()).set(codigoMap).await()

        auth.setLanguageCode("es")
        try {
            auth.sendPasswordResetEmail(correo).await()
        } catch (_: Exception) {}

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun confirmarRecuperacionPassword(codigo: String, nuevaContrasena: String): Result<Unit> = try {
        auth.confirmPasswordReset(codigo, nuevaContrasena).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun verificarYActualizarPassword(correo: String, codigo: String, nuevaContrasena: String): Result<Unit> {
        return try {
            val docRef = db.collection("codigos_recuperacion").document(correo.trim().lowercase())
            val snapshot = docRef.get().await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("Correo no registrado o solicitud de recuperación no encontrada."))
            }

            val codigoGuardado = snapshot.getString("codigo") ?: ""
            val expiracion = snapshot.getLong("expiracion") ?: 0L
            val ahora = System.currentTimeMillis()

            if (ahora > expiracion) {
                docRef.delete().await()
                return Result.failure(Exception("El código de verificación ha expirado. Solicita uno nuevo."))
            }

            if (codigoGuardado != codigo.trim()) {
                return Result.failure(Exception("El código de verificación es incorrecto."))
            }

            auth.setLanguageCode("es")
            try {
                auth.sendPasswordResetEmail(correo).await()
            } catch (_: Exception) {}

            docRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sesionActual(): Sesion? {
        val u = auth.currentUser ?: return null
        val doc = db.collection("usuarios").document(u.uid).get().await()
        val rolStr = doc.getString("rol") ?: "USUARIO"
        val rol = if (rolStr.equals("admin", ignoreCase = true)) Rol.ADMIN else Rol.USUARIO
        return Sesion(u.uid, u.email ?: "", rol)
    }

    override fun cerrarSesion() {
        auth.signOut()
    }

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null
}
