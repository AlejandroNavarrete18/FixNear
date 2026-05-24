package com.example.fixnearv1.modelo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fixnearv1.modelo.dao.ProductoDao
import com.example.fixnearv1.modelo.entity.Producto

@Database(
    entities = [Producto::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fixnear_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}