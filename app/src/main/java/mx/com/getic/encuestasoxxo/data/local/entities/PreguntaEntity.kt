package mx.com.getic.encuestasoxxo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Solo preguntas activas -- el soft-delete (activo=0) del lado
// PHP nunca las manda en /api/cuestionario, asi que aqui no hace
// falta ni guardar esa bandera.
@Entity(tableName = "pregunta_cache")
data class PreguntaEntity(
    @PrimaryKey val id: Int,
    val cuestionarioId: Int,
    val texto: String,
    val orden: Int
)
