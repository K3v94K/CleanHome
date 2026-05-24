package com.example.proyectopdm2026_gt01_grupo01_limpieza.sync

import com.example.proyectopdm2026_gt01_grupo01_limpieza.api.ApiClient
import com.example.proyectopdm2026_gt01_grupo01_limpieza.data.CleanHomeDbHelper
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.SyncSolicitudesRequest
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.SyncSolicitudesResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SolicitudesSyncManager(
    private val dbHelper: CleanHomeDbHelper,
    private val sessionManager: SessionManager
) {
    fun syncPending(onFinished: (SyncResult) -> Unit) {
        val authHeader = sessionManager.getAuthHeader()
        if (authHeader == null) {
            onFinished(SyncResult(false, 0, "Sesion no iniciada."))
            return
        }

        val pending = dbHelper.getSolicitudesPendientesSync(sessionManager.getUserId())
        if (pending.isEmpty()) {
            onFinished(SyncResult(true, 0, null))
            return
        }

        ApiClient.service.syncSolicitudes(authHeader, SyncSolicitudesRequest(pending))
            .enqueue(object : Callback<SyncSolicitudesResponse> {
                override fun onResponse(
                    call: Call<SyncSolicitudesResponse>,
                    response: Response<SyncSolicitudesResponse>
                ) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        onFinished(SyncResult(false, 0, "No se pudieron sincronizar solicitudes."))
                        return
                    }

                    body.sincronizadas.forEach { result ->
                        dbHelper.markSolicitudSynced(result.client_temp_id, result.id_solicitud)
                    }
                    body.errores.forEach { error ->
                        error.client_temp_id?.let {
                            dbHelper.markSolicitudSyncError(it, error.message)
                        }
                    }

                    val message = if (body.errores.isEmpty()) {
                        null
                    } else {
                        "Algunas solicitudes no pudieron sincronizarse."
                    }
                    onFinished(SyncResult(true, body.sincronizadas.size, message))
                }

                override fun onFailure(call: Call<SyncSolicitudesResponse>, t: Throwable) {
                    onFinished(SyncResult(false, 0, t.message ?: "Sin conexion con el backend."))
                }
            })
    }
}

data class SyncResult(
    val success: Boolean,
    val syncedCount: Int,
    val message: String?
)
