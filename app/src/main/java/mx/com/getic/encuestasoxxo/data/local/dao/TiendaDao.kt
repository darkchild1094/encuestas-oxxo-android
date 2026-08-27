package mx.com.getic.encuestasoxxo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mx.com.getic.encuestasoxxo.data.local.entities.TiendaEntity

@Dao
interface TiendaDao {
    @Query("SELECT * FROM tienda_cache WHERE id = :tiendaId LIMIT 1")
    suspend fun obtener(tiendaId: Int): TiendaEntity?

    @Query("SELECT * FROM tienda_cache WHERE plazaId = :plazaId ORDER BY nombre")
    suspend fun obtenerPorPlaza(plazaId: Int): List<TiendaEntity>

    @Query("DELETE FROM tienda_cache WHERE plazaId = :plazaId")
    suspend fun borrarDe(plazaId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(tiendas: List<TiendaEntity>)

    @Query("UPDATE tienda_cache SET atiUsuarioId = :atiId, atiNombre = :nombre, atiFoto = :foto, atiGenero = :genero, atiPendienteUsuarioId = :atiId WHERE id = :tiendaId")
    suspend fun guardarAsignacionPendiente(tiendaId: Int, atiId: Int, nombre: String, foto: String?, genero: String?)

    @Query("SELECT * FROM tienda_cache WHERE atiPendienteUsuarioId IS NOT NULL")
    suspend fun asignacionesPendientes(): List<TiendaEntity>

    @Query("UPDATE tienda_cache SET atiPendienteUsuarioId = NULL WHERE id = :tiendaId")
    suspend fun marcarAsignacionSincronizada(tiendaId: Int)
}
