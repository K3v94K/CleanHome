package com.example.proyectopdm2026_gt01_grupo01_limpieza

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopdm2026_gt01_grupo01_limpieza.adapters.ServiciosCatalogoAdapter
import com.example.proyectopdm2026_gt01_grupo01_limpieza.api.ApiClient
import com.example.proyectopdm2026_gt01_grupo01_limpieza.data.CleanHomeDbHelper
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.ServiciosResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.session.SessionManager
import com.example.proyectopdm2026_gt01_grupo01_limpieza.sync.SolicitudesSyncManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatalogoActivity : AppCompatActivity() {
    private lateinit var dbHelper: CleanHomeDbHelper
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: ServiciosCatalogoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogo)

        dbHelper = CleanHomeDbHelper(this)
        sessionManager = SessionManager(this)

        findViewById<TextView>(R.id.tv_catalogo_nombre_usuario).text =
            sessionManager.getUserName().ifBlank { "Usuario" }

        val rvServicios = findViewById<RecyclerView>(R.id.rv_catalogo_servicios)
        rvServicios.layoutManager = LinearLayoutManager(this)
        adapter = ServiciosCatalogoAdapter(dbHelper.getServicios()) { servicio ->
            val intent = Intent(this, AgendarActivity::class.java)
            intent.putExtra(AgendarActivity.EXTRA_ID_SERVICIO, servicio.id_servicio)
            startActivity(intent)
        }
        rvServicios.adapter = adapter

        findViewById<Button>(R.id.btn_catalogo_recargar).setOnClickListener {
            syncPendientesAndLoadServicios(showSuccess = true)
        }

        syncPendientesAndLoadServicios()

        findViewById<TextView>(R.id.nav_historial).setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }
        findViewById<TextView>(R.id.nav_perfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }

    private fun loadServicios() {
        ApiClient.service.getServicios().enqueue(object : Callback<ServiciosResponse> {
            override fun onResponse(call: Call<ServiciosResponse>, response: Response<ServiciosResponse>) {
                val servicios = response.body()?.servicios.orEmpty()
                if (response.isSuccessful && servicios.isNotEmpty()) {
                    dbHelper.saveServicios(servicios)
                    adapter.actualizarDatos(servicios)
                } else if (adapter.itemCount == 0) {
                    Toast.makeText(this@CatalogoActivity, "No hay servicios disponibles.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ServiciosResponse>, t: Throwable) {
                val locales = dbHelper.getServicios()
                adapter.actualizarDatos(locales)
                Toast.makeText(this@CatalogoActivity, "Usando servicios guardados localmente.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun syncPendientesAndLoadServicios(showSuccess: Boolean = false) {
        SolicitudesSyncManager(dbHelper, sessionManager).syncPending { result ->
            runOnUiThread {
                if (result.success && result.syncedCount > 0) {
                    Toast.makeText(
                        this,
                        "Solicitudes sincronizadas: ${result.syncedCount}.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (!result.success) {
                    Toast.makeText(this, "Sincronizacion pendiente: ${result.message}", Toast.LENGTH_SHORT).show()
                } else if (showSuccess) {
                    Toast.makeText(this, "Servicios actualizados.", Toast.LENGTH_SHORT).show()
                }
                loadServicios()
            }
        }
    }
}
