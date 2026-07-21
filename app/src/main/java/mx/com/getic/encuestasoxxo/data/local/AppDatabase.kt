package mx.com.getic.encuestasoxxo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import mx.com.getic.encuestasoxxo.data.local.dao.CuestionarioDao
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaDao
import mx.com.getic.encuestasoxxo.data.local.dao.TiendaDao
import mx.com.getic.encuestasoxxo.data.local.entities.CuestionarioEntity
import mx.com.getic.encuestasoxxo.data.local.entities.EncuestaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.PreguntaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.RespuestaDetalleEntity
import mx.com.getic.encuestasoxxo.data.local.entities.TiendaEntity

// Migracion 1 -> 2: agrega la tabla de cache de tiendas (selector
// offline). Es puramente aditiva -- no toca las tablas existentes, asi
// que las encuestas que ya estuvieran guardadas localmente (pendientes
// de sincronizar) no se pierden al actualizar la app.
private val MIGRACION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `tienda_cache` (" +
                "`id` INTEGER NOT NULL, " +
                "`plazaId` INTEGER NOT NULL, " +
                "`nombre` TEXT NOT NULL, " +
                "`codigo` TEXT NOT NULL, " +
                "PRIMARY KEY(`id`))"
        )
    }
}

@Database(
    entities = [
        CuestionarioEntity::class,
        PreguntaEntity::class,
        EncuestaEntity::class,
        RespuestaDetalleEntity::class,
        TiendaEntity::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cuestionarioDao(): CuestionarioDao
    abstract fun encuestaDao(): EncuestaDao
    abstract fun tiendaDao(): TiendaDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "encuestas_oxxo.db"
                ).addMigrations(MIGRACION_1_2).build().also { instancia = it }
            }
    }
}
