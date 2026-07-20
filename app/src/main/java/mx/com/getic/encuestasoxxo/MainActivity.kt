package mx.com.getic.encuestasoxxo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import mx.com.getic.encuestasoxxo.ui.navigation.NavGraph
import mx.com.getic.encuestasoxxo.ui.theme.PulsoTiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
