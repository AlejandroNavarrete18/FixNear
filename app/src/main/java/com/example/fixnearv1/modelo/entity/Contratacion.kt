package com.example.fixnearv1.modelo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contratacion")
data class Contratacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "servicio_id")
    val servicioId: Int,
    @ColumnInfo(name = "cliente_id")
    val clienteId: Int,
    @ColumnInfo(name = "trabajador_id")
    val trabajadorId: Int,
    @ColumnInfo(name = "descripcion_trabajo")
    val descripcionTrabajo: String? = null,
    @ColumnInfo(name = "fecha_inicio")
    val fechaInicio: String,
    @ColumnInfo(name = "fecha_fin")
    val fechaFin: String? = null,
    @ColumnInfo(name = "monto_total")
    val montoTotal: Double = 0.0,
    val estado: String = "pendiente",
    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: String = ""
)
