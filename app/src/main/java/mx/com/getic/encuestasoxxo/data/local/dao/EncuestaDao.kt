package mx.com.getic.encuestasoxxo.data.local.dao

import androidx.room.*
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

    @Query("SELECT * FROM respuesta_detalle WHERE encuestaId = :encuestaId")
    suspend fun respuestasDe(encuestaId: String): List<RespuestaDetalleEntity>

    @Query("UPDATE encuesta SET sincronizado = 1 WHERE id = :encuestaId")
    suspend fun marcarSincronizada(encuestaId: String)

    // Para el historial que ve el ATI dentro de la app (version simple;
    // el detalle filtrado/exportable vive en el panel web).
    @Query("SELECT * FROM encuesta ORDER BY fechaCreacionLocal DESC")
    suspend fun todas(): List<EncuestaEntity>
}
