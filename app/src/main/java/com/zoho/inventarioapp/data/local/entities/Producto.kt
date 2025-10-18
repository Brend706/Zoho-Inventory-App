package com.zoho.inventarioapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "productos",
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["idCategoria"],
            childColumns = ["idCategoria"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("idCategoria"),
        Index(value = ["codProducto"], unique = true)
    ]
)
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val idProducto: Int = 0,
    @ColumnInfo(name = "codProducto")
    val codProducto: String,
    val nombre: String,
    @ColumnInfo(name = "idCategoria")
    val idCategoria: Int,
    @ColumnInfo(name = "stockMinimo")
    val stockMinimo: Int,
    @ColumnInfo(name = "fechaCreacion")
    val fechaCreacion: Long = System.currentTimeMillis()
)