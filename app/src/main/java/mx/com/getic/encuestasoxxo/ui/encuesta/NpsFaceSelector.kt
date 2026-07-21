package mx.com.getic.encuestasoxxo.ui.encuesta

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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas

// Colores identicos al ejemplo: 1-6 detractor (rojo), 7-8 pasivo
// (amarillo), 9-10 promotor (verde).
private fun colorPara(numero: Int): Color = when {
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
    // Dos filas de 5 caritas, repartidas a lo ancho de la pantalla: se ve
    // completo sin necesidad de deslizar, sin importar el tamaño del equipo.
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            for (numero in 1..5) {
                CaritaNps(
                    numero = numero,
                    seleccionada = seleccion == numero,
                    onClick = { onSeleccionar(numero) },
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            for (numero in 6..10) {
                CaritaNps(
                    numero = numero,
                    seleccionada = seleccion == numero,
                    onClick = { onSeleccionar(numero) },
                )
            }
        }
    }
}

@Composable
private fun CaritaNps(numero: Int, seleccionada: Boolean, onClick: () -> Unit) {
    val color = colorPara(numero)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .selectable(selected = seleccionada, onClick = onClick)
            .padding(2.dp),
    ) {
        Canvas(
            modifier = Modifier
                .size(40.dp)
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
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(w / 2 - mouthHalfWidth, mouthY)
                quadraticBezierTo(w / 2, mouthY + curvatura, w / 2 + mouthHalfWidth, mouthY)
            }
            drawPath(path, color = ojoColor, style = Stroke(width = w * 0.045f))
        }
        Text(
            text = numero.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = if (seleccionada) color else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
