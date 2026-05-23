package com.example.proyectopdm2026_gt01_grupo01_limpieza.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopdm2026_gt01_grupo01_limpieza.R
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.Servicio

class ServiciosCatalogoAdapter(
    private var servicios: List<Servicio>,
    private val onServicioClick: (Servicio) -> Unit
) : RecyclerView.Adapter<ServiciosCatalogoAdapter.ServicioViewHolder>() {

    class ServicioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tv_servicio_nombre)
        val tvDescripcion: TextView = view.findViewById(R.id.tv_servicio_descripcion)
        val tvPrecio: TextView = view.findViewById(R.id.tv_servicio_precio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servicio_catalogo, parent, false)
        return ServicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        val servicio = servicios[position]
        holder.tvNombre.text = servicio.nombre
        holder.tvDescripcion.text = servicio.descripcion ?: "Servicio de limpieza CleanHome"
        holder.tvPrecio.text = "$${String.format("%.2f", servicio.precio)}"
        holder.itemView.setOnClickListener { onServicioClick(servicio) }
    }

    override fun getItemCount(): Int = servicios.size

    fun actualizarDatos(nuevosServicios: List<Servicio>) {
        servicios = nuevosServicios
        notifyDataSetChanged()
    }
}
