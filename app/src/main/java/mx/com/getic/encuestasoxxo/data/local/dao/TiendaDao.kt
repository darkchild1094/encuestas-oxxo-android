package mx.com.getic.encuestasoxxo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mx.com.getic.encuestasoxxo.data.local.entities.TiendaEntity

@Dao
interface TiendaDao {
    @Query("SELECT * FROM tienda_cache WHERE plazaId = :plazaId ORDER BY nombre")
    suspend fun obtenerPorPlaza(plazaId: Int): List<TiendaEntity>

    @Query("DELETE FROM tienda_cache WHERE plazaId = :plazaId")
    suspend fun borrarDe(plazaId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(tiendas: List<TiendaEntity>)
}
