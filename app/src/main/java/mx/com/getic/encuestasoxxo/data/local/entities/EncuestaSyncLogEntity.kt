package mx.com.getic.encuestasoxxo.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "encuesta_sync_log",
    foreignKeys = [
        ForeignKey(
            entity = EncuestaEntity::class,
            parentColumns = ["id"],
            childColumns = ["encuesta_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("encuesta_id"),
        Index("estado"),
        Index("handshake_id")
    ]
)
data class EncuestaSyncLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val encuesta_id: String,
    val estado: String,
    val intento_numero: Int = 1,
    val codigo_respuesta: Int? = null,
    val mensaje_error: String? = null,
    val handshake_id: String? = null,
    val confirmado_servidor: Boolean = false,
    val fecha_intento: Long = System.currentTimeMillis(),
    val fecha_confirmacion: Long? = null
)

data class SyncInitResponse(
    val handshake_id: String,
    val estado: String,
    val mensaje: String
)

data class SyncConfirmResponse(
    val confirmado: Boolean,
    val encuesta_id: String,
    val mensaje: String
)

data class SyncStatusResponse(
    val encuesta_id: String,
    val estado: String,
    val intento_numero: Int,
    val codigo_respuesta: Int?,
    val mensaje_error: String?,
    val handshake_id: String?,
    val confirmado_servidor: Boolean,
    val fecha_intento: String,
    val fecha_confirmacion: String?
)

data class ErrorRegistroRequest(
    val handshake_id: String,
    val codigo_respuesta: Int? = null,
    val mensaje_error: String,
    val intento_numero: Int
)
