package mx.com.getic.encuestasoxxo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import mx.com.getic.encuestasoxxo.data.local.entities.EncuestaSyncLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EncuestaSyncLogDao {
    
    @Insert
    suspend fun insertar(log: EncuestaSyncLogEntity): Long
    
    @Update
    suspend fun actualizar(log: EncuestaSyncLogEntity)
    
    @Query("SELECT * FROM encuesta_sync_log WHERE encuesta_id = :encuestaId ORDER BY fecha_intento DESC LIMIT 1")
    suspend fun obtenerUltimoPorEncuesta(encuestaId: String): EncuestaSyncLogEntity?
    
    @Query("SELECT * FROM encuesta_sync_log WHERE encuesta_id = :encuestaId")
    fun obtenerHistorialPorEncuesta(encuestaId: String): Flow<List<EncuestaSyncLogEntity>>
    
    @Query("SELECT * FROM encuesta_sync_log WHERE handshake_id = :handshakeId LIMIT 1")
    suspend fun obtenerPorHandshake(handshakeId: String): EncuestaSyncLogEntity?
    
    @Query("SELECT * FROM encuesta_sync_log WHERE estado = :estado ORDER BY fecha_intento DESC")
    fun obtenerPorEstado(estado: String): Flow<List<EncuestaSyncLogEntity>>
    
    @Query("""
        SELECT * FROM encuesta_sync_log 
        WHERE estado != 'exito'
        ORDER BY fecha_intento DESC
    """)
    fun obtenerPendientes(): Flow<List<EncuestaSyncLogEntity>>
    
    @Query("DELETE FROM encuesta_sync_log WHERE encuesta_id = :encuestaId")
    suspend fun eliminarPorEncuesta(encuestaId: String)
}
