package com.example.proyectopdm2026_gt01_grupo01_limpieza

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectopdm2026_gt01_grupo01_limpieza.data.CleanHomeDbHelper
import com.example.proyectopdm2026_gt01_grupo01_limpieza.session.SessionManager

class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val sessionManager = SessionManager(this)
        val dbHelper = CleanHomeDbHelper(this)
        val tvNombre = findViewById<TextView>(R.id.tv_perfil_nombre)
        val tvCorreo = findViewById<TextView>(R.id.tv_perfil_correo)

        findViewById<TextView>(R.id.tv_perfil_back).setOnClickListener { finish() }
        tvNombre.text = sessionManager.getUserName().ifBlank { "Usuario" }
        tvCorreo.text = sessionManager.getUserEmail().ifBlank { "correo@ejemplo.com" }

        findViewById<Button>(R.id.btn_perfil_editar).setOnClickListener {
            Toast.makeText(this, "Edición de perfil pendiente para una siguiente fase.", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_perfil_cerrar_sesion).setOnClickListener {
            sessionManager.clear()
            dbHelper.clearAll()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
