package mx.com.getic.encuestasoxxo.data.remote.dto

data class NegocioDto(val id: Int, val nombre: String, val es_default: Boolean)
data class RegionDto(val id: Int, val nombre: String, val cr: String?, val es_default: Boolean)
data class PlazaDto(val id: Int, val nombre: String, val cr: String?, val es_default: Boolean)
data class TiendaDto(val id: Int, val nombre: String, val codigo: String)
