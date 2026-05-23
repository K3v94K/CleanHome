package com.example.proyectopdm2026_gt01_grupo01_limpieza.utils

import android.graphics.Color

object UiFormatters {
    fun formatFechaHora(fecha: String, hora: String): String {
        val fechaCorta = fecha.substringBefore("T").substringBefore(" ")
        val horaCorta = hora.substringBefore(".").take(5)
        return "$fechaCorta - $horaCorta"
    }

    fun estadoColor(estado: String): Int {
        return when (estado.trim().lowercase()) {
            "pendiente" -> Color.parseColor("#F59E0B")
            "confirmada" -> Color.parseColor("#2563EB")
            "en proceso" -> Color.parseColor("#7C3AED")
            "completada" -> Color.parseColor("#16A34A")
            "cancelada" -> Color.parseColor("#DC2626")
            else -> Color.parseColor("#6B7280")
        }
    }
}
