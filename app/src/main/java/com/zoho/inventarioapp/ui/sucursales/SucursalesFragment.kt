package com.zoho.inventarioapp.ui.sucursales

import android.os.Bundle
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zoho.inventarioapp.R
import com.zoho.inventarioapp.data.local.entities.Sucursal
import kotlinx.coroutines.launch

class SucursalesFragment : Fragment() {

    private val viewModel: SucursalesViewModel by viewModels()
    private lateinit var adapter: SucursalAdapter
    private lateinit var rvSucursales: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAgregar: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sucursales, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        rvSucursales = view.findViewById(R.id.rvSucursales)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        progressBar = view.findViewById(R.id.progressBar)
        fabAgregar = view.findViewById(R.id.fabAgregar)

        setupRecyclerView()
        observarDatos()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = SucursalAdapter(
            onEditClick = { sucursal ->
                mostrarDialogoEditar(sucursal)
            },
            onDeleteClick = { sucursal ->
                confirmarEliminar(sucursal)
            }
        )
        rvSucursales.adapter = adapter
    }

    private fun observarDatos() {
        // Observar lista de sucursales
        lifecycleScope.launch {
            viewModel.sucursales.collect { lista ->
                adapter.submitList(lista)

                if (lista.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                    rvSucursales.visibility = View.GONE
                } else {
                    tvEmpty.visibility = View.GONE
                    rvSucursales.visibility = View.VISIBLE
                }
            }
        }

        // Observar estado de carga
        lifecycleScope.launch {
            viewModel.cargando.collect { cargando ->
                progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
            }
        }

        // Observar mensajes
        lifecycleScope.launch {
            viewModel.mensaje.collect { mensaje ->
                mensaje?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.limpiarMensaje()
                }
            }
        }
    }

    private fun setupListeners() {
        fabAgregar.setOnClickListener {
            mostrarDialogoAgregar()
        }
    }

    private fun mostrarDialogoAgregar() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_sucursal_form, null)

        val etNombre = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNombre)
        val etDireccion = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etDireccion)
        val etTelefono = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etTelefono)

        // Solo permitir números y guion
        etTelefono.keyListener = DigitsKeyListener.getInstance("0123456789-")

        AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
            .setTitle("Agregar Sucursal")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString()
                val direccion = etDireccion.text.toString()
                val telefono = etTelefono.text.toString()

                if (nombre.isNotEmpty() && direccion.isNotEmpty() && telefono.isNotEmpty() && telefono.matches(Regex("\\d{4}-\\d{4}"))) {
                    viewModel.agregarSucursal(nombre, direccion, telefono)
                }  else if(!telefono.matches(Regex("\\d{4}-\\d{4}"))){
                    Toast.makeText(requireContext(), "Ingresa el Telefono con el formato 0000-0000", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditar(sucursal: Sucursal) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_sucursal_form, null)

        val etNombre = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNombre)
        val etDireccion = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etDireccion)
        val etTelefono = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etTelefono)

        // Solo permitir números y guion
        etTelefono.keyListener = DigitsKeyListener.getInstance("0123456789-")

        // Pre-llenar con datos actuales
        etNombre.setText(sucursal.nombre)
        etDireccion.setText(sucursal.direccion)
        etTelefono.setText(sucursal.telefono)

        AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
            .setTitle("Editar Sucursal")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { _, _ ->
                val nombre = etNombre.text.toString()
                val direccion = etDireccion.text.toString()
                val telefono = etTelefono.text.toString()

                if (nombre.isNotEmpty() && direccion.isNotEmpty() && telefono.isNotEmpty() && telefono.matches(Regex("\\d{4}-\\d{4}"))) {
                    viewModel.agregarSucursal(nombre, direccion, telefono)
                }  else if(!telefono.matches(Regex("\\d{4}-\\d{4}"))){
                    Toast.makeText(requireContext(), "Ingresa el Telefono con el formato 0000-0000", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarEliminar(sucursal: Sucursal) {
        AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
            .setTitle("Eliminar Sucursal")
            .setMessage("¿Estás seguro de eliminar '${sucursal.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminarSucursal(sucursal)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}