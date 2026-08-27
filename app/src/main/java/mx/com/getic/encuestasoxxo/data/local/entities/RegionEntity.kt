package mx.com.getic.encuestasoxxo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "region_cache")
data class RegionEntity(
    @PrimaryKey val id: Int,
    val negocioId: Int,
    val nombre: String,
    val cr: String?,
    val esDefault: Boolean
)
