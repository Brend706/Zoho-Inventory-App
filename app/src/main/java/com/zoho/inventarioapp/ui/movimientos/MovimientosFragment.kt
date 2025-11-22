package com.zoho.inventarioapp.ui.movimientos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.zoho.inventarioapp.R
import com.zoho.inventarioapp.data.local.database.AppDatabase
import com.zoho.inventarioapp.data.repository.InventarioSucursalRepository
import com.zoho.inventarioapp.data.repository.MovimientosInventarioRepository
import com.zoho.inventarioapp.data.local.entities.MovimientosInventario
import kotlinx.coroutines.launch
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.navigation.fragment.findNavController
import android.widget.Spinner
import android.widget.EditText
import android.widget.Toast
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import android.content.Context
import com.zoho.inventarioapp.data.repository.FechasProductoRepository
import android.app.DatePickerDialog
import android.widget.AdapterView
import android.widget.Button
import com.zoho.inventarioapp.data.local.dao.MovimientoConDetalles
import java.util.Calendar
import com.zoho.inventarioapp.data.local.entities.FechasProducto
import com.zoho.inventarioapp.data.local.entities.TipoMovimiento

class MovimientosFragment : Fragment() {

    private lateinit var contenedorMovimientos: LinearLayout
    private lateinit var titulo: TextView

    private var idInventario: Int = 0

    private val viewModel: MovimientosInventarioViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())

        MovimientosVMFactory(
            MovimientosInventarioRepository(db.movimientosInventarioDao()),
            InventarioSucursalRepository(db.inventarioSucursalDao()),
            FechasProductoRepository(db.fechasProductoDao())
        )
    }

    // Variables para almacenar las fechas seleccionadas por el usuario
    private var fechaFabricacionSelec: Long? = null
    private var fechaCaducidadSelec: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idInventario = arguments?.getInt("inventarioId") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movimientos, container, false)

        contenedorMovimientos = view.findViewById(R.id.contenedorMovimientos)
        titulo = view.findViewById(R.id.txtTituloMovimientos)

        // Botón volver
        val btnVolver = view.findViewById<ImageButton>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            findNavController().navigateUp() // Regresa a la vista anterior
        }

        // Botón agregar movimiento
        val fabAgregar = view.findViewById<FloatingActionButton>(R.id.fabAgregarMovimiento)
        fabAgregar.setOnClickListener {
            mostrarDialogoAgregarMovimiento()
        }

        mostrarNombreProducto()
        viewModel.cargarNombreProducto(idInventario)
        verMovimientos()

        return view
    }

    private fun mostrarNombreProducto() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nombreProducto.collect { nombre ->
                titulo.text = if (nombre != null) {
                    "Movimientos del producto: $nombre"
                } else {
                    "Movimientos del inventario #$idInventario"
                }
            }
        }
    }

    private fun verMovimientos() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel
                .obtenerMovimientos(idInventario)
                .collect { lista ->
                    contenedorMovimientos.removeAllViews()
                    contenedorMovimientos.addView(titulo)

                    if (lista.isEmpty()) {
                        agregarMensaje("No hay movimientos registrados.")
                    } else {
                        lista.forEach { movConDetalles ->
                            agregarItemMovimiento(movConDetalles)
                        }
                    }
                }
        }
    }

    private fun agregarItemMovimiento(movConDetalles: MovimientoConDetalles) {
        val card = layoutInflater.inflate(
            R.layout.item_movimiento,
            contenedorMovimientos,
            false
        )

        val tipo = card.findViewById<TextView>(R.id.txtTipo)
        val cantidad = card.findViewById<TextView>(R.id.txtCantidad)
        val sucursal = card.findViewById<TextView>(R.id.txtSucursal)
        val motivo = card.findViewById<TextView>(R.id.txtMotivo)
        val fechaFab = card.findViewById<TextView>(R.id.txtFechaFabricacion)
        val fechaVenc = card.findViewById<TextView>(R.id.txtFechaVencimiento)
        val fecha = card.findViewById<TextView>(R.id.txtFecha)
        val btnEliminar = card.findViewById<ImageButton>(R.id.btnEliminar)

        val mov = movConDetalles.movimiento

        // Botón Eliminar
        btnEliminar.setOnClickListener {
            mostrarDialogoConfirmarEliminacion(mov)
        }
        tipo.text = "Tipo: ${mov.tipoMovimiento}"
        cantidad.text = "Cantidad: ${mov.cantidad}"
        sucursal.text = "Sucursal: ${movConDetalles.nombreSucursal}"
        motivo.text = mov.motivo
        fecha.text = "Realizado el: ${convertirFecha(mov.fechaMovimiento)}, por: ${movConDetalles.nombreUsuario}"

        // Mostrar fechas solo si existen (osea solo para movimientos de INGRESO)
        if (movConDetalles.fechaFabricacion != null && movConDetalles.fechaVencimiento != null) {
            fechaFab.visibility = View.VISIBLE
            fechaVenc.visibility = View.VISIBLE
            fechaFab.text = "Fecha de Fabricación: ${convertirSoloFecha(movConDetalles.fechaFabricacion)}"
            fechaVenc.text = "Fecha de Vencimiento: ${convertirSoloFecha(movConDetalles.fechaVencimiento)}"
        } else {
            fechaFab.visibility = View.GONE
            fechaVenc.visibility = View.GONE
        }

        contenedorMovimientos.addView(card)
    }

    private fun convertirSoloFecha(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }

    private fun mostrarDialogoConfirmarEliminacion(mov: MovimientosInventario) {
        AlertDialog.Builder(requireContext(),R.style.EstiloDialog)
            .setTitle("Eliminar Movimiento")
            .setMessage("¿Está seguro de eliminar este movimiento?\n\nTipo: ${mov.tipoMovimiento}\nCantidad: ${mov.cantidad}\n\nEsta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarMovimiento(mov)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarMovimiento(mov: MovimientosInventario) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // almacenar el id del inventario para...
                val inventario = viewModel.obtenerInventarioPorId(mov.idInventario)
                if (inventario != null) {
                    //...reestablecer su stock si se elimina un movimiento, como revertir los cambios hechos
                    val nuevoStock = when (mov.tipoMovimiento) {
                        TipoMovimiento.INGRESO -> inventario.stockActual - mov.cantidad // si fue ingreso entonces se le resta al inventario
                        TipoMovimiento.SALIDA -> inventario.stockActual + mov.cantidad  // si fue salida entonces se le suma al inventario
                        TipoMovimiento.AJUSTE -> inventario.stockActual // si fue ajuste se mantiene el stock
                    }
                    viewModel.actualizarStock(mov.idInventario, nuevoStock)
                }
                // y ahora si se elimina el movimiento
                viewModel.eliminarMovimiento(mov)
                Toast.makeText(requireContext(), "Movimiento eliminado correctamente", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoAgregarMovimiento() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_agregar_movimiento, null)

        val spinnerTipo = dialogView.findViewById<Spinner>(R.id.spinnerTipoMovimiento)
        val etCantidad = dialogView.findViewById<EditText>(R.id.etCantidad)
        val etMotivo = dialogView.findViewById<EditText>(R.id.etMotivo)
        val contenedorFechas = dialogView.findViewById<LinearLayout>(R.id.contenedorFechas)
        val btnFechaFabricacion = dialogView.findViewById<Button>(R.id.btnFechaFabricacion)
        val btnFechaCaducidad = dialogView.findViewById<Button>(R.id.btnFechaCaducidad)

        // Reiniciar fechas
        fechaFabricacionSelec = null
        fechaCaducidadSelec = null

        // Configurar spinner con tipos de movimiento
        val tipos = arrayOf("INGRESO", "SALIDA", "AJUSTE")
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            tipos
        )
        adapter.setDropDownViewResource(R.layout.item_spinner)  // Layout para el dropdown
        spinnerTipo.adapter = adapter

        // Listener para mostrar/ocultar campos de fecha
        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (tipos[position] == "INGRESO") {
                    contenedorFechas.visibility = View.VISIBLE
                } else {
                    contenedorFechas.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Selector de fecha de fabricación
        btnFechaFabricacion.setOnClickListener {
            mostrarDatePicker { fechaSeleccionada ->
                fechaFabricacionSelec = fechaSeleccionada
                btnFechaFabricacion.text = convertirFecha(fechaSeleccionada)
            }
        }

        // Selector de fecha de caducidad
        btnFechaCaducidad.setOnClickListener {
            mostrarDatePicker { fechaSeleccionada ->
                fechaCaducidadSelec = fechaSeleccionada
                btnFechaCaducidad.text = convertirFecha(fechaSeleccionada)
            }
        }

        AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
            .setTitle("Registrar Movimiento")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val cantidadStr = etCantidad.text.toString()
                val motivo = etMotivo.text.toString()

                // Validaciones
                if (cantidadStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Ingrese una cantidad", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val cantidad = cantidadStr.toIntOrNull() ?: 0
                if (cantidad <= 0) {
                    Toast.makeText(requireContext(), "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val tipo = when(spinnerTipo.selectedItem.toString()) {
                    "INGRESO" -> TipoMovimiento.INGRESO
                    "SALIDA" -> TipoMovimiento.SALIDA
                    else -> TipoMovimiento.AJUSTE
                }

                val tipoSeleccionado = spinnerTipo.selectedItem.toString()
                // Validar fechas si es INGRESO
                if (tipoSeleccionado == "INGRESO") {
                    if (fechaFabricacionSelec == null || fechaCaducidadSelec == null) {
                        Toast.makeText(requireContext(), "Seleccione ambas fechas para el ingreso", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    if (fechaCaducidadSelec!! <= fechaFabricacionSelec!!) {
                        Toast.makeText(requireContext(), "La fecha de caducidad debe ser posterior a la de fabricación", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                }

                registrarMovimiento(tipo, cantidad, motivo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDatePicker(onFechaSeleccionada: (Long) -> Unit) {
        val calendario = Calendar.getInstance()
        val anio = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                calendario.set(anioSeleccionado, mesSeleccionado, diaSeleccionado, 0, 0, 0)
                onFechaSeleccionada(calendario.timeInMillis)
            },
            anio, mes, dia
        ).show()
    }
    private fun registrarMovimiento(tipo: TipoMovimiento, cantidad: Int, motivo: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            // Obtener datos del inventario
            val inventario = viewModel.obtenerInventarioPorId(idInventario)

            if (inventario == null) {
                Toast.makeText(requireContext(), "Error: Inventario no encontrado", Toast.LENGTH_SHORT).show()
                return@launch
            }

            var idFechasP: Int? = null

            // Si es INGRESO, insertar las fechas primero
            if (tipo == TipoMovimiento.INGRESO &&
                fechaFabricacionSelec != null &&
                fechaCaducidadSelec != null) {

                val fechasProducto = FechasProducto(
                    idProducto = inventario.idProducto,
                    fechaFabricacion = fechaFabricacionSelec!!,
                    fechaCaducidad = fechaCaducidadSelec!!
                )

                idFechasP = viewModel.insertarFechasProducto(fechasProducto).toInt()
            }

            val movimiento = MovimientosInventario(
                idInventario = idInventario,
                idProducto = inventario.idProducto,
                idSucursal = inventario.idSucursal,
                idUsuario = obtenerUsuarioActual(), // Método para obtener el ID del usuario logueado
                tipoMovimiento = tipo,
                cantidad = cantidad,
                idFechasP = idFechasP,
                motivo = motivo.ifEmpty { "Sin motivo especificado" },
                fechaMovimiento = System.currentTimeMillis()
            )

            viewModel.registrarMovimiento(movimiento)
            Toast.makeText(requireContext(), "Movimiento registrado exitosamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerUsuarioActual(): Int {
        // Aquí se debe obtener el ID del usuario logueado
        val sharedPref = requireActivity().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val usuarioId = sharedPref.getInt("idUsuario", -1)

        if (usuarioId == -1) {
            Toast.makeText(requireContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return 1 // poner al admin solo si hay error
        }

        return usuarioId
    }

    private fun agregarMensaje(texto: String) {
        val tv = TextView(requireContext())
        tv.text = texto
        tv.textSize = 16f
        contenedorMovimientos.addView(tv)
    }

    private fun convertirFecha(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}