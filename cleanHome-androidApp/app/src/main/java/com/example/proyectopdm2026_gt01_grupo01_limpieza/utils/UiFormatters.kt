package com.example.proyectopdm2026_gt01_grupo01_limpieza.utils

import android.graphics.Color

object UiFormatters {
    fun formatFechaHora(fecha: String, hora: String): String {
        val fechaCorta = fecha.substringBefore("T").substringBefore(" ")
        return "$fechaCorta - ${formatHora12(hora)}"
    }

    private fun formatHora12(hora: String): String {
        val parts = hora.substringBefore(".").split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: return hora
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val period = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return "%02d:%02d %s".format(displayHour, minute, period)
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
