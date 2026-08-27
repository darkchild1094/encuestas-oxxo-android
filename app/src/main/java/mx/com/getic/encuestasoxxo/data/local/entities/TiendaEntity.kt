package mx.com.getic.encuestasoxxo.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tienda_cache",
    indices = [Index(value = ["plazaId"])]
)
data class TiendaEntity(
    @PrimaryKey val id: Int,
    val plazaId: Int,
    val nombre: String,
    val codigo: String,
    val direccion: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val atiUsuarioId: Int? = null,
    val atiNombre: String? = null,
    val atiFoto: String? = null,
    val atiGenero: String? = null,
    val atiPendienteUsuarioId: Int? = null,
)
