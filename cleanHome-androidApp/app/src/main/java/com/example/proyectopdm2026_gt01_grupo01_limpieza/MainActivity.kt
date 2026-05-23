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
import com.example.proyectopdm2026_gt01_grupo01_limpieza.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var dbHelper: CleanHomeDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)
        dbHelper = CleanHomeDbHelper(this)

        if (sessionManager.isLoggedIn()) {
            goToHomeByRole()
            return
        }

        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val botonIniciar = findViewById<Button>(R.id.btnIniciarSesion)

        botonIniciar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val pass = etContrasena.text.toString().trim()

            if (correo.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Correo y contraseña son obligatorios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            botonIniciar.isEnabled = false
            ApiClient.service.login(LoginRequest(correo, pass)).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    botonIniciar.isEnabled = true
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        sessionManager.saveSession(body.token, body.usuario)
                        dbHelper.saveUsuario(body.usuario, body.token)
                        goToHomeByRole()
                    } else {
                        Toast.makeText(this@MainActivity, "Credenciales inválidas o servidor no disponible.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    botonIniciar.isEnabled = true
                    Toast.makeText(this@MainActivity, "No se pudo conectar con el backend local.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        findViewById<TextView>(R.id.tvAdmin).setOnClickListener {
            Toast.makeText(this, "Inicia sesión con una cuenta administradora.", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.tvRegistrate).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }

    private fun goToHomeByRole() {
        val target = if (sessionManager.getRoleName().equals("Admin", ignoreCase = true)) {
            AdminActivity::class.java
        } else {
            CatalogoActivity::class.java
        }
        val intent = Intent(this, target)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
