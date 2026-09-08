package com.kernel94.pulsoti.data.local.dao

import androidx.room.*
import com.kernel94.pulsoti.data.local.entities.CuestionarioEntity
import com.kernel94.pulsoti.data.local.entities.PreguntaEntity

@Dao
interface CuestionarioDao {
    @Query("SELECT * FROM cuestionario_cache WHERE plazaId = :plazaId LIMIT 1")
    suspend fun obtenerPorPlaza(plazaId: Int): CuestionarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(cuestionario: CuestionarioEntity)

    @Query("SELECT * FROM pregunta_cache WHERE cuestionarioId = :cuestionarioId ORDER BY orden")
    suspend fun obtenerPreguntas(cuestionarioId: Int): List<PreguntaEntity>

    @Query("DELETE FROM pregunta_cache WHERE cuestionarioId = :cuestionarioId")
    suspend fun borrarPreguntasDe(cuestionarioId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarPreguntas(preguntas: List<PreguntaEntity>)
}
