package com.example.proyectopdm2026_gt01_grupo01_limpieza

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectopdm2026_gt01_grupo01_limpieza.api.ApiClient
import com.example.proyectopdm2026_gt01_grupo01_limpieza.data.CleanHomeDbHelper
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.CreateSolicitudRequest
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.CreateSolicitudResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.Solicitud
import com.example.proyectopdm2026_gt01_grupo01_limpieza.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AgendarActivity : AppCompatActivity() {
    private lateinit var dbHelper: CleanHomeDbHelper
    private lateinit var sessionManager: SessionManager
    private var idServicio: Int = 0
    private var horaServicioApi: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar)

        dbHelper = CleanHomeDbHelper(this)
        sessionManager = SessionManager(this)
        idServicio = intent.getIntExtra(EXTRA_ID_SERVICIO, 0)

        findViewById<TextView>(R.id.tv_agendar_back).setOnClickListener { finish() }

        val etFecha = findViewById<EditText>(R.id.et_agendar_fecha)
        val etHora = findViewById<EditText>(R.id.et_agendar_hora)
        val etDireccion = findViewById<EditText>(R.id.et_agendar_direccion)
        val tvTotal = findViewById<TextView>(R.id.tv_agendar_total)
        val tvServicioNombre = findViewById<TextView>(R.id.tv_agendar_servicio_nombre)
        val tvServicioDetalle = findViewById<TextView>(R.id.tv_agendar_servicio_detalle)
        val btnConfirmar = findViewById<Button>(R.id.btn_agendar_confirmar)

        val servicio = dbHelper.getServicioById(idServicio)
        tvServicioNombre.text = servicio?.nombre ?: "Servicio seleccionado"
        tvServicioDetalle.text = servicio?.descripcion ?: "Detalle del servicio"
        tvTotal.text = "$${String.format("%.2f", servicio?.precio ?: 0.0)}"
        etDireccion.setText(sessionManager.getUserAddress())

        etFecha.isFocusable = false
        etFecha.isClickable = true
        etHora.isFocusable = false
        etHora.isClickable = true
        etFecha.setOnClickListener { showDatePicker(etFecha) }
        etHora.setOnClickListener { showTimePicker(etHora) }

        btnConfirmar.setOnClickListener {
            val fecha = etFecha.text.toString().trim()
            val hora = horaServicioApi
            val direccion = etDireccion.text.toString().trim()

            if (idServicio == 0 || fecha.isBlank() || hora.isBlank() || direccion.isBlank()) {
                Toast.makeText(this, "Completa fecha, hora y dirección.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (direccion.length < MIN_ADDRESS_LENGTH) {
                etDireccion.error = "Minimo $MIN_ADDRESS_LENGTH caracteres."
                etDireccion.requestFocus()
                return@setOnClickListener
            }

            val authHeader = sessionManager.getAuthHeader()
            if (authHeader == null) {
                Toast.makeText(this, "Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return@setOnClickListener
            }

            btnConfirmar.isEnabled = false
            btnConfirmar.text = "Enviando..."
            val clientTempId = UUID.randomUUID().toString()
            val request = CreateSolicitudRequest(
                id_servicio = idServicio,
                fecha_servicio = fecha,
                hora_servicio = hora,
                direccion_atencion = direccion,
                client_temp_id = clientTempId
            )

            ApiClient.service.createSolicitud(authHeader, request).enqueue(object : Callback<CreateSolicitudResponse> {
                override fun onResponse(call: Call<CreateSolicitudResponse>, response: Response<CreateSolicitudResponse>) {
                    btnConfirmar.isEnabled = true
                    btnConfirmar.text = "Confirmar Reserva"
                    val idSolicitud = response.body()?.id_solicitud
                    if (response.isSuccessful && idSolicitud != null) {
                        dbHelper.saveSolicitudLocal(buildSolicitud(idSolicitud, clientTempId, fecha, hora, direccion), "synced")
                        Toast.makeText(this@AgendarActivity, "Solicitud creada correctamente.", Toast.LENGTH_SHORT).show()
                        goToHistorial()
                    } else {
                        Toast.makeText(this@AgendarActivity, "No se pudo crear la solicitud.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CreateSolicitudResponse>, t: Throwable) {
                    btnConfirmar.isEnabled = true
                    btnConfirmar.text = "Confirmar Reserva"
                    val localId = -System.currentTimeMillis().toInt()
                    dbHelper.saveSolicitudLocal(buildSolicitud(localId, clientTempId, fecha, hora, direccion), "pending", t.message)
                    Toast.makeText(this@AgendarActivity, "Guardada localmente para sincronizar después.", Toast.LENGTH_SHORT).show()
                    goToHistorial()
                }
            })
        }
    }

    private fun buildSolicitud(
        idSolicitud: Int,
        clientTempId: String,
        fecha: String,
        hora: String,
        direccion: String
    ): Solicitud {
        val servicio = dbHelper.getServicioById(idServicio)
        return Solicitud(
            id_solicitud = idSolicitud,
            client_temp_id = clientTempId,
            id_usuario = sessionManager.getUserId(),
            id_servicio = idServicio,
            id_personal = null,
            fecha_servicio = fecha,
            hora_servicio = hora,
            direccion_atencion = direccion,
            estado = "Pendiente",
            cliente = sessionManager.getUserName(),
            servicio = servicio?.nombre,
            servicio_descripcion = servicio?.descripcion,
            personal_nombre = null,
            fecha_creacion = null,
            updated_at = null
        )
    }

    private fun showDatePicker(target: EditText) {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                target.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()
    }

    private fun showTimePicker(target: EditText) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                horaServicioApi = String.format(Locale.US, "%02d:%02d:00", hourOfDay, minute)
                val period = if (hourOfDay < 12) "AM" else "PM"
                val displayHour = when {
                    hourOfDay == 0 -> 12
                    hourOfDay > 12 -> hourOfDay - 12
                    else -> hourOfDay
                }
                target.setText(String.format(Locale.US, "%02d:%02d %s", displayHour, minute, period))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun goToHistorial() {
        startActivity(Intent(this, HistorialActivity::class.java))
        finish()
    }

    companion object {
        const val EXTRA_ID_SERVICIO = "extra_id_servicio"
        private const val MIN_ADDRESS_LENGTH = 10
    }
}
