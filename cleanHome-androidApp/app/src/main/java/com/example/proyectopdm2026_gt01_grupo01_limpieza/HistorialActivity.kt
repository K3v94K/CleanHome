package com.example.proyectopdm2026_gt01_grupo01_limpieza

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopdm2026_gt01_grupo01_limpieza.adapters.HistorialSolicitudesAdapter
import com.example.proyectopdm2026_gt01_grupo01_limpieza.api.ApiClient
import com.example.proyectopdm2026_gt01_grupo01_limpieza.data.CleanHomeDbHelper
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.SolicitudesResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistorialActivity : AppCompatActivity() {
    private lateinit var dbHelper: CleanHomeDbHelper
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: HistorialSolicitudesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        dbHelper = CleanHomeDbHelper(this)
        sessionManager = SessionManager(this)

        findViewById<TextView>(R.id.tv_historial_back).setOnClickListener { finish() }

        val rvHistorial = findViewById<RecyclerView>(R.id.rv_historial_lista)
        rvHistorial.layoutManager = LinearLayoutManager(this)
        adapter = HistorialSolicitudesAdapter(dbHelper.getSolicitudesByUsuario(sessionManager.getUserId()))
        rvHistorial.adapter = adapter

        loadSolicitudes()
    }

    private fun loadSolicitudes() {
        val authHeader = sessionManager.getAuthHeader()
        if (authHeader == null) {
            Toast.makeText(this, "Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.service.getMisSolicitudes(authHeader).enqueue(object : Callback<SolicitudesResponse> {
            override fun onResponse(call: Call<SolicitudesResponse>, response: Response<SolicitudesResponse>) {
                val solicitudes = response.body()?.solicitudes.orEmpty()
                if (response.isSuccessful) {
                    dbHelper.saveSolicitudes(solicitudes)
                    adapter.actualizarDatos(dbHelper.getSolicitudesByUsuario(sessionManager.getUserId()))
                } else {
                    Toast.makeText(this@HistorialActivity, "No se pudo actualizar el historial.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SolicitudesResponse>, t: Throwable) {
                adapter.actualizarDatos(dbHelper.getSolicitudesByUsuario(sessionManager.getUserId()))
                Toast.makeText(this@HistorialActivity, "Mostrando historial guardado localmente.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
