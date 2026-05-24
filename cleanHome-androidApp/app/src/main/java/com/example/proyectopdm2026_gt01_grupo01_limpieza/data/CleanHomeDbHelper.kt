package com.example.proyectopdm2026_gt01_grupo01_limpieza.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.CreateSolicitudRequest
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.Servicio
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.Solicitud
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.Usuario

class CleanHomeDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS usuario_sesion (
                id_usuario INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL,
                correo TEXT NOT NULL,
                telefono TEXT,
                direccion TEXT,
                id_rol INTEGER,
                nombre_rol TEXT,
                token TEXT,
                fecha_creacion TEXT,
                updated_at TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS servicios (
                id_servicio INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                duracion_estimada TEXT,
                precio REAL NOT NULL,
                activo INTEGER NOT NULL DEFAULT 1,
                fecha_creacion TEXT,
                updated_at TEXT,
                local_updated_at TEXT NOT NULL DEFAULT (datetime('now'))
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS solicitudes (
                id_solicitud INTEGER PRIMARY KEY,
                client_temp_id TEXT UNIQUE,
                id_usuario INTEGER NOT NULL,
                id_servicio INTEGER NOT NULL,
                id_personal INTEGER,
                fecha_servicio TEXT NOT NULL,
                hora_servicio TEXT NOT NULL,
                direccion_atencion TEXT NOT NULL,
                estado TEXT NOT NULL DEFAULT 'Pendiente',
                cliente TEXT,
                servicio TEXT,
                servicio_descripcion TEXT,
                personal_nombre TEXT,
                fecha_creacion TEXT,
                updated_at TEXT,
                local_created_at TEXT NOT NULL DEFAULT (datetime('now')),
                local_updated_at TEXT NOT NULL DEFAULT (datetime('now')),
                sync_status TEXT NOT NULL DEFAULT 'synced',
                sync_error TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS solicitudes_pendientes_sync (
                id_local INTEGER PRIMARY KEY AUTOINCREMENT,
                client_temp_id TEXT NOT NULL UNIQUE,
                id_servicio INTEGER NOT NULL,
                fecha_servicio TEXT NOT NULL,
                hora_servicio TEXT NOT NULL,
                direccion_atencion TEXT NOT NULL,
                accion TEXT NOT NULL DEFAULT 'create',
                intentos INTEGER NOT NULL DEFAULT 0,
                ultimo_error TEXT,
                created_at TEXT NOT NULL DEFAULT (datetime('now')),
                updated_at TEXT NOT NULL DEFAULT (datetime('now'))
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS solicitudes_pendientes_sync")
        db.execSQL("DROP TABLE IF EXISTS solicitudes")
        db.execSQL("DROP TABLE IF EXISTS servicios")
        db.execSQL("DROP TABLE IF EXISTS usuario_sesion")
        onCreate(db)
    }

    fun saveUsuario(usuario: Usuario, token: String) {
        writableDatabase.insertWithOnConflict(
            "usuario_sesion",
            null,
            ContentValues().apply {
                put("id_usuario", usuario.id_usuario)
                put("nombre", usuario.nombre)
                put("correo", usuario.correo)
                put("telefono", usuario.telefono)
                put("direccion", usuario.direccion)
                put("id_rol", usuario.id_rol)
                put("nombre_rol", usuario.nombre_rol)
                put("token", token)
            },
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun clearUsuario() {
        writableDatabase.delete("usuario_sesion", null, null)
    }

    fun saveServicios(servicios: List<Servicio>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            servicios.forEach { servicio ->
                db.insertWithOnConflict("servicios", null, servicio.toValues(), SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getServicios(): List<Servicio> {
        val cursor = readableDatabase.query(
            "servicios",
            null,
            "activo = 1",
            null,
            null,
            null,
            "nombre"
        )
        return cursor.use { rows ->
            buildList {
                while (rows.moveToNext()) add(rows.toServicio())
            }
        }
    }

    fun getServicioById(idServicio: Int): Servicio? {
        val cursor = readableDatabase.query(
            "servicios",
            null,
            "id_servicio = ?",
            arrayOf(idServicio.toString()),
            null,
            null,
            null
        )
        return cursor.use { rows ->
            if (rows.moveToFirst()) rows.toServicio() else null
        }
    }

    fun saveSolicitudes(solicitudes: List<Solicitud>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            solicitudes.forEach { solicitud ->
                db.insertWithOnConflict("solicitudes", null, solicitud.toValues(), SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun saveSolicitudLocal(solicitud: Solicitud, syncStatus: String, syncError: String? = null) {
        writableDatabase.insertWithOnConflict(
            "solicitudes",
            null,
            solicitud.toValues(syncStatus, syncError),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getSolicitudesByUsuario(idUsuario: Int): List<Solicitud> {
        val cursor = readableDatabase.query(
            "solicitudes",
            null,
            "id_usuario = ?",
            arrayOf(idUsuario.toString()),
            null,
            null,
            "fecha_creacion DESC, local_created_at DESC"
        )
        return cursor.use { rows ->
            buildList {
                while (rows.moveToNext()) add(rows.toSolicitud())
            }
        }
    }

    fun getSolicitudesPendientesSync(idUsuario: Int): List<CreateSolicitudRequest> {
        val cursor = readableDatabase.query(
            "solicitudes",
            arrayOf("client_temp_id", "id_servicio", "fecha_servicio", "hora_servicio", "direccion_atencion"),
            "id_usuario = ? AND sync_status = ? AND client_temp_id IS NOT NULL",
            arrayOf(idUsuario.toString(), "pending"),
            null,
            null,
            "local_created_at ASC"
        )
        return cursor.use { rows ->
            buildList {
                while (rows.moveToNext()) {
                    add(
                        CreateSolicitudRequest(
                            id_servicio = rows.getInt(rows.column("id_servicio")),
                            fecha_servicio = rows.getString(rows.column("fecha_servicio")),
                            hora_servicio = rows.getString(rows.column("hora_servicio")),
                            direccion_atencion = rows.getString(rows.column("direccion_atencion")),
                            client_temp_id = rows.getString(rows.column("client_temp_id"))
                        )
                    )
                }
            }
        }
    }

    fun markSolicitudSynced(clientTempId: String, idSolicitud: Int) {
        val db = writableDatabase
        db.delete("solicitudes", "id_solicitud = ? AND client_temp_id != ?", arrayOf(idSolicitud.toString(), clientTempId))
        db.update(
            "solicitudes",
            ContentValues().apply {
                put("id_solicitud", idSolicitud)
                put("sync_status", "synced")
                putNull("sync_error")
            },
            "client_temp_id = ?",
            arrayOf(clientTempId)
        )
    }

    fun markSolicitudSyncError(clientTempId: String, error: String?) {
        writableDatabase.update(
            "solicitudes",
            ContentValues().apply {
                put("sync_status", "pending")
                put("sync_error", error)
            },
            "client_temp_id = ?",
            arrayOf(clientTempId)
        )
    }

    fun clearAll() {
        writableDatabase.delete("usuario_sesion", null, null)
        writableDatabase.delete("solicitudes", null, null)
    }

    private fun Servicio.toValues() = ContentValues().apply {
        put("id_servicio", id_servicio)
        put("nombre", nombre)
        put("descripcion", descripcion)
        put("duracion_estimada", duracion_estimada)
        put("precio", precio)
        put("activo", activo)
        put("fecha_creacion", fecha_creacion)
        put("updated_at", updated_at)
    }

    private fun Solicitud.toValues(syncStatus: String = "synced", syncError: String? = null) = ContentValues().apply {
        put("id_solicitud", id_solicitud)
        put("client_temp_id", client_temp_id)
        put("id_usuario", id_usuario)
        put("id_servicio", id_servicio)
        put("id_personal", id_personal)
        put("fecha_servicio", fecha_servicio)
        put("hora_servicio", hora_servicio)
        put("direccion_atencion", direccion_atencion)
        put("estado", estado)
        put("cliente", cliente)
        put("servicio", servicio)
        put("servicio_descripcion", servicio_descripcion)
        put("personal_nombre", personal_nombre)
        put("fecha_creacion", fecha_creacion)
        put("updated_at", updated_at)
        put("sync_status", syncStatus)
        put("sync_error", syncError)
    }

    private fun Cursor.toServicio() = Servicio(
        id_servicio = getInt(column("id_servicio")),
        nombre = getString(column("nombre")),
        descripcion = getNullableString("descripcion"),
        duracion_estimada = getNullableString("duracion_estimada"),
        precio = getDouble(column("precio")),
        activo = getInt(column("activo")),
        fecha_creacion = getNullableString("fecha_creacion"),
        updated_at = getNullableString("updated_at")
    )

    private fun Cursor.toSolicitud() = Solicitud(
        id_solicitud = getInt(column("id_solicitud")),
        client_temp_id = getNullableString("client_temp_id"),
        id_usuario = getInt(column("id_usuario")),
        id_servicio = getInt(column("id_servicio")),
        id_personal = getNullableInt("id_personal"),
        fecha_servicio = getString(column("fecha_servicio")),
        hora_servicio = getString(column("hora_servicio")),
        direccion_atencion = getString(column("direccion_atencion")),
        estado = getString(column("estado")),
        cliente = getNullableString("cliente"),
        servicio = getNullableString("servicio"),
        servicio_descripcion = getNullableString("servicio_descripcion"),
        personal_nombre = getNullableString("personal_nombre"),
        fecha_creacion = getNullableString("fecha_creacion"),
        updated_at = getNullableString("updated_at")
    )

    private fun Cursor.column(name: String) = getColumnIndexOrThrow(name)

    private fun Cursor.getNullableString(name: String): String? {
        val index = column(name)
        return if (isNull(index)) null else getString(index)
    }

    private fun Cursor.getNullableInt(name: String): Int? {
        val index = column(name)
        return if (isNull(index)) null else getInt(index)
    }

    companion object {
        private const val DATABASE_NAME = "cleanhome_android.db"
        private const val DATABASE_VERSION = 1
    }
}
