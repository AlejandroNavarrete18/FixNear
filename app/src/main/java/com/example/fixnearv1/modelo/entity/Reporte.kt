package com.example.fixnearv1.modelo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reporte")
data class Reporte(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "reportante_id")
    val reportanteId: Int,
    @ColumnInfo(name = "reportado_id")
    val reportadoId: Int,
    val motivo: String,
    val descripcion: String? = null,
    val estado: String = "pendiente",
    val fecha: String = ""
)
