package com.example.fixnearv1.modelo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacante")
data class Vacante(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "empleador_id")
    val empleadorId: Int,
    @ColumnInfo(name = "categoria_id")
    val categoriaId: Int,
    val titulo: String,
    val descripcion: String,
    val ubicacion: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val salario: Double? = null,
    @ColumnInfo(name = "tipo_contrato")
    val tipoContrato: String = "tiempo_completo",
    val estado: String = "activa",
    @ColumnInfo(name = "fecha_publicacion")
    val fechaPublicacion: String = "",
    @ColumnInfo(name = "fecha_cierre")
    val fechaCierre: String? = null
)
