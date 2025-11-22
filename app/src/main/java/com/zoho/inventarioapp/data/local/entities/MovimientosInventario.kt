package com.zoho.inventarioapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movimientos_inventario",
    foreignKeys = [
        ForeignKey(
            entity = Producto::class,  //Directo a Producto
            parentColumns = ["idProducto"],
            childColumns = ["idProducto"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Sucursal::class,  //Directo a Sucursal
            parentColumns = ["idSucursal"],
            childColumns = ["idSucursal"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = FechasProducto::class,
            parentColumns = ["idFechasP"],
            childColumns = ["idFechasP"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["idUsuario"],
            childColumns = ["idUsuario"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = InventarioSucursal::class,
            parentColumns = ["idInventario"],
            childColumns = ["idInventario"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("idProducto"),
        Index("idSucursal"),
        Index("idFechasP"),
        Index("idUsuario"),
        Index("fecha_movimiento"),
        Index("tipo_movimiento"),
        Index("idInventario")
    ]
)
data class MovimientosInventario(
    @PrimaryKey(autoGenerate = true)
    val idMovimiento: Int = 0,
    val idInventario: Int,
    @ColumnInfo(name = "tipo_movimiento")
    val tipoMovimiento: TipoMovimiento,
    @ColumnInfo(name = "idProducto")
    val idProducto: Int,  // Qué producto
    @ColumnInfo(name = "idSucursal")
    val idSucursal: Int,  // En qué sucursal
    @ColumnInfo(name = "idFechasP")
    val idFechasP: Int?,  // Con qué fechas de entrada y expiracion (solo para INGRESO)
    val cantidad: Int,
    @ColumnInfo(name = "idUsuario")
    val idUsuario: Int,
    val motivo: String?,
    @ColumnInfo(name = "fecha_movimiento")
    val fechaMovimiento: Long = System.currentTimeMillis()
)