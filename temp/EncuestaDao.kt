package mx.com.getic.encuestasoxxo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mx.com.getic.encuestasoxxo.data.local.entities.EncuestaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.RespuestaDetalleEntity

@Dao
interface EncuestaDao {
    @Insert
    suspend fun guardarEncuesta(encuesta: EncuestaEntity)

    @Insert
    suspend fun guardarRespuestas(respuestas: List<RespuestaDetalleEntity>)

    @Query("SELECT * FROM encuesta WHERE sincronizado = 0")
    suspend fun pendientesDeSincronizar(): List<EncuestaEntity>

    @Query("SELECT * FROM respuesta_detalle WHERE encuesta_id = :encuestaId")
    suspend fun respuestasDe(encuestaId: String): List<RespuestaDetalleEntity>

    @Query("UPDATE encuesta SET sincronizado = 1 WHERE id = :encuestaId")
    suspend fun marcarSincronizada(encuestaId: String)

    // Observar cantidad de encuestas pendientes como Flow para actualizaciones en tiempo real
    @Query("SELECT COUNT(*) FROM encuesta WHERE sincronizado = 0")
    fun contarPendientes(): Flow<Int>
}
