package mx.com.getic.encuestasoxxo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "respuesta_detalle")
data class RespuestaDetalleEntity(
    @PrimaryKey val id: String, // uuid, igual que encuesta.id
    val encuestaId: String,
    val preguntaId: Int,
    val calificacion: Int // 1-10, escala NPS: 1-6 detractor, 7-8 pasivo, 9-10 promotor
)
