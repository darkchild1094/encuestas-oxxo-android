package mx.com.getic.encuestasoxxo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mx.com.getic.encuestasoxxo.data.local.entities.UsuarioEntity

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuario_cache")
    suspend fun obtenerUsuarios(): List<UsuarioEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarUsuarios(usuarios: List<UsuarioEntity>)

    @Query("DELETE FROM usuario_cache")
    suspend fun borrarUsuarios()
}
