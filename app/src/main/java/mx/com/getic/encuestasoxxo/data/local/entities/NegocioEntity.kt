package mx.com.getic.encuestasoxxo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "negocio_cache")
data class NegocioEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val esDefault: Boolean
)
