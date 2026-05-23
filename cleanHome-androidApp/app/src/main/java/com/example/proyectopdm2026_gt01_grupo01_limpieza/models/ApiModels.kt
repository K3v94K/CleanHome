package com.example.proyectopdm2026_gt01_grupo01_limpieza.models

data class LoginRequest(
    val correo: String,
    val password: String
)

data class RegisterRequest(
    val nombre: String,
    val correo: String,
    val telefono: String?,
    val direccion: String?,
    val password: String
)

data class AuthResponse(
    val message: String?,
    val token: String,
    val usuario: Usuario
)

data class RegisterResponse(
    val message: String?,
    val id_usuario: Int?
)

data class Usuario(
    val id_usuario: Int,
    val nombre: String,
    val correo: String,
    val telefono: String?,
    val direccion: String?,
    val id_rol: Int?,
    val nombre_rol: String?
)

data class Servicio(
    val id_servicio: Int,
    val nombre: String,
    val descripcion: String?,
    val duracion_estimada: String?,
    val precio: Double,
    val activo: Int,
    val fecha_creacion: String?,
    val updated_at: String?
)

data class ServiciosResponse(
    val servicios: List<Servicio>
)

data class ServicioRequest(
    val nombre: String,
    val descripcion: String?,
    val duracion_estimada: String?,
    val precio: Double,
    val activo: Boolean = true
)

data class CreateSolicitudRequest(
    val id_servicio: Int,
    val fecha_servicio: String,
    val hora_servicio: String,
    val direccion_atencion: String,
    val client_temp_id: String?
)

data class CreateSolicitudResponse(
    val message: String?,
    val id_solicitud: Int?
)

data class Solicitud(
    val id_solicitud: Int,
    val client_temp_id: String?,
    val id_usuario: Int,
    val id_servicio: Int,
    val id_personal: Int?,
    val fecha_servicio: String,
    val hora_servicio: String,
    val direccion_atencion: String,
    val estado: String,
    val cliente: String?,
    val servicio: String?,
    val servicio_descripcion: String?,
    val personal_nombre: String?,
    val fecha_creacion: String?,
    val updated_at: String?
)

data class SolicitudesResponse(
    val solicitudes: List<Solicitud>
)

data class UpdateEstadoRequest(
    val estado: String
)

data class MessageResponse(
    val message: String?
)
