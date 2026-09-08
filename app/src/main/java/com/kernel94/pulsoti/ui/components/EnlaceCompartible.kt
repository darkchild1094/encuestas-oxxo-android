package com.kernel94.pulsoti.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Tarjeta con un enlace de solo lectura + botones "Copiar enlace" y
 * "Compartir" (hoja nativa de Android). Pensada para el enlace publico
 * del cuestionario web de la encuesta de oficina (secciones Preguntas y
 * Areas), pero es generica -- sirve para cualquier URL que el ATI
 * necesite pasarle a alguien fuera de la app.
 */
@Composable
fun EnlaceCompartible(
    titulo: String,
    url: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(titulo, style = MaterialTheme.typography.titleSmall)
            Text(
                url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { copiarAlPortapapeles(context, url) }) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Copiar enlace")
                }
                Button(onClick = { compartirEnlace(context, url) }) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Compartir")
                }
            }
        }
    }
}

private fun copiarAlPortapapeles(context: Context, url: String) {
    val portapapeles = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    portapapeles.setPrimaryClip(ClipData.newPlainText("Enlace de la encuesta", url))
    // En Android 13+ el sistema ya muestra su propio aviso al copiar;
    // este Toast solo se ve en versiones anteriores, pero no estorba.
    Toast.makeText(context, "Enlace copiado", Toast.LENGTH_SHORT).show()
}

private fun compartirEnlace(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir encuesta de oficina"))
}
