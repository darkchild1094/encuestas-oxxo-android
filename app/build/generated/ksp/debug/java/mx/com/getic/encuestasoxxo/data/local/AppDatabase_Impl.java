package mx.com.getic.encuestasoxxo.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import mx.com.getic.encuestasoxxo.data.local.dao.AtiDao;
import mx.com.getic.encuestasoxxo.data.local.dao.AtiDao_Impl;
import mx.com.getic.encuestasoxxo.data.local.dao.CatalogoDao;
import mx.com.getic.encuestasoxxo.data.local.dao.CatalogoDao_Impl;
import mx.com.getic.encuestasoxxo.data.local.dao.CuestionarioDao;
import mx.com.getic.encuestasoxxo.data.local.dao.CuestionarioDao_Impl;
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaDao;
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaDao_Impl;
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaSyncLogDao;
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaSyncLogDao_Impl;
import mx.com.getic.encuestasoxxo.data.local.dao.TiendaDao;
import mx.com.getic.encuestasoxxo.data.local.dao.TiendaDao_Impl;
import mx.com.getic.encuestasoxxo.data.local.dao.UsuarioDao;
import mx.com.getic.encuestasoxxo.data.local.dao.UsuarioDao_Impl;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile CuestionarioDao _cuestionarioDao;

  private volatile EncuestaDao _encuestaDao;

  private volatile AtiDao _atiDao;

  private volatile TiendaDao _tiendaDao;

  private volatile EncuestaSyncLogDao _encuestaSyncLogDao;

  private volatile CatalogoDao _catalogoDao;

  private volatile UsuarioDao _usuarioDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(7) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `cuestionario_cache` (`id` INTEGER NOT NULL, `plazaId` INTEGER NOT NULL, `nombre` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `pregunta_cache` (`id` INTEGER NOT NULL, `cuestionarioId` INTEGER NOT NULL, `texto` TEXT NOT NULL, `orden` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `encuesta` (`id` TEXT NOT NULL, `usuarioId` INTEGER NOT NULL, `tiendaId` INTEGER NOT NULL, `cuestionarioId` INTEGER NOT NULL, `folio` TEXT NOT NULL, `comentario` TEXT, `fechaCreacionLocal` TEXT NOT NULL, `sincronizado` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `respuesta_detalle` (`id` TEXT NOT NULL, `encuestaId` TEXT NOT NULL, `preguntaId` INTEGER NOT NULL, `calificacion` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tienda_cache` (`id` INTEGER NOT NULL, `plazaId` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `codigo` TEXT NOT NULL, `direccion` TEXT, `latitud` REAL, `longitud` REAL, `atiUsuarioId` INTEGER, `atiNombre` TEXT, `atiFoto` TEXT, `atiGenero` TEXT, `atiPendienteUsuarioId` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ati_cache` (`plazaId` INTEGER NOT NULL, `id` INTEGER NOT NULL, `nombreCompleto` TEXT NOT NULL, `fotoPerfil` TEXT, `genero` TEXT, PRIMARY KEY(`plazaId`, `id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `encuesta_sync_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `encuesta_id` TEXT NOT NULL, `estado` TEXT NOT NULL, `intento_numero` INTEGER NOT NULL, `codigo_respuesta` INTEGER, `mensaje_error` TEXT, `handshake_id` TEXT, `confirmado_servidor` INTEGER NOT NULL, `fecha_intento` INTEGER NOT NULL, `fecha_confirmacion` INTEGER, FOREIGN KEY(`encuesta_id`) REFERENCES `encuesta`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_encuesta_sync_log_encuesta_id` ON `encuesta_sync_log` (`encuesta_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_encuesta_sync_log_estado` ON `encuesta_sync_log` (`estado`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_encuesta_sync_log_handshake_id` ON `encuesta_sync_log` (`handshake_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `negocio_cache` (`id` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `esDefault` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `region_cache` (`id` INTEGER NOT NULL, `negocioId` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `cr` TEXT, `esDefault` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `plaza_cache` (`id` INTEGER NOT NULL, `regionId` INTEGER NOT NULL, `nombre` TEXT NOT NULL, `cr` TEXT, `esDefault` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `usuario_cache` (`id` INTEGER NOT NULL, `correo` TEXT NOT NULL, `nombreCompleto` TEXT, `fotoPerfil` TEXT, `genero` TEXT, `plazaId` INTEGER, `plazaNombre` TEXT, `rol` TEXT NOT NULL, `gestionaPreguntas` INTEGER NOT NULL, `gestionaUsuarios` INTEGER NOT NULL, `esEncuestable` INTEGER NOT NULL, `veResultadosTiendas` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `rol_cache` (`id` INTEGER NOT NULL, `nombre` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '19bb66e374a27341322a64343cee6f2a')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `cuestionario_cache`");
        db.execSQL("DROP TABLE IF EXISTS `pregunta_cache`");
        db.execSQL("DROP TABLE IF EXISTS `encuesta`");
        db.execSQL("DROP TABLE IF EXISTS `respuesta_detalle`");
        db.execSQL("DROP TABLE IF EXISTS `tienda_cache`");
        db.execSQL("DROP TABLE IF EXISTS `ati_cache`");
        db.execSQL("DROP TABLE IF EXISTS `encuesta_sync_log`");
        db.execSQL("DROP TABLE IF EXISTS `negocio_cache`");
        db.execSQL("DROP TABLE IF EXISTS `region_cache`");
        db.execSQL("DROP TABLE IF EXISTS `plaza_cache`");
        db.execSQL("DROP TABLE IF EXISTS `usuario_cache`");
        db.execSQL("DROP TABLE IF EXISTS `rol_cache`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCuestionarioCache = new HashMap<String, TableInfo.Column>(3);
        _columnsCuestionarioCache.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCuestionarioCache.put("plazaId", new TableInfo.Column("plazaId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCuestionarioCache.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCuestionarioCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCuestionarioCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCuestionarioCache = new TableInfo("cuestionario_cache", _columnsCuestionarioCache, _foreignKeysCuestionarioCache, _indicesCuestionarioCache);
        final TableInfo _existingCuestionarioCache = TableInfo.read(db, "cuestionario_cache");
        if (!_infoCuestionarioCache.equals(_existingCuestionarioCache)) {
          return new RoomOpenHelper.ValidationResult(false, "cuestionario_cache(mx.com.getic.encuestasoxxo.data.local.entities.CuestionarioEntity).\n"
                  + " Expected:\n" + _infoCuestionarioCache + "\n"
                  + " Found:\n" + _existingCuestionarioCache);
        }
        final HashMap<String, TableInfo.Column> _columnsPreguntaCache = new HashMap<String, TableInfo.Column>(4);
        _columnsPreguntaCache.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreguntaCache.put("cuestionarioId", new TableInfo.Column("cuestionarioId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreguntaCache.put("texto", new TableInfo.Column("texto", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreguntaCache.put("orden", new TableInfo.Column("orden", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPreguntaCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPreguntaCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPreguntaCache = new TableInfo("pregunta_cache", _columnsPreguntaCache, _foreignKeysPreguntaCache, _indicesPreguntaCache);
        final TableInfo _existingPreguntaCache = TableInfo.read(db, "pregunta_cache");
        if (!_infoPreguntaCache.equals(_existingPreguntaCache)) {
          return new RoomOpenHelper.ValidationResult(false, "pregunta_cache(mx.com.getic.encuestasoxxo.data.local.entities.PreguntaEntity).\n"
                  + " Expected:\n" + _infoPreguntaCache + "\n"
                  + " Found:\n" + _existingPreguntaCache);
        }
        final HashMap<String, TableInfo.Column> _columnsEncuesta = new HashMap<String, TableInfo.Column>(8);
        _columnsEncuesta.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuesta.put("usuarioId", new TableInfo.Column("usuarioId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuesta.put("tiendaId", new TableInfo.Column("tiendaId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuesta.put("cuestionarioId", new TableInfo.Column("cuestionarioId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuesta.put("folio", new TableInfo.Column("folio", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuesta.put("comentario", new TableInfo.Column("comentario", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuesta.put("fechaCreacionLocal", new TableInfo.Column("fechaCreacionLocal", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuesta.put("sincronizado", new TableInfo.Column("sincronizado", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEncuesta = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEncuesta = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEncuesta = new TableInfo("encuesta", _columnsEncuesta, _foreignKeysEncuesta, _indicesEncuesta);
        final TableInfo _existingEncuesta = TableInfo.read(db, "encuesta");
        if (!_infoEncuesta.equals(_existingEncuesta)) {
          return new RoomOpenHelper.ValidationResult(false, "encuesta(mx.com.getic.encuestasoxxo.data.local.entities.EncuestaEntity).\n"
                  + " Expected:\n" + _infoEncuesta + "\n"
                  + " Found:\n" + _existingEncuesta);
        }
        final HashMap<String, TableInfo.Column> _columnsRespuestaDetalle = new HashMap<String, TableInfo.Column>(4);
        _columnsRespuestaDetalle.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRespuestaDetalle.put("encuestaId", new TableInfo.Column("encuestaId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRespuestaDetalle.put("preguntaId", new TableInfo.Column("preguntaId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRespuestaDetalle.put("calificacion", new TableInfo.Column("calificacion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRespuestaDetalle = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRespuestaDetalle = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRespuestaDetalle = new TableInfo("respuesta_detalle", _columnsRespuestaDetalle, _foreignKeysRespuestaDetalle, _indicesRespuestaDetalle);
        final TableInfo _existingRespuestaDetalle = TableInfo.read(db, "respuesta_detalle");
        if (!_infoRespuestaDetalle.equals(_existingRespuestaDetalle)) {
          return new RoomOpenHelper.ValidationResult(false, "respuesta_detalle(mx.com.getic.encuestasoxxo.data.local.entities.RespuestaDetalleEntity).\n"
                  + " Expected:\n" + _infoRespuestaDetalle + "\n"
                  + " Found:\n" + _existingRespuestaDetalle);
        }
        final HashMap<String, TableInfo.Column> _columnsTiendaCache = new HashMap<String, TableInfo.Column>(12);
        _columnsTiendaCache.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTiendaCache.put("plazaId", new TableInfo.Column("plazaId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTiendaCache.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTiendaCache.put("codigo", new TableInfo.Column("codigo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTiendaCache.put("direccion", new TableInfo.Column("direccion", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTiendaCache.put("latitud", new TableInfo.Column("latitud", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTiendaCache.put("longitud", new TableInfo.Column("longitud", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTiendaCache.put("atiUsuarioId", new TableInfo.Column("atiUsuarioId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTiendaCache.put("atiNombre", new TableInfo.Column("atiNombre", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTiendaCache.put("atiFoto", new TableInfo.Column("atiFoto", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTiendaCache.put("atiGenero", new TableInfo.Column("atiGenero", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTiendaCache.put("atiPendienteUsuarioId", new TableInfo.Column("atiPendienteUsuarioId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTiendaCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTiendaCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTiendaCache = new TableInfo("tienda_cache", _columnsTiendaCache, _foreignKeysTiendaCache, _indicesTiendaCache);
        final TableInfo _existingTiendaCache = TableInfo.read(db, "tienda_cache");
        if (!_infoTiendaCache.equals(_existingTiendaCache)) {
          return new RoomOpenHelper.ValidationResult(false, "tienda_cache(mx.com.getic.encuestasoxxo.data.local.entities.TiendaEntity).\n"
                  + " Expected:\n" + _infoTiendaCache + "\n"
                  + " Found:\n" + _existingTiendaCache);
        }
        final HashMap<String, TableInfo.Column> _columnsAtiCache = new HashMap<String, TableInfo.Column>(5);
        _columnsAtiCache.put("plazaId", new TableInfo.Column("plazaId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAtiCache.put("id", new TableInfo.Column("id", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAtiCache.put("nombreCompleto", new TableInfo.Column("nombreCompleto", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAtiCache.put("fotoPerfil", new TableInfo.Column("fotoPerfil", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAtiCache.put("genero", new TableInfo.Column("genero", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAtiCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAtiCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAtiCache = new TableInfo("ati_cache", _columnsAtiCache, _foreignKeysAtiCache, _indicesAtiCache);
        final TableInfo _existingAtiCache = TableInfo.read(db, "ati_cache");
        if (!_infoAtiCache.equals(_existingAtiCache)) {
          return new RoomOpenHelper.ValidationResult(false, "ati_cache(mx.com.getic.encuestasoxxo.data.local.entities.AtiEntity).\n"
                  + " Expected:\n" + _infoAtiCache + "\n"
                  + " Found:\n" + _existingAtiCache);
        }
        final HashMap<String, TableInfo.Column> _columnsEncuestaSyncLog = new HashMap<String, TableInfo.Column>(10);
        _columnsEncuestaSyncLog.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuestaSyncLog.put("encuesta_id", new TableInfo.Column("encuesta_id", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuestaSyncLog.put("estado", new TableInfo.Column("estado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuestaSyncLog.put("intento_numero", new TableInfo.Column("intento_numero", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuestaSyncLog.put("codigo_respuesta", new TableInfo.Column("codigo_respuesta", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuestaSyncLog.put("mensaje_error", new TableInfo.Column("mensaje_error", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuestaSyncLog.put("handshake_id", new TableInfo.Column("handshake_id", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuestaSyncLog.put("confirmado_servidor", new TableInfo.Column("confirmado_servidor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuestaSyncLog.put("fecha_intento", new TableInfo.Column("fecha_intento", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuestaSyncLog.put("fecha_confirmacion", new TableInfo.Column("fecha_confirmacion", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEncuestaSyncLog = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysEncuestaSyncLog.add(new TableInfo.ForeignKey("encuesta", "CASCADE", "NO ACTION", Arrays.asList("encuesta_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesEncuestaSyncLog = new HashSet<TableInfo.Index>(3);
        _indicesEncuestaSyncLog.add(new TableInfo.Index("index_encuesta_sync_log_encuesta_id", false, Arrays.asList("encuesta_id"), Arrays.asList("ASC")));
        _indicesEncuestaSyncLog.add(new TableInfo.Index("index_encuesta_sync_log_estado", false, Arrays.asList("estado"), Arrays.asList("ASC")));
        _indicesEncuestaSyncLog.add(new TableInfo.Index("index_encuesta_sync_log_handshake_id", false, Arrays.asList("handshake_id"), Arrays.asList("ASC")));
        final TableInfo _infoEncuestaSyncLog = new TableInfo("encuesta_sync_log", _columnsEncuestaSyncLog, _foreignKeysEncuestaSyncLog, _indicesEncuestaSyncLog);
        final TableInfo _existingEncuestaSyncLog = TableInfo.read(db, "encuesta_sync_log");
        if (!_infoEncuestaSyncLog.equals(_existingEncuestaSyncLog)) {
          return new RoomOpenHelper.ValidationResult(false, "encuesta_sync_log(mx.com.getic.encuestasoxxo.data.local.entities.EncuestaSyncLogEntity).\n"
                  + " Expected:\n" + _infoEncuestaSyncLog + "\n"
                  + " Found:\n" + _existingEncuestaSyncLog);
        }
        final HashMap<String, TableInfo.Column> _columnsNegocioCache = new HashMap<String, TableInfo.Column>(3);
        _columnsNegocioCache.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNegocioCache.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNegocioCache.put("esDefault", new TableInfo.Column("esDefault", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNegocioCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNegocioCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNegocioCache = new TableInfo("negocio_cache", _columnsNegocioCache, _foreignKeysNegocioCache, _indicesNegocioCache);
        final TableInfo _existingNegocioCache = TableInfo.read(db, "negocio_cache");
        if (!_infoNegocioCache.equals(_existingNegocioCache)) {
          return new RoomOpenHelper.ValidationResult(false, "negocio_cache(mx.com.getic.encuestasoxxo.data.local.entities.NegocioEntity).\n"
                  + " Expected:\n" + _infoNegocioCache + "\n"
                  + " Found:\n" + _existingNegocioCache);
        }
        final HashMap<String, TableInfo.Column> _columnsRegionCache = new HashMap<String, TableInfo.Column>(5);
        _columnsRegionCache.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegionCache.put("negocioId", new TableInfo.Column("negocioId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegionCache.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegionCache.put("cr", new TableInfo.Column("cr", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRegionCache.put("esDefault", new TableInfo.Column("esDefault", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRegionCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRegionCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRegionCache = new TableInfo("region_cache", _columnsRegionCache, _foreignKeysRegionCache, _indicesRegionCache);
        final TableInfo _existingRegionCache = TableInfo.read(db, "region_cache");
        if (!_infoRegionCache.equals(_existingRegionCache)) {
          return new RoomOpenHelper.ValidationResult(false, "region_cache(mx.com.getic.encuestasoxxo.data.local.entities.RegionEntity).\n"
                  + " Expected:\n" + _infoRegionCache + "\n"
                  + " Found:\n" + _existingRegionCache);
        }
        final HashMap<String, TableInfo.Column> _columnsPlazaCache = new HashMap<String, TableInfo.Column>(5);
        _columnsPlazaCache.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlazaCache.put("regionId", new TableInfo.Column("regionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlazaCache.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlazaCache.put("cr", new TableInfo.Column("cr", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlazaCache.put("esDefault", new TableInfo.Column("esDefault", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlazaCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPlazaCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPlazaCache = new TableInfo("plaza_cache", _columnsPlazaCache, _foreignKeysPlazaCache, _indicesPlazaCache);
        final TableInfo _existingPlazaCache = TableInfo.read(db, "plaza_cache");
        if (!_infoPlazaCache.equals(_existingPlazaCache)) {
          return new RoomOpenHelper.ValidationResult(false, "plaza_cache(mx.com.getic.encuestasoxxo.data.local.entities.PlazaEntity).\n"
                  + " Expected:\n" + _infoPlazaCache + "\n"
                  + " Found:\n" + _existingPlazaCache);
        }
        final HashMap<String, TableInfo.Column> _columnsUsuarioCache = new HashMap<String, TableInfo.Column>(12);
        _columnsUsuarioCache.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuarioCache.put("correo", new TableInfo.Column("correo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuarioCache.put("nombreCompleto", new TableInfo.Column("nombreCompleto", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuarioCache.put("fotoPerfil", new TableInfo.Column("fotoPerfil", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuarioCache.put("genero", new TableInfo.Column("genero", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuarioCache.put("plazaId", new TableInfo.Column("plazaId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuarioCache.put("plazaNombre", new TableInfo.Column("plazaNombre", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuarioCache.put("rol", new TableInfo.Column("rol", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuarioCache.put("gestionaPreguntas", new TableInfo.Column("gestionaPreguntas", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuarioCache.put("gestionaUsuarios", new TableInfo.Column("gestionaUsuarios", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuarioCache.put("esEncuestable", new TableInfo.Column("esEncuestable", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuarioCache.put("veResultadosTiendas", new TableInfo.Column("veResultadosTiendas", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsuarioCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsuarioCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsuarioCache = new TableInfo("usuario_cache", _columnsUsuarioCache, _foreignKeysUsuarioCache, _indicesUsuarioCache);
        final TableInfo _existingUsuarioCache = TableInfo.read(db, "usuario_cache");
        if (!_infoUsuarioCache.equals(_existingUsuarioCache)) {
          return new RoomOpenHelper.ValidationResult(false, "usuario_cache(mx.com.getic.encuestasoxxo.data.local.entities.UsuarioEntity).\n"
                  + " Expected:\n" + _infoUsuarioCache + "\n"
                  + " Found:\n" + _existingUsuarioCache);
        }
        final HashMap<String, TableInfo.Column> _columnsRolCache = new HashMap<String, TableInfo.Column>(2);
        _columnsRolCache.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRolCache.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRolCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRolCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRolCache = new TableInfo("rol_cache", _columnsRolCache, _foreignKeysRolCache, _indicesRolCache);
        final TableInfo _existingRolCache = TableInfo.read(db, "rol_cache");
        if (!_infoRolCache.equals(_existingRolCache)) {
          return new RoomOpenHelper.ValidationResult(false, "rol_cache(mx.com.getic.encuestasoxxo.data.local.entities.RolEntity).\n"
                  + " Expected:\n" + _infoRolCache + "\n"
                  + " Found:\n" + _existingRolCache);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "19bb66e374a27341322a64343cee6f2a", "7d2f96d6a059af0e4631e0b04cf9ec5e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "cuestionario_cache","pregunta_cache","encuesta","respuesta_detalle","tienda_cache","ati_cache","encuesta_sync_log","negocio_cache","region_cache","plaza_cache","usuario_cache","rol_cache");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `cuestionario_cache`");
      _db.execSQL("DELETE FROM `pregunta_cache`");
      _db.execSQL("DELETE FROM `encuesta`");
      _db.execSQL("DELETE FROM `respuesta_detalle`");
      _db.execSQL("DELETE FROM `tienda_cache`");
      _db.execSQL("DELETE FROM `ati_cache`");
      _db.execSQL("DELETE FROM `encuesta_sync_log`");
      _db.execSQL("DELETE FROM `negocio_cache`");
      _db.execSQL("DELETE FROM `region_cache`");
      _db.execSQL("DELETE FROM `plaza_cache`");
      _db.execSQL("DELETE FROM `usuario_cache`");
      _db.execSQL("DELETE FROM `rol_cache`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CuestionarioDao.class, CuestionarioDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EncuestaDao.class, EncuestaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AtiDao.class, AtiDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TiendaDao.class, TiendaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EncuestaSyncLogDao.class, EncuestaSyncLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CatalogoDao.class, CatalogoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UsuarioDao.class, UsuarioDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CuestionarioDao cuestionarioDao() {
    if (_cuestionarioDao != null) {
      return _cuestionarioDao;
    } else {
      synchronized(this) {
        if(_cuestionarioDao == null) {
          _cuestionarioDao = new CuestionarioDao_Impl(this);
        }
        return _cuestionarioDao;
      }
    }
  }

  @Override
  public EncuestaDao encuestaDao() {
    if (_encuestaDao != null) {
      return _encuestaDao;
    } else {
      synchronized(this) {
        if(_encuestaDao == null) {
          _encuestaDao = new EncuestaDao_Impl(this);
        }
        return _encuestaDao;
      }
    }
  }

  @Override
  public AtiDao atiDao() {
    if (_atiDao != null) {
      return _atiDao;
    } else {
      synchronized(this) {
        if(_atiDao == null) {
          _atiDao = new AtiDao_Impl(this);
        }
        return _atiDao;
      }
    }
  }

  @Override
  public TiendaDao tiendaDao() {
    if (_tiendaDao != null) {
      return _tiendaDao;
    } else {
      synchronized(this) {
        if(_tiendaDao == null) {
          _tiendaDao = new TiendaDao_Impl(this);
        }
        return _tiendaDao;
      }
    }
  }

  @Override
  public EncuestaSyncLogDao encuestaSyncLogDao() {
    if (_encuestaSyncLogDao != null) {
      return _encuestaSyncLogDao;
    } else {
      synchronized(this) {
        if(_encuestaSyncLogDao == null) {
          _encuestaSyncLogDao = new EncuestaSyncLogDao_Impl(this);
        }
        return _encuestaSyncLogDao;
      }
    }
  }

  @Override
  public CatalogoDao catalogoDao() {
    if (_catalogoDao != null) {
      return _catalogoDao;
    } else {
      synchronized(this) {
        if(_catalogoDao == null) {
          _catalogoDao = new CatalogoDao_Impl(this);
        }
        return _catalogoDao;
      }
    }
  }

  @Override
  public UsuarioDao usuarioDao() {
    if (_usuarioDao != null) {
      return _usuarioDao;
    } else {
      synchronized(this) {
        if(_usuarioDao == null) {
          _usuarioDao = new UsuarioDao_Impl(this);
        }
        return _usuarioDao;
      }
    }
  }
}
