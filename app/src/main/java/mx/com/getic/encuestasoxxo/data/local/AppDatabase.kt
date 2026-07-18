package mx.com.getic.encuestasoxxo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mx.com.getic.encuestasoxxo.data.local.dao.CuestionarioDao
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaDao
import mx.com.getic.encuestasoxxo.data.local.entities.CuestionarioEntity
import mx.com.getic.encuestasoxxo.data.local.entities.EncuestaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.PreguntaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.RespuestaDetalleEntity

@Database(
    entities = [
        CuestionarioEntity::class,
        PreguntaEntity::class,
        EncuestaEntity::class,
        RespuestaDetalleEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cuestionarioDao(): CuestionarioDao
    abstract fun encuestaDao(): EncuestaDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "encuestas_oxxo.db"
                ).build().also { instancia = it }
            }
    }
}
