package com.zoho.inventarioapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    foreignKeys = [
        ForeignKey(
            entity = Rol::class,
            parentColumns = ["idRol"],
            childColumns = ["idRol"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Sucursal::class,
            parentColumns = ["idSucursal"],
            childColumns = ["idSucursal"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index("idRol"),
        Index("idSucursal"),
        Index(value = ["correo"], unique = true),
        Index(value = ["codUsuario"], unique = true)
    ]
)

data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val idUsuario: Int = 0,
    @ColumnInfo(name = "codUsuario")
    val codUsuario: String,
    val nombre: String,
    val correo: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "idRol")
    val idRol: Int,
    @ColumnInfo(name = "idSucursal")
    val idSucursal: Int?
)