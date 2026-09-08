package com.kernel94.pulsoti.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.kernel94.pulsoti.data.local.entities.EncuestaEntity
import com.kernel94.pulsoti.data.local.entities.RespuestaDetalleEntity

@Dao
interface EncuestaDao {
    @Insert
    suspend fun guardarEncuesta(encuesta: EncuestaEntity)

    @Insert
    suspend fun guardarRespuestas(respuestas: List<RespuestaDetalleEntity>)

    // Guardado atomico: la cabecera y sus respuestas entran en una sola
    // transaccion. Si el SO mata el proceso a media escritura, o entran
    // las dos o no entra ninguna -- nunca una encuesta sin respuestas
    // (que luego se sincronizaba incompleta al servidor).
    @Transaction
    suspend fun guardarEncuestaCompleta(
        encuesta: EncuestaEntity,
        respuestas: List<RespuestaDetalleEntity>,
    ) {
        guardarEncuesta(encuesta)
        guardarRespuestas(respuestas)
    }

    // Borra una encuesta previa (sin folio) y sus respuestas en una sola
    // transaccion, para dar prioridad a una nueva que si trae folio.
    @Transaction
    suspend fun reemplazarPrevia(previa: EncuestaEntity) {
        borrarRespuestasDe(previa.id)
        borrarEncuesta(previa)
    }

    @Query("SELECT * FROM encuesta WHERE sincronizado = 0")
    suspend fun pendientesDeSincronizar(): List<EncuestaEntity>

    @Query("SELECT * FROM respuesta_detalle WHERE encuestaId = :encuestaId")
    suspend fun respuestasDe(encuestaId: String): List<RespuestaDetalleEntity>

    @Query("UPDATE encuesta SET sincronizado = 1 WHERE id = :encuestaId")
    suspend fun marcarSincronizada(encuestaId: String)

    @Query("SELECT COUNT(*) FROM encuesta WHERE sincronizado = 0")
    fun contarPendientes(): Flow<Int>

    // Para el historial que ve el ATI dentro de la app (version simple;
    // el detalle filtrado/exportable vive en el panel web).
    @Query("SELECT * FROM encuesta ORDER BY fechaCreacionLocal DESC")
    suspend fun todas(): List<EncuestaEntity>

    @Query("SELECT * FROM encuesta WHERE tiendaId = :tiendaId ORDER BY fechaCreacionLocal DESC LIMIT 1")
    suspend fun ultimaEncuestaDeTienda(tiendaId: Int): EncuestaEntity?

    @Delete
    suspend fun borrarEncuesta(encuesta: EncuestaEntity)

    @Query("DELETE FROM respuesta_detalle WHERE encuestaId = :encuestaId")
    suspend fun borrarRespuestasDe(encuestaId: String)
}
