package com.example.proyectopdm2026_gt01_grupo01_limpieza

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopdm2026_gt01_grupo01_limpieza.adapters.AdminServiciosAdapter
import com.example.proyectopdm2026_gt01_grupo01_limpieza.adapters.AdminSolicitudesAdapter
import com.example.proyectopdm2026_gt01_grupo01_limpieza.api.ApiClient
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.MessageResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.Servicio
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.ServicioRequest
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.ServiciosResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.Solicitud
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.SolicitudesResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.UpdateEstadoRequest
import com.example.proyectopdm2026_gt01_grupo01_limpieza.session.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var rvLista: RecyclerView
    private lateinit var btnTabSolicitudes: Button
    private lateinit var btnTabServicios: Button
    private lateinit var tvTituloLista: TextView
    private lateinit var fabAddServicio: FloatingActionButton
    private var currentTab = TAB_SOLICITUDES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        sessionManager = SessionManager(this)
        findViewById<TextView>(R.id.tv_admin_back).setOnClickListener { handleBackNavigation() }

        btnTabSolicitudes = findViewById(R.id.btn_tab_solicitudes)
        btnTabServicios = findViewById(R.id.btn_tab_servicios)
        tvTituloLista = findViewById(R.id.tv_admin_titulo_lista)
        rvLista = findViewById(R.id.rv_admin_lista)
        fabAddServicio = findViewById(R.id.fab_admin_add_servicio)

        rvLista.layoutManager = LinearLayoutManager(this)
        btnTabSolicitudes.setOnClickListener { showSolicitudes() }
        btnTabServicios.setOnClickListener { showServicios() }
        fabAddServicio.setOnClickListener { showServicioDialog(null) }

        showSolicitudes()
    }

    @Deprecated("Deprecated in Android SDK, kept for compatibility with this project setup.")
    override fun onBackPressed() {
        handleBackNavigation()
    }

    private fun handleBackNavigation() {
        if (currentTab == TAB_SERVICES) {
            showSolicitudes()
        } else {
            finish()
        }
    }

    private fun showSolicitudes() {
        selectSolicitudesTab()
        val authHeader = getAuthHeaderOrWarn() ?: return

        ApiClient.service.getAdminSolicitudes(authHeader).enqueue(object : Callback<SolicitudesResponse> {
            override fun onResponse(call: Call<SolicitudesResponse>, response: Response<SolicitudesResponse>) {
                if (currentTab != TAB_SOLICITUDES) return
                if (response.isSuccessful) {
                    rvLista.adapter = AdminSolicitudesAdapter(response.body()?.solicitudes.orEmpty()) {
                        showEstadoDialog(it)
                    }
                } else {
                    Toast.makeText(this@AdminActivity, "No tienes permisos de administrador.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SolicitudesResponse>, t: Throwable) {
                Toast.makeText(this@AdminActivity, "No se pudo conectar con el backend local.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showServicios() {
        selectServiciosTab()
        val authHeader = getAuthHeaderOrWarn() ?: return

        ApiClient.service.getAdminServicios(authHeader).enqueue(object : Callback<ServiciosResponse> {
            override fun onResponse(call: Call<ServiciosResponse>, response: Response<ServiciosResponse>) {
                if (currentTab != TAB_SERVICES) return
                if (response.isSuccessful) {
                    rvLista.adapter = AdminServiciosAdapter(
                        response.body()?.servicios.orEmpty(),
                        onEditarClick = { showServicioDialog(it) },
                        onEliminarClick = { confirmDeleteServicio(it) }
                    )
                } else {
                    Toast.makeText(this@AdminActivity, "No tienes permisos de administrador.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ServiciosResponse>, t: Throwable) {
                Toast.makeText(this@AdminActivity, "No se pudo conectar con el backend local.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEstadoDialog(solicitud: Solicitud) {
        val estados = arrayOf("Pendiente", "Confirmada", "En proceso", "Completada", "Cancelada")
        AlertDialog.Builder(this)
            .setTitle("Cambiar estado")
            .setItems(estados) { _, which ->
                updateEstado(solicitud.id_solicitud, estados[which])
            }
            .show()
    }

    private fun updateEstado(idSolicitud: Int, estado: String) {
        val authHeader = getAuthHeaderOrWarn() ?: return
        ApiClient.service.updateSolicitudEstado(authHeader, idSolicitud, UpdateEstadoRequest(estado))
            .enqueue(simpleCallback("Estado actualizado.") { showSolicitudes() })
    }

    private fun showServicioDialog(servicio: Servicio?) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 12, 48, 0)
        }
        val etNombre = dialogEditText("Nombre", InputType.TYPE_CLASS_TEXT).apply {
            setText(servicio?.nombre.orEmpty())
        }
        val etDescripcion = dialogEditText("Descripción", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE).apply {
            setText(servicio?.descripcion.orEmpty())
        }
        val etDuracion = dialogEditText("Duración estimada", InputType.TYPE_CLASS_TEXT).apply {
            setText(servicio?.duracion_estimada.orEmpty())
        }
        val etPrecio = dialogEditText("Precio", InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL).apply {
            setText(servicio?.precio?.toString().orEmpty())
        }

        container.addView(etNombre)
        container.addView(etDescripcion)
        container.addView(etDuracion)
        container.addView(etPrecio)

        AlertDialog.Builder(this)
            .setTitle(if (servicio == null) "Nuevo servicio" else "Editar servicio")
            .setView(container)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val precio = etPrecio.text.toString().trim().toDoubleOrNull()
                if (nombre.isBlank() || precio == null) {
                    Toast.makeText(this, "Nombre y precio son obligatorios.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                saveServicio(
                    servicio,
                    ServicioRequest(
                        nombre = nombre,
                        descripcion = etDescripcion.text.toString().trim().ifBlank { null },
                        duracion_estimada = etDuracion.text.toString().trim().ifBlank { null },
                        precio = precio,
                        activo = true
                    )
                )
            }
            .show()
    }

    private fun saveServicio(servicio: Servicio?, request: ServicioRequest) {
        val authHeader = getAuthHeaderOrWarn() ?: return
        val call = if (servicio == null) {
            ApiClient.service.createServicio(authHeader, request)
        } else {
            ApiClient.service.updateServicio(authHeader, servicio.id_servicio, request)
        }
        call.enqueue(simpleCallback("Servicio guardado.") { showServicios() })
    }

    private fun confirmDeleteServicio(servicio: Servicio) {
        AlertDialog.Builder(this)
            .setTitle("Desactivar servicio")
            .setMessage("¿Deseas desactivar ${servicio.nombre}?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Desactivar") { _, _ ->
                val authHeader = getAuthHeaderOrWarn() ?: return@setPositiveButton
                ApiClient.service.deleteServicio(authHeader, servicio.id_servicio)
                    .enqueue(simpleCallback("Servicio desactivado.") { showServicios() })
            }
            .show()
    }

    private fun dialogEditText(hint: String, type: Int) = EditText(this).apply {
        this.hint = hint
        inputType = type
        setTextColor(Color.parseColor("#1F2937"))
        setHintTextColor(Color.parseColor("#6B7280"))
    }

    private fun simpleCallback(successMessage: String, onSuccess: () -> Unit): Callback<MessageResponse> {
        return object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AdminActivity, successMessage, Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    Toast.makeText(this@AdminActivity, "No se pudo completar la acción.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Toast.makeText(this@AdminActivity, "No se pudo conectar con el backend local.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAuthHeaderOrWarn(): String? {
        val authHeader = sessionManager.getAuthHeader()
        if (authHeader == null) {
            Toast.makeText(this, "Inicia sesión como administrador.", Toast.LENGTH_SHORT).show()
        }
        return authHeader
    }

    private fun selectSolicitudesTab() {
        currentTab = TAB_SOLICITUDES
        btnTabSolicitudes.setBackgroundColor(Color.parseColor("#0D6EFD"))
        btnTabSolicitudes.setTextColor(Color.WHITE)
        btnTabServicios.setBackgroundColor(Color.parseColor("#E9ECEF"))
        btnTabServicios.setTextColor(Color.parseColor("#374151"))
        tvTituloLista.text = "Gestión de Solicitudes"
        fabAddServicio.visibility = View.GONE
    }

    private fun selectServiciosTab() {
        currentTab = TAB_SERVICES
        btnTabServicios.setBackgroundColor(Color.parseColor("#0D6EFD"))
        btnTabServicios.setTextColor(Color.WHITE)
        btnTabSolicitudes.setBackgroundColor(Color.parseColor("#E9ECEF"))
        btnTabSolicitudes.setTextColor(Color.parseColor("#374151"))
        tvTituloLista.text = "Gestión de Servicios"
        fabAddServicio.visibility = View.VISIBLE
    }

    companion object {
        private const val TAB_SOLICITUDES = "solicitudes"
        private const val TAB_SERVICES = "servicios"
    }
}
