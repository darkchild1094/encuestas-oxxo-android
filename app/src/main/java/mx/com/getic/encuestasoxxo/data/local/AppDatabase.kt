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
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaSyncLogDao
import mx.com.getic.encuestasoxxo.data.local.dao.CatalogoDao
import mx.com.getic.encuestasoxxo.data.local.dao.UsuarioDao
import mx.com.getic.encuestasoxxo.data.local.entities.CuestionarioEntity
import mx.com.getic.encuestasoxxo.data.local.entities.AtiEntity
import mx.com.getic.encuestasoxxo.data.local.entities.EncuestaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.EncuestaSyncLogEntity
import mx.com.getic.encuestasoxxo.data.local.entities.PreguntaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.RespuestaDetalleEntity
import mx.com.getic.encuestasoxxo.data.local.entities.TiendaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.NegocioEntity
import mx.com.getic.encuestasoxxo.data.local.entities.RegionEntity
import mx.com.getic.encuestasoxxo.data.local.entities.PlazaEntity
import mx.com.getic.encuestasoxxo.data.local.entities.UsuarioEntity
import mx.com.getic.encuestasoxxo.data.local.entities.RolEntity

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

private val MIGRACION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `encuesta_sync_log` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `encuesta_id` TEXT NOT NULL,
                `estado` TEXT NOT NULL,
                `intento_numero` INTEGER NOT NULL DEFAULT 1,
                `codigo_respuesta` INTEGER,
                `mensaje_error` TEXT,
                `handshake_id` TEXT,
                `confirmado_servidor` INTEGER NOT NULL DEFAULT 0,
                `fecha_intento` INTEGER NOT NULL,
                `fecha_confirmacion` INTEGER,
                FOREIGN KEY(`encuesta_id`) REFERENCES `encuesta`(`id`) ON DELETE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX `idx_encuesta_id` ON `encuesta_sync_log`(`encuesta_id`)")
        db.execSQL("CREATE INDEX `idx_estado` ON `encuesta_sync_log`(`estado`)")
        db.execSQL("CREATE INDEX `idx_handshake_id` ON `encuesta_sync_log`(`handshake_id`)")
    }
}

private val MIGRACION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `negocio_cache` (`id` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `esDefault` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `region_cache` (`id` INTEGER NOT NULL, `negocioId` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `cr` TEXT, `esDefault` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `plaza_cache` (`id` INTEGER NOT NULL, `regionId` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `cr` TEXT, `esDefault` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `usuario_cache` (`id` INTEGER NOT NULL, `correo` TEXT NOT NULL, `nombreCompleto` TEXT, `fotoPerfil` TEXT, `genero` TEXT, `plazaId` INTEGER, `plazaNombre` TEXT, `rol` TEXT NOT NULL, `gestionaPreguntas` INTEGER NOT NULL, `gestionaUsuarios` INTEGER NOT NULL, `esEncuestable` INTEGER NOT NULL, `veResultadosTiendas` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `rol_cache` (`id` INTEGER NOT NULL, `nombre` TEXT NOT NULL, PRIMARY KEY(`id`))")
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
        EncuestaSyncLogEntity::class,
        NegocioEntity::class,
        RegionEntity::class,
        PlazaEntity::class,
        UsuarioEntity::class,
        RolEntity::class,
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cuestionarioDao(): CuestionarioDao
    abstract fun encuestaDao(): EncuestaDao
    abstract fun atiDao(): AtiDao
    abstract fun tiendaDao(): TiendaDao
    abstract fun encuestaSyncLogDao(): EncuestaSyncLogDao
    abstract fun catalogoDao(): CatalogoDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "encuestas_oxxo.db"
                ).addMigrations(MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4, MIGRACION_4_5, MIGRACION_5_6, MIGRACION_6_7).build().also { instancia = it }
            }
    }
}
