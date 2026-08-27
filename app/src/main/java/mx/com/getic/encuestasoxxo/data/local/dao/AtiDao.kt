package mx.com.getic.encuestasoxxo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mx.com.getic.encuestasoxxo.data.local.entities.AtiEntity

@Dao
interface AtiDao {
    @Query("SELECT * FROM ati_cache WHERE plazaId = :plazaId ORDER BY nombreCompleto")
    suspend fun obtenerPorPlaza(plazaId: Int): List<AtiEntity>

    @Query("DELETE FROM ati_cache WHERE plazaId = :plazaId")
    suspend fun borrarDe(plazaId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(atis: List<AtiEntity>)
}