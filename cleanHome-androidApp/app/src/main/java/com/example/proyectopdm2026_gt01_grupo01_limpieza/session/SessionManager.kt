package com.example.proyectopdm2026_gt01_grupo01_limpieza.session

import android.content.Context
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.Usuario

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("cleanhome_session", Context.MODE_PRIVATE)

    fun saveSession(token: String, usuario: Usuario) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, usuario.id_usuario)
            .putString(KEY_NAME, usuario.nombre)
            .putString(KEY_EMAIL, usuario.correo)
            .putString(KEY_PHONE, usuario.telefono)
            .putString(KEY_ADDRESS, usuario.direccion)
            .putInt(KEY_ROLE_ID, usuario.id_rol ?: 0)
            .putString(KEY_ROLE_NAME, usuario.nombre_rol)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getAuthHeader(): String? = getToken()?.let { "Bearer $it" }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, 0)

    fun getUserName(): String = prefs.getString(KEY_NAME, "") ?: ""

    fun getUserEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    fun getUserAddress(): String = prefs.getString(KEY_ADDRESS, "") ?: ""

    fun getRoleName(): String = prefs.getString(KEY_ROLE_NAME, "") ?: ""

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "id_usuario"
        private const val KEY_NAME = "nombre"
        private const val KEY_EMAIL = "correo"
        private const val KEY_PHONE = "telefono"
        private const val KEY_ADDRESS = "direccion"
        private const val KEY_ROLE_ID = "id_rol"
        private const val KEY_ROLE_NAME = "nombre_rol"
    }
}
