package mx.com.getic.encuestasoxxo.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plaza_cache",
    indices = [Index(value = ["regionId"])]
)
data class PlazaEntity(
    @PrimaryKey val id: Int,
    val regionId: Int,
    val nombre: String,
    val cr: String?,
    val esDefault: Boolean
)
