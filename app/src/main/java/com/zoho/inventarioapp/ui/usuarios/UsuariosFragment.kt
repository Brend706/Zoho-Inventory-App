package com.zoho.inventarioapp.ui.usuarios

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.zoho.inventarioapp.R
//Importamos los atributos de la tabla Usuarios
import com.zoho.inventarioapp.data.local.entities.Usuario
import kotlinx.coroutines.launch
import java.util.*

class UsuariosFragment : Fragment() {

    private val viewModel: UsuariosViewModel by viewModels()
    private lateinit var contenedorUsuarios: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_usuarios, container, false)
        contenedorUsuarios = view.findViewById(R.id.contenedorUsuarios)

        view.findViewById<Button>(R.id.btnCrearUsuario).setOnClickListener {
            mostrarDialogoCrearUsuario()
        }

        observarUsuarios()
        return view
    }

    private fun observarUsuarios() {
        lifecycleScope.launch {
            viewModel.todosLosUsuarios.collect { lista ->
                contenedorUsuarios.removeAllViews()
                lista.forEach { usuario ->
                    crearCardUsuario(usuario)
                }
            }
        }
    }

    //Esta parte ocupa el item_usuario, fragment_usuario y dialog_usuario

    //Item_usurio es el modelo que tiene que tener cada card para cada usuario
    //fragment_usuario es la representacion del contenedor donde tiene que ir cada item_usuario
    //aparte ahi te define el boton crear (no encontre una manera de hacerlo en un solo archivo asique lo hice asi)
    //dialog_usuario es como los modals de mvc, es lo que vemos cuando le damos a crear o editar, y
    //ahi esta el diseño de esa parte
    private fun crearCardUsuario(usuario: Usuario) {
        val card = layoutInflater.inflate(R.layout.item_usuario, contenedorUsuarios, false)

        // Referencias a los elementos del CardView
        val tvNombre = card.findViewById<TextView>(R.id.tvNombreUsuario)
        val tvCorreo = card.findViewById<TextView>(R.id.tvCorreoUsuario)
        val btnEditar = card.findViewById<Button>(R.id.btnEditar)
        val btnEliminar = card.findViewById<Button>(R.id.btnEliminar)

        // Asignamos los datos del usuario
        tvNombre.text = "Nombre: ${usuario.nombre}"
        tvCorreo.text = "Correo: ${usuario.correo}"

        // Listeners
        btnEditar.setOnClickListener { mostrarDialogoEditarUsuario(usuario) }
        btnEliminar.setOnClickListener {
            eliminarUsuarioConConfirmacion(usuario)
        }

        // Agregamos el CardView al contenedor
        contenedorUsuarios.addView(card)
    }

    private fun mostrarDialogoCrearUsuario() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_usuario, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etCorreo = dialogView.findViewById<EditText>(R.id.etCorreo)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)
        val spRol = dialogView.findViewById<Spinner>(R.id.spRol)
        val spSucursal = dialogView.findViewById<Spinner>(R.id.spSucursal)

        // El campo de contraseña se muestra en creación
        etPassword.visibility = View.VISIBLE

        // Cargar datos desde la BD
        viewModel.obtenerRoles()
        viewModel.obtenerSucursales()

        lifecycleScope.launch {
            viewModel.roles.collect { roles ->
                val listaRoles = mutableListOf("Selecciona un rol")
                listaRoles.addAll(roles.map { it.tipoRol })
                val adapterRoles = ArrayAdapter(
                    requireContext(),
                    R.layout.item_spinner,
                    listaRoles
                )
                adapterRoles.setDropDownViewResource(R.layout.item_spinner)
                spRol.adapter = adapterRoles
            }
        }

        lifecycleScope.launch {
            viewModel.sucursales.collect { sucursales ->
                val listaSucursales = mutableListOf("Selecciona una sucursal (opcional)")
                listaSucursales.addAll(sucursales.map { it.nombre })
                val adapterSucursales = ArrayAdapter(
                    requireContext(),
                    R.layout.item_spinner,
                    listaSucursales
                )
                adapterSucursales.setDropDownViewResource(R.layout.item_spinner)
                spSucursal.adapter = adapterSucursales
            }
        }

        val builder = AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
        builder.setTitle("Crear Usuario")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val correo = etCorreo.text.toString().trim()
                val password = etPassword.text.toString().trim()
                val rolSeleccionado = spRol.selectedItemPosition
                val sucursalSeleccionada = spSucursal.selectedItemPosition

                if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (rolSeleccionado == 0) {
                    Toast.makeText(requireContext(), "Selecciona un rol válido", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val idRol = viewModel.roles.value.getOrNull(rolSeleccionado - 1)?.idRol ?: 1
                val idSucursal = if (sucursalSeleccionada > 0)
                    viewModel.sucursales.value.getOrNull(sucursalSeleccionada - 1)?.idSucursal
                else null

                val nuevoUsuario = Usuario(
                    codUsuario = UUID.randomUUID().toString(),
                    nombre = nombre,
                    correo = correo,
                    password = password,
                    idRol = idRol,
                    idSucursal = idSucursal
                )

                viewModel.agregarUsuario(nuevoUsuario)
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

    private fun mostrarDialogoEditarUsuario(usuario: Usuario) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_usuario, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etCorreo = dialogView.findViewById<EditText>(R.id.etCorreo)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)
        val spRol = dialogView.findViewById<Spinner>(R.id.spRol)
        val spSucursal = dialogView.findViewById<Spinner>(R.id.spSucursal)

        //Esto es para que no se vea el campo de Contraseña cuando sea edicion
        etPassword.visibility = View.GONE

        //Se cargan los datos de nombre y correo del usuario cuando sea edicion
        etNombre.setText(usuario.nombre)
        etCorreo.setText(usuario.correo)

        // Cargar datos desde la BD
        viewModel.obtenerRoles()
        viewModel.obtenerSucursales()

        lifecycleScope.launch {
            viewModel.roles.collect { roles ->
                val listaRoles = mutableListOf("Selecciona un rol")
                listaRoles.addAll(roles.map { it.tipoRol })
                val adapterRoles = ArrayAdapter(
                    requireContext(),
                    R.layout.item_spinner,
                    listaRoles
                )
                adapterRoles.setDropDownViewResource(R.layout.item_spinner)
                spRol.adapter = adapterRoles

                // Seleccionar el rol actual
                val indiceRol = roles.indexOfFirst { it.idRol == usuario.idRol }
                if (indiceRol != -1) spRol.setSelection(indiceRol + 1)
            }
        }

        lifecycleScope.launch {
            viewModel.sucursales.collect { sucursales ->
                val listaSucursales = mutableListOf("Selecciona una sucursal (opcional)")
                listaSucursales.addAll(sucursales.map { it.nombre })
                val adapterSucursales = ArrayAdapter(
                    requireContext(),
                    R.layout.item_spinner,
                    listaSucursales
                )
                adapterSucursales.setDropDownViewResource(R.layout.item_spinner)
                spSucursal.adapter = adapterSucursales

                // Seleccionar la sucursal actual (si tiene)
                val indiceSucursal = sucursales.indexOfFirst { it.idSucursal == usuario.idSucursal }
                if (indiceSucursal != -1) spSucursal.setSelection(indiceSucursal + 1)
            }
        }

        val builder = AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
        builder.setTitle("Editar Usuario")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val correo = etCorreo.text.toString().trim()
                val rolSeleccionado = spRol.selectedItemPosition
                val sucursalSeleccionada = spSucursal.selectedItemPosition

                if (nombre.isEmpty() || correo.isEmpty()) {
                    Toast.makeText(requireContext(), "Nombre y correo son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (rolSeleccionado == 0) {
                    Toast.makeText(requireContext(), "Selecciona un rol válido", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val idRol = viewModel.roles.value.getOrNull(rolSeleccionado - 1)?.idRol ?: usuario.idRol
                val idSucursal = if (sucursalSeleccionada > 0)
                    viewModel.sucursales.value.getOrNull(sucursalSeleccionada - 1)?.idSucursal
                else null

                val usuarioActualizado = usuario.copy(
                    nombre = nombre,
                    correo = correo,
                    idRol = idRol,
                    idSucursal = idSucursal
                )

                viewModel.editarUsuario(usuarioActualizado)
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

    private fun eliminarUsuarioConConfirmacion(usuario: Usuario) {
        val dialog = AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de que deseas eliminar a ${usuario.nombre}?")
            .setPositiveButton("Sí") { _, _ ->
                viewModel.eliminarUsuario(usuario)
            }
            .setNegativeButton("Cancelar", null)
            .create()
 
        dialog.show()

        // Botones personalizados
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            setTextColor(resources.getColor(R.color.morado_mas_suave))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
            setTextColor(resources.getColor(R.color.morado_mas_suave))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }

}
