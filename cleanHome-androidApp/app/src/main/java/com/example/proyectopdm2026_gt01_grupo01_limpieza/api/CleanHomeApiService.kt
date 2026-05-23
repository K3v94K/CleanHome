package com.example.proyectopdm2026_gt01_grupo01_limpieza.api

import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.AuthResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.CreateSolicitudRequest
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.CreateSolicitudResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.LoginRequest
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.MessageResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.RegisterRequest
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.RegisterResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.ServicioRequest
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.ServiciosResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.SolicitudesResponse
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.UpdateEstadoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CleanHomeApiService {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @GET("servicios")
    fun getServicios(): Call<ServiciosResponse>

    @POST("solicitudes")
    fun createSolicitud(
        @Header("Authorization") authorization: String,
        @Body request: CreateSolicitudRequest
    ): Call<CreateSolicitudResponse>

    @GET("solicitudes/mis-solicitudes")
    fun getMisSolicitudes(
        @Header("Authorization") authorization: String
    ): Call<SolicitudesResponse>

    @GET("admin/solicitudes")
    fun getAdminSolicitudes(
        @Header("Authorization") authorization: String
    ): Call<SolicitudesResponse>

    @PATCH("admin/solicitudes/{id}/estado")
    fun updateSolicitudEstado(
        @Header("Authorization") authorization: String,
        @Path("id") idSolicitud: Int,
        @Body request: UpdateEstadoRequest
    ): Call<MessageResponse>

    @GET("admin/servicios")
    fun getAdminServicios(
        @Header("Authorization") authorization: String
    ): Call<ServiciosResponse>

    @POST("admin/servicios")
    fun createServicio(
        @Header("Authorization") authorization: String,
        @Body request: ServicioRequest
    ): Call<MessageResponse>

    @PUT("admin/servicios/{id}")
    fun updateServicio(
        @Header("Authorization") authorization: String,
        @Path("id") idServicio: Int,
        @Body request: ServicioRequest
    ): Call<MessageResponse>

    @DELETE("admin/servicios/{id}")
    fun deleteServicio(
        @Header("Authorization") authorization: String,
        @Path("id") idServicio: Int
    ): Call<MessageResponse>
}
