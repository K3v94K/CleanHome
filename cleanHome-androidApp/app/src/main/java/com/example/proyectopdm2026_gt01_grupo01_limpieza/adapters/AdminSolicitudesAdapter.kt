package com.example.proyectopdm2026_gt01_grupo01_limpieza.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopdm2026_gt01_grupo01_limpieza.R
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.Solicitud
import com.example.proyectopdm2026_gt01_grupo01_limpieza.utils.UiFormatters

class AdminSolicitudesAdapter(
    private var listaSolicitudes: List<Solicitud>,
    private val onCambiarEstadoClick: (Solicitud) -> Unit
) : RecyclerView.Adapter<AdminSolicitudesAdapter.SolicitudViewHolder>() {

    class SolicitudViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCliente: TextView = view.findViewById(R.id.tv_item_solicitud_cliente)
        val tvEstado: TextView = view.findViewById(R.id.tv_item_solicitud_estado)
        val tvServicio: TextView = view.findViewById(R.id.tv_item_solicitud_servicio)
        val tvFecha: TextView = view.findViewById(R.id.tv_item_solicitud_fecha)
        val tvDireccion: TextView = view.findViewById(R.id.tv_item_solicitud_direccion)
        val btnAsignar: Button = view.findViewById(R.id.btn_item_solicitud_asignar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitudViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_solicitud_admin, parent, false)
        return SolicitudViewHolder(view)
    }

    override fun onBindViewHolder(holder: SolicitudViewHolder, position: Int) {
        val solicitud = listaSolicitudes[position]
        holder.tvCliente.text = solicitud.cliente ?: "Cliente #${solicitud.id_usuario}"
        holder.tvEstado.text = solicitud.estado
        holder.tvEstado.setTextColor(UiFormatters.estadoColor(solicitud.estado))
        holder.tvServicio.text = (solicitud.servicio ?: "Servicio #${solicitud.id_servicio}").uppercase()
        holder.tvFecha.text = UiFormatters.formatFechaHora(solicitud.fecha_servicio, solicitud.hora_servicio)
        holder.tvDireccion.text = solicitud.direccion_atencion
        holder.btnAsignar.text = "Cambiar estado"
        holder.btnAsignar.visibility = View.VISIBLE
        holder.btnAsignar.setOnClickListener { onCambiarEstadoClick(solicitud) }
    }

    override fun getItemCount() = listaSolicitudes.size

    fun actualizarDatos(nuevaLista: List<Solicitud>) {
        listaSolicitudes = nuevaLista
        notifyDataSetChanged()
    }
}
