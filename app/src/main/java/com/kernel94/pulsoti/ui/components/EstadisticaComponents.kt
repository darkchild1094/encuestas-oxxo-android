package com.kernel94.pulsoti.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

/**
 * Piezas compactas compartidas por los dashboards (ATI y WEBMASTER):
 * chips de KPI, encabezado de sección y fila de ranking, todo pensado
 * para caber mucho en poco espacio vertical (la version anterior con
 * tarjetas grandes + circulo animado por fila se sentia "enorme").
 */

fun colorParaPromedio(promedio: Double): Color = when {
    promedio >= 9.0 -> Color(0xFF2E7D32)
    promedio >= 7.0 -> Color(0xFFB8860B)
    else -> Color(0xFFC62828)
}

@Composable
fun KpiChip(
    icono: ImageVector,
    color: Color,
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        androidx.compose.foundation.layout.Column {
            Text(
                valor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
            )
            Text(
                etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun SeccionCompacta(titulo: String, icono: ImageVector, color: Color, total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
        Text(titulo, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        if (total > 0) {
            Text(
                "· $total",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun FilaRanking(texto: String, promedio: Double, totalEncuestas: Int) {
    val color = colorParaPromedio(promedio)
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                String.format(Locale.getDefault(), "%.1f", promedio),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 10.sp,
            )
        }
        Text(
            texto,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            "$totalEncuestas",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
