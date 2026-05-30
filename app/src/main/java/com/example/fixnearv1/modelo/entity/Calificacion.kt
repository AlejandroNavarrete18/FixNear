package com.example.fixnearv1.modelo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calificacion")
data class Calificacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "contratacion_id")
    val contratacionId: Int,
    @ColumnInfo(name = "calificador_id")
    val calificadorId: Int,
    @ColumnInfo(name = "calificado_id")
    val calificadoId: Int,
    val puntuacion: Int,
    val comentario: String? = null,
    val fecha: String = ""
)
