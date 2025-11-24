package com.zoho.inventarioapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zoho.inventarioapp.data.local.dao.*
import com.zoho.inventarioapp.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        Rol::class,
        Sucursal::class,
        Categoria::class,
        Usuario::class,
        Producto::class,
        FechasProducto::class,
        InventarioSucursal::class,
        MovimientosInventario::class
    ],
    version = 4, //cambion de version de bd
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rolDao(): RolDao
    abstract fun sucursalDao(): SucursalDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun fechasProductoDao(): FechasProductoDao
    abstract fun inventarioSucursalDao(): InventarioSucursalDao
    abstract fun movimientosInventarioDao(): MovimientosInventarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Crear un scope específico para la base de datos
        private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        // controla el tiempo de vida de las tareas asíncronas (coroutines)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventario_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }

        //llama a la insercion de datos despues de crear la base de datos en el scope
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    applicationScope.launch {
                        insertarDatosIniciales(database)
                    }
                }
            }
        }

        // recibe la instancia de la BD para insertarle los datos
        private suspend fun insertarDatosIniciales(database: AppDatabase) {
            // 1. Insertar roles
            database.rolDao().insertarTodos(listOf(
                Rol(tipoRol = "Administrador"),
                Rol(tipoRol = "Empleado")
            ))

            // 2. Insertar sucursales
            val idSucursalCentral = database.sucursalDao().insertar(
                Sucursal(
                    nombre = "Sucursal Central",
                    direccion = "San Salvador, El Salvador",
                    telefono = "2222-2222"
                )
            )
            val idSucursalNorte = database.sucursalDao().insertar(
                Sucursal(
                    nombre = "Sucursal Norte",
                    direccion = "Santa Ana, El Salvador",
                    telefono = "2444-5555"
                )
            )

            // 3. Insertar usuario Admin (sin sucursal asignada)
            database.usuarioDao().insertar(
                Usuario(
                    codUsuario = "ADMIN001",
                    nombre = "Administrador",
                    correo = "admin@zoho.com",
                    password = "admin123",
                    idRol = 1,
                    idSucursal = null
                )
            )

            // 4. Insertar usuario Empleado (asignado a Sucursal Norte)
            database.usuarioDao().insertar(
                Usuario(
                    codUsuario = "EMP001",
                    nombre = "Juan Pérez",
                    correo = "juan.perez@pastella.com",
                    password = "emp123",
                    idRol = 2,  // Rol Empleado
                    idSucursal = idSucursalNorte.toInt()  // Asignado a Sucursal Norte
                )
            )

            // 5. Insertar categorías para la pastelería
            val idCategoriaHarinas = database.categoriaDao().insertar(
                Categoria(
                    nombre = "Harinas y Cereales",
                )
            )
            val idCategoriaLacteos = database.categoriaDao().insertar(
                Categoria(
                    nombre = "Lácteos",
                )
            )
            val idCategoriaEndulzantes = database.categoriaDao().insertar(
                Categoria(
                    nombre = "Endulzantes",
                )
            )

            // 6. Insertar productos por categoría
            val idProductoHarina = database.productoDao().insertar(
                Producto(
                    codProducto = "PROD001",
                    nombre = "Harina de Trigo Todo Uso",
                    idCategoria = idCategoriaHarinas.toInt(),
                    stockMinimo = 20
                )
            )

            val idProductoMantequilla = database.productoDao().insertar(
                Producto(
                    codProducto = "PROD002",
                    nombre = "Mantequilla sin Sal",
                    idCategoria = idCategoriaLacteos.toInt(),
                    stockMinimo = 15
                )
            )

            val idProductoAzucar = database.productoDao().insertar(
                Producto(
                    codProducto = "PROD003",
                    nombre = "Azúcar Blanca Refinada",
                    idCategoria = idCategoriaEndulzantes.toInt(),
                    stockMinimo = 25
                )
            )
        }
    }
}