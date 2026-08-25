package mx.com.getic.encuestasoxxo.data.remote.dto

data class NegocioDto(val id: Int, val nombre: String, val es_default: Boolean)
data class RegionDto(val id: Int, val nombre: String, val cr: String?, val es_default: Boolean)
data class PlazaDto(val id: Int, val nombre: String, val cr: String?, val es_default: Boolean)
data class TiendaDto(
    val id: Int,
    val nombre: String,
    val codigo: String,
    val plaza_id: Int = 0,
    val direccion: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    // Vienen null si la tienda no tiene ATI asignado (comun fuera de
    // Valles): eso es la señal para que la UI muestre el selector.
    val ati_usuario_id: Int? = null,
    val ati_nombre: String? = null,
    val ati_foto: String? = null,
    val ati_genero: String? = "M", // "H" o "M"
)

data class AtiDto(
    val id: Int,
    val nombre_completo: String,
    val foto_perfil: String?,
    val genero: String? = "M", // "H" o "M"
)

data class AsignarAtiRequest(
    val tienda_id: Int,
    val usuario_id: Int,
)
