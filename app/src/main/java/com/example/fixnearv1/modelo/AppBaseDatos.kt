package com.example.fixnearv1.modelo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fixnearv1.modelo.dao.*
import com.example.fixnearv1.modelo.entity.*

@Database(
    entities = [
        Usuario::class,
        Categoria::class,
        Servicio::class,
        Vacante::class,
        Postulacion::class,
        Contratacion::class,
        Producto::class,
        Movimiento::class,
        Calificacion::class,
        Reporte::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppBaseDatos : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun servicioDao(): ServicioDao
    abstract fun vacanteDao(): VacanteDao
    abstract fun postulacionDao(): PostulacionDao
    abstract fun contratacionDao(): ContratacionDao
    abstract fun productoDao(): ProductoDao
    abstract fun movimientoDao(): MovimientoDao
    abstract fun calificacionDao(): CalificacionDao
    abstract fun reporteDao(): ReporteDao

    companion object {

        @Volatile
        private var INSTANCE: AppBaseDatos? = null

        fun getDatabase(context: Context): AppBaseDatos {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppBaseDatos::class.java,
                    "clickwork_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
