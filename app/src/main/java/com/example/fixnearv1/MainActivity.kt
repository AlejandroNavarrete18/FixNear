package com.example.fixnearv1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fixnearv1.modelo.AppBaseDatos
import com.example.fixnearv1.modelo.repository.MovimientoRepository
import com.example.fixnearv1.modelo.repository.ProductoRepository
import com.example.fixnearv1.modelo.repository.UsuarioRepository
import com.example.fixnearv1.navigation.AppNavigation
import com.example.fixnearv1.ui.theme.FixNearV1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppBaseDatos.getDatabase(applicationContext)
        val usuarioRepo = UsuarioRepository(db.usuarioDao())
        val productoRepo = ProductoRepository(db.productoDao())
        val movimientoRepo = MovimientoRepository(db.movimientoDao())

        setContent {
            FixNearV1Theme {
                AppNavigation(
                    usuarioRepository = usuarioRepo,
                    productoRepository = productoRepo,
                    movimientoRepository = movimientoRepo
                )
            }
        }
    }
}
