package mx.com.getic.encuestasoxxo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Cache local de las tiendas de la plaza del usuario. Se refresca cada
// vez que hay señal (tipicamente al iniciar sesion), y sirve de
// respaldo cuando el selector de tienda se abre sin conexion.
@Entity(tableName = "tienda_cache")
data class TiendaEntity(
    @PrimaryKey val id: Int,
    val plazaId: Int,
    val nombre: String,
    val codigo: String,
)
