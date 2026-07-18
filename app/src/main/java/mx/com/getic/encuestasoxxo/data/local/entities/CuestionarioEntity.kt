package mx.com.getic.encuestasoxxo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Cache local del cuestionario activo de una plaza. Se refresca cada
// vez que hay señal (ver EncuestaRepository.refrescarCuestionario).
@Entity(tableName = "cuestionario_cache")
data class CuestionarioEntity(
    @PrimaryKey val id: Int,
    val plazaId: Int,
    val nombre: String
)
