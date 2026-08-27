package mx.com.getic.encuestasoxxo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import mx.com.getic.encuestasoxxo.data.local.dao.CuestionarioDao
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaDao
import mx.com.getic.encuestasoxxo.data.local.dao.AtiDao
import mx.com.getic.encuestasoxxo.data.local.dao.TiendaDao
import mx.com.getic.encuestasoxxo.data.local.entities.CuestionarioEntity
import mx.com.getic.encuestasoxxo.data.local.entities.AtiEntity
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

// Migracion 2 -> 3: agrega direccion y coordenadas al cache de tiendas.
private val MIGRACION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `tienda_cache` ADD COLUMN `direccion` TEXT")
        db.execSQL("ALTER TABLE `tienda_cache` ADD COLUMN `latitud` REAL")
        db.execSQL("ALTER TABLE `tienda_cache` ADD COLUMN `longitud` REAL")
    }
}

private val MIGRACION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `tienda_cache` ADD COLUMN `atiUsuarioId` INTEGER")
        db.execSQL("ALTER TABLE `tienda_cache` ADD COLUMN `atiNombre` TEXT")
        db.execSQL("ALTER TABLE `tienda_cache` ADD COLUMN `atiFoto` TEXT")
        db.execSQL("ALTER TABLE `tienda_cache` ADD COLUMN `atiGenero` TEXT")
        db.execSQL("ALTER TABLE `tienda_cache` ADD COLUMN `atiPendienteUsuarioId` INTEGER")
        db.execSQL("CREATE TABLE IF NOT EXISTS `ati_cache` (`plazaId` INTEGER NOT NULL, `id` INTEGER NOT NULL, `nombreCompleto` TEXT NOT NULL, `fotoPerfil` TEXT, `genero` TEXT, PRIMARY KEY(`plazaId`, `id`))")
    }
}

private val MIGRACION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `encuesta` ADD COLUMN `folio` TEXT NOT NULL DEFAULT ''")
    }
}

@Database(
    entities = [
        CuestionarioEntity::class,
        PreguntaEntity::class,
        EncuestaEntity::class,
        RespuestaDetalleEntity::class,
        TiendaEntity::class,
        AtiEntity::class,
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cuestionarioDao(): CuestionarioDao
    abstract fun encuestaDao(): EncuestaDao
    abstract fun atiDao(): AtiDao
    abstract fun tiendaDao(): TiendaDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "encuestas_oxxo.db"
                ).addMigrations(MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4, MIGRACION_4_5).build().also { instancia = it }
            }
    }
}
