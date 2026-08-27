package mx.com.getic.encuestasoxxo.data.local.entities

import androidx.room.Entity

@Entity(tableName = "ati_cache", primaryKeys = ["plazaId", "id"])
data class AtiEntity(
    val plazaId: Int,
    val id: Int,
    val nombreCompleto: String,
    val fotoPerfil: String?,
    val genero: String?,
)