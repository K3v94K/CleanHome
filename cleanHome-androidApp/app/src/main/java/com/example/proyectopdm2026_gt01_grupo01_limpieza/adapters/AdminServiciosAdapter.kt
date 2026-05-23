package com.example.proyectopdm2026_gt01_grupo01_limpieza.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopdm2026_gt01_grupo01_limpieza.R
import com.example.proyectopdm2026_gt01_grupo01_limpieza.models.Servicio

class AdminServiciosAdapter(
    private var listaServicios: List<Servicio>,
    private val onEditarClick: (Servicio) -> Unit,
    private val onEliminarClick: (Servicio) -> Unit
) : RecyclerView.Adapter<AdminServiciosAdapter.ServicioViewHolder>() {

    class ServicioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tv_item_servicio_nombre)
        val tvPrecio: TextView = view.findViewById(R.id.tv_item_servicio_precio)
        val btnEditar: ImageButton = view.findViewById(R.id.btn_item_servicio_editar)
        val btnEliminar: ImageButton = view.findViewById(R.id.btn_item_servicio_eliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_servicio_admin, parent, false)
        return ServicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        val servicio = listaServicios[position]
        holder.tvNombre.text = servicio.nombre
        holder.tvPrecio.text = "$${String.format("%.2f", servicio.precio)}"
        holder.btnEditar.setOnClickListener { onEditarClick(servicio) }
        holder.btnEliminar.setOnClickListener { onEliminarClick(servicio) }
    }

    override fun getItemCount() = listaServicios.size

    fun actualizarDatos(nuevaLista: List<Servicio>) {
        listaServicios = nuevaLista
        notifyDataSetChanged()
    }
}
