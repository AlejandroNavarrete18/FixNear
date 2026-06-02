package com.example.fixnearv1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fixnearv1.navigation.AppNavigation

class MainActivity : ComponentActivity() {

    // Guardamos el Intent que abrió la app (o null)
    companion object {
        var pendingIntent: Intent? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Capturamos el deep link si la app arrancó desde el callback de OAuth
        pendingIntent = intent

        setContent {
            AppNavigation()
        }
    }

    // También capturamos si ya estaba corriendo y volvió desde el navegador
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pendingIntent = intent
    }
}
