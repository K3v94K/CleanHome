package com.example.proyectopdm2026_gt01_grupo01_limpieza

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectopdm2026_gt01_grupo01_limpieza.api.ApiClient
import com.example.proyectopdm2026_gt01_grupo01_limpieza.data.CleanHomeDbHelper
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.AuthResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.LoginRequest
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.RegisterRequest
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.RegisterResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistroActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var dbHelper: CleanHomeDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        sessionManager = SessionManager(this)
        dbHelper = CleanHomeDbHelper(this)

        findViewById<TextView>(R.id.tv_registro_back).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tv_registro_ir_login).setOnClickListener { finish() }

        val etNombre = findViewById<EditText>(R.id.et_registro_nombre)
        val etCorreo = findViewById<EditText>(R.id.et_registro_correo)
        val etTelefono = findViewById<EditText>(R.id.et_registro_telefono)
        val etPassword = findViewById<EditText>(R.id.et_registro_password)
        val etDireccion = findViewById<EditText>(R.id.et_registro_direccion)
        val btnGuardar = findViewById<Button>(R.id.btn_registro_guardar)

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()

            if (nombre.isBlank() || correo.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Nombre, correo y contraseña son obligatorios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnGuardar.isEnabled = false
            val request = RegisterRequest(
                nombre = nombre,
                correo = correo,
                telefono = telefono.ifBlank { null },
                direccion = direccion.ifBlank { null },
                password = password
            )

            ApiClient.service.register(request).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        loginAfterRegister(correo, password, btnGuardar)
                    } else {
                        btnGuardar.isEnabled = true
                        Toast.makeText(this@RegistroActivity, "No se pudo registrar. Revisa los datos.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    btnGuardar.isEnabled = true
                    Toast.makeText(this@RegistroActivity, "No se pudo conectar con el backend local.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loginAfterRegister(correo: String, password: String, button: Button) {
        ApiClient.service.login(LoginRequest(correo, password)).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                button.isEnabled = true
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    sessionManager.saveSession(body.token, body.usuario)
                    dbHelper.saveUsuario(body.usuario, body.token)
                    val intent = Intent(this@RegistroActivity, CatalogoActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this@RegistroActivity, "Cuenta creada. Inicia sesión para continuar.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                button.isEnabled = true
                Toast.makeText(this@RegistroActivity, "Cuenta creada. Inicia sesión para continuar.", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }
}
