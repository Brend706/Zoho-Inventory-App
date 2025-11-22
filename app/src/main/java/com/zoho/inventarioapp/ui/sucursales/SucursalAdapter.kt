package com.zoho.inventarioapp.ui.sucursales

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zoho.inventarioapp.R
import com.zoho.inventarioapp.data.local.entities.Sucursal

class SucursalAdapter(
    private val onEditClick: (Sucursal) -> Unit,
    private val onDeleteClick: (Sucursal) -> Unit
) : ListAdapter<Sucursal, SucursalAdapter.SucursalViewHolder>(SucursalDiffCallback()) {

    private var esAdmin: Boolean = false
    fun setEsAdmin(admin: Boolean) {
        esAdmin = admin
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SucursalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sucursal, parent, false)
        return SucursalViewHolder(view)
    }

    override fun onBindViewHolder(holder: SucursalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SucursalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        private val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        private val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(sucursal: Sucursal) {
            tvNombre.text = sucursal.nombre
            tvDireccion.text = sucursal.direccion
            tvTelefono.text = sucursal.telefono

            // Configurar visibilidad según el rol
            if (esAdmin) {
                btnEditar.visibility = View.VISIBLE
                btnEliminar.visibility = View.VISIBLE

                btnEditar.setOnClickListener {
                    onEditClick(sucursal)
                }
                btnEliminar.setOnClickListener {
                    onDeleteClick(sucursal)
                }
            } else {
                btnEditar.visibility = View.GONE
                btnEliminar.visibility = View.GONE
            }
        }
    }

    class SucursalDiffCallback : DiffUtil.ItemCallback<Sucursal>() {
        override fun areItemsTheSame(oldItem: Sucursal, newItem: Sucursal): Boolean {
            return oldItem.idSucursal == newItem.idSucursal
        }

        override fun areContentsTheSame(oldItem: Sucursal, newItem: Sucursal): Boolean {
            return oldItem == newItem
        }
    }
}