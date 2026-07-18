package mx.com.getic.encuestasoxxo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// id es un UUID generado EN EL DISPOSITIVO al momento de contestar
// (ver EncuestaRepository.nuevaEncuesta) -- asi nace con identidad
// propia sin depender del servidor, que es lo que hace posible el
// modo offline sin colisiones entre tecnicos distintos.
@Entity(tableName = "encuesta")
data class EncuestaEntity(
    @PrimaryKey val id: String,
    val usuarioId: Int,
    val tiendaId: Int,
    val cuestionarioId: Int,
    val comentario: String?,
    val fechaCreacionLocal: String, // formato "yyyy-MM-dd HH:mm:ss", mismo que espera MariaDB
    val sincronizado: Boolean = false
)
