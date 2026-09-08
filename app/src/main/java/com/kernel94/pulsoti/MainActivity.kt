package com.kernel94.pulsoti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kernel94.pulsoti.ui.navigation.NavGraph
import com.kernel94.pulsoti.ui.theme.PulsoTiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val container = (application as EncuestasOxxoApp).container

        setContent {
            PulsoTiTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavGraph(container = container)
                }
            }
        }

    }
}
