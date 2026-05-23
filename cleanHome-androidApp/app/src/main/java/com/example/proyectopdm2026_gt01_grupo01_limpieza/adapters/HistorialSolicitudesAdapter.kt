package com.example.proyectopdm2026_gt01_grupo01_limpieza.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopdm2026_gt01_grupo01_limpieza.R
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.Solicitud
import com.example.proyectopdm2026_gt01_grupo01_limpieza.utils.UiFormatters

class HistorialSolicitudesAdapter(
    private var solicitudes: List<Solicitud>
) : RecyclerView.Adapter<HistorialSolicitudesAdapter.SolicitudViewHolder>() {

    class SolicitudViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvServicio: TextView = view.findViewById(R.id.tv_historial_servicio)
        val tvEstado: TextView = view.findViewById(R.id.tv_historial_estado)
        val tvFecha: TextView = view.findViewById(R.id.tv_historial_fecha)
        val tvDireccion: TextView = view.findViewById(R.id.tv_historial_direccion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitudViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historial_solicitud, parent, false)
        return SolicitudViewHolder(view)
    }

    override fun onBindViewHolder(holder: SolicitudViewHolder, position: Int) {
        val solicitud = solicitudes[position]
        holder.tvServicio.text = solicitud.servicio ?: "Servicio #${solicitud.id_servicio}"
        holder.tvEstado.text = solicitud.estado
        holder.tvEstado.setTextColor(UiFormatters.estadoColor(solicitud.estado))
        holder.tvFecha.text = UiFormatters.formatFechaHora(solicitud.fecha_servicio, solicitud.hora_servicio)
        holder.tvDireccion.text = solicitud.direccion_atencion
    }

    override fun getItemCount(): Int = solicitudes.size

    fun actualizarDatos(nuevasSolicitudes: List<Solicitud>) {
        solicitudes = nuevasSolicitudes
        notifyDataSetChanged()
    }
}
