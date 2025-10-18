package com.zoho.inventarioapp.data.local.database

import androidx.room.TypeConverter
import com.zoho.inventarioapp.data.local.entities.TipoMovimiento

class Converters {

    @TypeConverter
    fun fromTipoMovimiento(value: TipoMovimiento): String {
        return value.name
    }

    @TypeConverter
    fun toTipoMovimiento(value: String): TipoMovimiento {
        return TipoMovimiento.valueOf(value)
    }
}