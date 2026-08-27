package mx.com.getic.encuestasoxxo.ui.encuesta

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

// Colores identicos al ejemplo: 1-6 detractor (rojo), 7-8 pasivo
// (amarillo), 9-10 promotor (verde).
internal fun colorParaNps(numero: Int): Color = when {
    numero <= 6 -> Color(0xFFDA3E64)
    numero <= 8 -> Color(0xFFF5B913)
    else -> Color(0xFF3AAE7A)
}

@Composable
fun NpsFaceSelector(
    seleccion: Int?,
    onSeleccionar: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Una sola fila de 10 caritas. El modifier.weight(1f) en cada elemento
    // asegura que todas se repartan el ancho de la pantalla en partes iguales.
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp), // Pequeño espacio entre caritas
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (numero in 1..10) {
            CaritaNps(
                numero = numero,
                seleccionada = seleccion == numero,
                onClick = { onSeleccionar(numero) },
                modifier = Modifier.weight(1f) // Obliga a distribuir equitativamente el ancho
            )
        }
    }
}

@Composable
private fun CaritaNps(
    numero: Int,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = colorParaNps(numero)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .selectable(selected = seleccionada, onClick = onClick)
            .padding(vertical = 4.dp), // Solo padding vertical, el horizontal lo maneja Arrangement.spacedBy
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth() // Toma todo el ancho que le da el weight de su padre
                .aspectRatio(1f) // Se mantiene como un cuadrado perfecto dinámicamente
                .clip(CircleShape)
                .background(if (seleccionada) color else color.copy(alpha = 0.35f))
        ) {
            val w = size.width
            val h = size.height
            val eyeY = h * 0.4f
            val eyeOffsetX = w * 0.22f
            val eyeRadius = w * 0.045f
            val ojoColor = Color.White

            drawCircle(ojoColor, radius = eyeRadius, center = Offset(w / 2 - eyeOffsetX, eyeY))
            drawCircle(ojoColor, radius = eyeRadius, center = Offset(w / 2 + eyeOffsetX, eyeY))

            val mouthY = h * 0.62f
            val mouthHalfWidth = w * 0.22f
            val curvatura = when {
                numero <= 6 -> -h * 0.12f  // triste: arco hacia arriba en los extremos
                numero <= 8 -> 0f           // neutral: linea recta
                else -> h * 0.12f           // feliz: arco hacia abajo en los extremos
            }
            val path = Path().apply {
                moveTo(w / 2 - mouthHalfWidth, mouthY)
                quadraticBezierTo(w / 2, mouthY + curvatura, w / 2 + mouthHalfWidth, mouthY)
            }
            drawPath(path, color = ojoColor, style = Stroke(width = w * 0.045f))
        }

        Spacer(modifier = Modifier.height(4.dp)) // Espacio entre el círculo y el número

        Text(
            text = numero.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = if (seleccionada) color else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}