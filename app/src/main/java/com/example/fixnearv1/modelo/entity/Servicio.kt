package com.example.fixnearv1.modelo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servicio")
data class Servicio(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "trabajador_id")
    val trabajadorId: Int,
    @ColumnInfo(name = "categoria_id")
    val categoriaId: Int,
    val titulo: String,
    val descripcion: String? = null,
    @ColumnInfo(name = "precio_base")
    val precioBase: Double = 0.0,
    @ColumnInfo(name = "modalidad_precio")
    val modalidadPrecio: String = "a_convenir",
    val disponibilidad: String? = null,
    val activo: Int = 1,
    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: String = ""
)
