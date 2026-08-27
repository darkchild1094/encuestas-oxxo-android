package mx.com.getic.encuestasoxxo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mx.com.getic.encuestasoxxo.data.local.entities.*

@Dao
interface CatalogoDao {
    @Query("SELECT * FROM negocio_cache")
    suspend fun obtenerNegocios(): List<NegocioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarNegocios(negocios: List<NegocioEntity>)

    @Query("SELECT * FROM region_cache WHERE negocioId = :negocioId")
    suspend fun obtenerRegiones(negocioId: Int): List<RegionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarRegiones(regiones: List<RegionEntity>)

    @Query("SELECT * FROM plaza_cache WHERE regionId = :regionId")
    suspend fun obtenerPlazas(regionId: Int): List<PlazaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarPlazas(plazas: List<PlazaEntity>)

    @Query("SELECT * FROM rol_cache")
    suspend fun obtenerRoles(): List<RolEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarRoles(roles: List<RolEntity>)
    
    @Query("DELETE FROM negocio_cache")
    suspend fun borrarNegocios()

    @Query("DELETE FROM region_cache")
    suspend fun borrarRegiones()

    @Query("DELETE FROM plaza_cache")
    suspend fun borrarPlazas()

    @Query("DELETE FROM rol_cache")
    suspend fun borrarRoles()
}
