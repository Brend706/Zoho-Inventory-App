package com.zoho.inventarioapp.ui.inventario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zoho.inventarioapp.R
import androidx.fragment.app.viewModels
import android.app.AlertDialog
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.zoho.inventarioapp.data.local.entities.InventarioSucursal
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class InventarioFragment : Fragment() {

    private val viewModel: InventarioViewModel by viewModels()
    private lateinit var contenedorInventario: LinearLayout
    private lateinit var btnFiltrarSucursal: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inventario, container, false)
        contenedorInventario = view.findViewById(R.id.contenedorInventario)
        btnFiltrarSucursal = view.findViewById(R.id.btnFiltrarSucursal)

        // Obtener SharedPreferences para validacion del boton de Filtrar por Sucursal
        val sharedPrefs = requireContext().getSharedPreferences("userPrefs", android.content.Context.MODE_PRIVATE)
        val esAdmin = sharedPrefs.getBoolean("esAdmin", false)
        val idSucursalUsuario = sharedPrefs.getInt("idSucursalUsuario", -1)

        // Mostrar siempre el botón para ambos roles
        btnFiltrarSucursal.visibility = View.VISIBLE

        if (esAdmin) {
            //Si es admin, permitir usar el filtro por sucursal
            btnFiltrarSucursal.isEnabled = true
            btnFiltrarSucursal.alpha = 1f
            btnFiltrarSucursal.setOnClickListener {
                mostrarDialogoFiltrarPorSucursal()
            }
        } else {
            // Si es empleado, desactiva el boton y aplica un filtro automáticamente por su sucursal asignada
            btnFiltrarSucursal.isEnabled = false
            btnFiltrarSucursal.alpha = 0.6f // diseno visual de desactivado

            // Esperar a que las sucursales estén disponibles antes de aplicar filtro
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.sucursales.collect { listaSucursales ->
                    if (listaSucursales.isNotEmpty()) {
                        val sucursalEmpleado = listaSucursales.find { it.idSucursal == idSucursalUsuario }
                        if (sucursalEmpleado != null) {
                            // Aplica filtro solo una vez
                            viewModel.filtrarPorSucursal(sucursalEmpleado)
                            // Mostrar nombre de sucursal en el botón
                            btnFiltrarSucursal.text = sucursalEmpleado.nombre
                        }
                    }
                }
            }
        }

        view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
            R.id.fabAgregar
        ).setOnClickListener {
            mostrarDialogoCrearInventario()
        }

        btnFiltrarSucursal.setOnClickListener {
            mostrarDialogoFiltrarPorSucursal()
        }

        listarInventarios()
        observarMensajes()
        verPorSucursalSeleccionada()

        return view
    }

    private fun listarInventarios() {
        lifecycleScope.launch {
            viewModel.inventariosFiltrados.collect { lista ->
                contenedorInventario.removeAllViews()

                if (lista.isEmpty()) {
                    mostrarMensajeVacio()
                } else {
                    lista.forEach { inventario ->
                        crearInventario(inventario)
                    }
                }
            }
        }
    }

    private fun observarMensajes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mensaje.collect { mensaje ->
                if (mensaje != null) {
                    Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
                    viewModel.limpiarMensaje()
                }
            }
        }
    }

    private fun verPorSucursalSeleccionada() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sucursalSeleccionada.collect { sucursal ->
                btnFiltrarSucursal.text = if (sucursal != null) {
                    "Sucursal: ${sucursal.nombre}"
                } else {
                    "Todas las sucursales"
                }
            }
        }
    }

    private fun mostrarMensajeVacio() {
        val textView = TextView(requireContext()).apply {
            text = "No hay inventarios registrados"
            textSize = 16f
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setPadding(16, 100, 16, 16)
        }
        contenedorInventario.addView(textView)
    }

    private fun crearInventario(inventario: InventarioSucursal) {
        val card = layoutInflater.inflate(R.layout.item_inventario, contenedorInventario, false)

        val tvProducto = card.findViewById<TextView>(R.id.tvNombreProducto)
        val tvSucursal = card.findViewById<TextView>(R.id.tvNombreSucursal)
        val tvStock = card.findViewById<TextView>(R.id.tvStockActual)
        val tvFecha = card.findViewById<TextView>(R.id.tvUltimaActualizacion)
        val btnVer = card.findViewById<ImageButton>(R.id.btnVer)
        val btnEditar = card.findViewById<ImageButton>(R.id.btnEditar)
        val btnEliminar = card.findViewById<ImageButton>(R.id.btnEliminar)

        // Obtener nombre del producto
        val producto = viewModel.productos.value.find { it.idProducto == inventario.idProducto }
        tvProducto.text = producto?.nombre ?: "Producto desconocido"

        // Obtener nombre de la sucursal
        val sucursal = viewModel.sucursales.value.find { it.idSucursal == inventario.idSucursal }
        tvSucursal.text = sucursal?.nombre ?: "Sucursal Desconocida"

        // Stock actual
        tvStock.text = "Stock actual: ${inventario.stockActual}"

        // Fecha de última actualización
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fechaFormateada = sdf.format(Date(inventario.ultimaActualizacion))
        tvFecha.text = "Actualizado: $fechaFormateada"

        // Botones
        btnVer.setOnClickListener {
            Toast.makeText(requireContext(), "Próximamente: Ver movimientos", Toast.LENGTH_SHORT).show()
        }
        btnEditar.setOnClickListener {
            mostrarDialogoEditarInventario(inventario)
        }
        btnEliminar.setOnClickListener {
            eliminarInventarioConConfirmacion(inventario)
        }
        contenedorInventario.addView(card)
    }

    private fun mostrarDialogoCrearInventario() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_inventario, null)
        val spProducto = dialogView.findViewById<Spinner>(R.id.spProducto)
        val spSucursal = dialogView.findViewById<Spinner>(R.id.spSucursal)
        val etStock = dialogView.findViewById<EditText>(R.id.etStockActual)

        // Cargar productos
        lifecycleScope.launch {
            viewModel.productos.collect { productos ->
                val listaProductos = mutableListOf("Selecciona un producto")
                listaProductos.addAll(productos.map { it.nombre })
                val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, listaProductos)
                adapter.setDropDownViewResource(R.layout.item_spinner)
                spProducto.adapter = adapter
            }
        }

        // Cargar sucursales en general
        /*lifecycleScope.launch {
            viewModel.sucursales.collect { sucursales ->
                val listaSucursales = mutableListOf("Selecciona una sucursal")
                listaSucursales.addAll(sucursales.map { it.nombre })
                val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, listaSucursales)
                adapter.setDropDownViewResource(R.layout.item_spinner)
                spSucursal.adapter = adapter
            }
        }*/

        // Cargar sucursales segun el rol
        val prefs = requireContext().getSharedPreferences("userPrefs", android.content.Context.MODE_PRIVATE)
        val esAdmin = prefs.getBoolean("esAdmin", false)
        val idSucursalUsuario = prefs.getInt("idSucursalUsuario", -1)

        lifecycleScope.launch {
            viewModel.sucursales.collect { sucursales ->
                val listaSucursales = mutableListOf("Selecciona una sucursal")

                if (esAdmin) {
                    //Si es el admin muestra todas las sucursales
                    listaSucursales.addAll(sucursales.map { it.nombre })
                    val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, listaSucursales)
                    adapter.setDropDownViewResource(R.layout.item_spinner)
                    spSucursal.adapter = adapter
                    spSucursal.isEnabled = true
                } else {
                    //Si es empleado va a mostrar solo su sucursal
                    val sucursalEmpleado = sucursales.find { it.idSucursal == idSucursalUsuario }
                    if (sucursalEmpleado != null) {
                        listaSucursales.add(sucursalEmpleado.nombre)
                        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, listaSucursales)
                        adapter.setDropDownViewResource(R.layout.item_spinner)
                        spSucursal.adapter = adapter
                        spSucursal.setSelection(1) // selecciona la única opción
                        spSucursal.isEnabled = false // deshabilitar edición
                    } else {
                        // En caso de error (no encuentra sucursal)
                        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, listOf("Sucursal no encontrada"))
                        spSucursal.adapter = adapter
                        spSucursal.isEnabled = false
                    }
                }
            }
        }


        val builder = AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
        builder.setTitle("Crear Inventario")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val productoPos = spProducto.selectedItemPosition
                val sucursalPos = spSucursal.selectedItemPosition
                val stockStr = etStock.text.toString().trim()

                if (productoPos == 0 || sucursalPos == 0) {
                    Toast.makeText(requireContext(), "Selecciona producto y sucursal", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (stockStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Ingresa el stock actual", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val stock = try {
                    stockStr.toInt()
                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Stock debe ser un número válido", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val idProducto = viewModel.productos.value.getOrNull(productoPos - 1)?.idProducto ?: return@setPositiveButton
                //val idSucursal = viewModel.sucursales.value.getOrNull(sucursalPos - 1)?.idSucursal ?: return@setPositiveButton

                val idSucursal = if (esAdmin) {
                    // Si es admin, toma el ID según el spinner
                    viewModel.sucursales.value.getOrNull(sucursalPos - 1)?.idSucursal
                } else {
                    // Si es empleado, usa directamente su sucursal asignada
                    idSucursalUsuario.takeIf { it != -1 }
                }
                if (idSucursal == null) {
                    Toast.makeText(requireContext(), "No se pudo determinar la sucursal", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val nuevoInventario = InventarioSucursal(
                    idProducto = idProducto,
                    idSucursal = idSucursal,
                    stockActual = stock,
                    ultimaActualizacion = System.currentTimeMillis()
                )

                lifecycleScope.launch {
                    try {
                        viewModel.agregarInventario(nuevoInventario)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error al crear inventario: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            setTextColor(resources.getColor(R.color.morado_suave))
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            setTextColor(resources.getColor(R.color.morado_suave))
        }
    }

    private fun mostrarDialogoEditarInventario(inventario: InventarioSucursal) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_inventario, null)
        val spProducto = dialogView.findViewById<Spinner>(R.id.spProducto)
        val spSucursal = dialogView.findViewById<Spinner>(R.id.spSucursal)
        val etStock = dialogView.findViewById<EditText>(R.id.etStockActual)

        etStock.setText(inventario.stockActual.toString())

        // Cargar productos
        lifecycleScope.launch {
            viewModel.productos.collect { productos ->
                val listaProductos = mutableListOf("Selecciona un producto")
                listaProductos.addAll(productos.map { it.nombre })
                val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, listaProductos)
                adapter.setDropDownViewResource(R.layout.item_spinner)
                spProducto.adapter = adapter

                val index = productos.indexOfFirst { it.idProducto == inventario.idProducto }
                if (index != -1) spProducto.setSelection(index + 1)
            }
        }

        // Cargar sucursales segun el rol
        val prefs = requireContext().getSharedPreferences("userPrefs", android.content.Context.MODE_PRIVATE)
        val esAdmin = prefs.getBoolean("esAdmin", false)
        val idSucursalUsuario = prefs.getInt("idSucursalUsuario", -1)

        lifecycleScope.launch {

            viewModel.sucursales.collect { sucursales ->
                val listaSucursales = mutableListOf("Selecciona una sucursal")

                if (esAdmin) {
                    //Si es el admin muestra todas las sucursales
                    listaSucursales.addAll(sucursales.map { it.nombre })
                    val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, listaSucursales)
                    adapter.setDropDownViewResource(R.layout.item_spinner)
                    spSucursal.adapter = adapter
                    spSucursal.isEnabled = true

                    // Se Selecciona la sucursal actual del inventario en el spinner
                    val index = sucursales.indexOfFirst { it.idSucursal == inventario.idSucursal }
                    if (index != -1) spSucursal.setSelection(index + 1)
                } else {
                    //Si es empleado va a mostrar solo su sucursal
                    val sucursalEmpleado = sucursales.find { it.idSucursal == idSucursalUsuario }
                    if (sucursalEmpleado != null) {
                        listaSucursales.add(sucursalEmpleado.nombre)
                        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, listaSucursales)
                        adapter.setDropDownViewResource(R.layout.item_spinner)
                        spSucursal.adapter = adapter
                        spSucursal.setSelection(1) // única opción
                        spSucursal.isEnabled = false
                    } else {
                        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, listOf("Sucursal no encontrada"))
                        spSucursal.adapter = adapter
                        spSucursal.isEnabled = false
                    }
                }
            }
        }

        val builder = AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
        builder.setTitle("Editar Inventario")
            .setView(dialogView)

            .setPositiveButton("Guardar") { _, _ ->
                val productoPos = spProducto.selectedItemPosition
                val sucursalPos = spSucursal.selectedItemPosition
                val stockStr = etStock.text.toString().trim()

                if (productoPos == 0 || sucursalPos == 0 || stockStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val idProducto = viewModel.productos.value.getOrNull(productoPos - 1)?.idProducto ?: inventario.idProducto
                //val idSucursal = viewModel.sucursales.value.getOrNull(sucursalPos - 1)?.idSucursal ?: inventario.idSucursal
                val idSucursal = if (esAdmin) {
                    // Admin: obtiene del spinner
                    viewModel.sucursales.value.getOrNull(sucursalPos - 1)?.idSucursal
                } else {
                    // Empleado: usa solo su propia sucursal
                    idSucursalUsuario.takeIf { it != -1 }
                } ?: inventario.idSucursal

                val inventarioActualizado = inventario.copy(
                    idProducto = idProducto,
                    idSucursal = idSucursal,
                    stockActual = stockStr.toInt(),
                    ultimaActualizacion = System.currentTimeMillis()
                )

                viewModel.editarInventario(inventarioActualizado)
            }
            .setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
            setTextColor(resources.getColor(R.color.morado_suave))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
            setTextColor(resources.getColor(R.color.morado_suave))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }

    private fun eliminarInventarioConConfirmacion(inventario: InventarioSucursal) {
        val producto = viewModel.productos.value.find { it.idProducto == inventario.idProducto }
        val sucursal = viewModel.sucursales.value.find { it.idSucursal == inventario.idSucursal }

        val dialog = AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
            .setTitle("Eliminar Inventario")
            .setMessage("¿Estás seguro de eliminar el inventario de ${producto?.nombre} en ${sucursal?.nombre}?")
            .setPositiveButton("Sí") { _, _ ->
                viewModel.eliminarInventario(inventario)
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            setTextColor(resources.getColor(R.color.morado_mas_suave))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
            setTextColor(resources.getColor(R.color.morado_mas_suave))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }

    private fun mostrarDialogoFiltrarPorSucursal() {
        val sucursales = viewModel.sucursales.value
        val nombres = mutableListOf("Todas las sucursales")
        nombres.addAll(sucursales.map { it.nombre })

        val builder = AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
        builder.setTitle("Filtrar por Sucursal")
            .setItems(nombres.toTypedArray()) { _, which ->
                if (which == 0) {
                    viewModel.filtrarPorSucursal(null)
                } else {
                    val sucursal = sucursales.getOrNull(which - 1)
                    viewModel.filtrarPorSucursal(sucursal)
                }
            }
            .setNegativeButton("Cancelar", null)

        builder.create().show()
    }
}