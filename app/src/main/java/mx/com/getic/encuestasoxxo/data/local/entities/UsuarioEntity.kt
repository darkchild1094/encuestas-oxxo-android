package mx.com.getic.encuestasoxxo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario_cache")
data class UsuarioEntity(
    @PrimaryKey val id: Int,
    val correo: String,
    val nombreCompleto: String?,
    val fotoPerfil: String?,
    val genero: String?,
    val plazaId: Int?,
    val plazaNombre: String?,
    val rol: String,
    val gestionaPreguntas: Boolean,
    val gestionaUsuarios: Boolean,
    val esEncuestable: Boolean,
    val veResultadosTiendas: Boolean
)
