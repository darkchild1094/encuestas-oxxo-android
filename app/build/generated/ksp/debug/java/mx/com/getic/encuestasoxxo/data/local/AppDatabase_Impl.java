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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import mx.com.getic.encuestasoxxo.data.local.dao.CuestionarioDao;
import mx.com.getic.encuestasoxxo.data.local.dao.CuestionarioDao_Impl;
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaDao;
import mx.com.getic.encuestasoxxo.data.local.dao.EncuestaDao_Impl;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile CuestionarioDao _cuestionarioDao;

  private volatile EncuestaDao _encuestaDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `cuestionario_cache` (`id` INTEGER NOT NULL, `plazaId` INTEGER NOT NULL, `nombre` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `pregunta_cache` (`id` INTEGER NOT NULL, `cuestionarioId` INTEGER NOT NULL, `texto` TEXT NOT NULL, `orden` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `encuesta` (`id` TEXT NOT NULL, `usuarioId` INTEGER NOT NULL, `tiendaId` INTEGER NOT NULL, `cuestionarioId` INTEGER NOT NULL, `comentario` TEXT, `fechaCreacionLocal` TEXT NOT NULL, `sincronizado` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `respuesta_detalle` (`id` TEXT NOT NULL, `encuestaId` TEXT NOT NULL, `preguntaId` INTEGER NOT NULL, `calificacion` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '042460a1a0f444a9a3d6b9960fb01e15')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `cuestionario_cache`");
        db.execSQL("DROP TABLE IF EXISTS `pregunta_cache`");
        db.execSQL("DROP TABLE IF EXISTS `encuesta`");
        db.execSQL("DROP TABLE IF EXISTS `respuesta_detalle`");
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
        final HashMap<String, TableInfo.Column> _columnsEncuesta = new HashMap<String, TableInfo.Column>(7);
        _columnsEncuesta.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuesta.put("usuarioId", new TableInfo.Column("usuarioId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuesta.put("tiendaId", new TableInfo.Column("tiendaId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncuesta.put("cuestionarioId", new TableInfo.Column("cuestionarioId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
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
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "042460a1a0f444a9a3d6b9960fb01e15", "0360ec8cc85df44a01cdccd4a5d72d4a");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "cuestionario_cache","pregunta_cache","encuesta","respuesta_detalle");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `cuestionario_cache`");
      _db.execSQL("DELETE FROM `pregunta_cache`");
      _db.execSQL("DELETE FROM `encuesta`");
      _db.execSQL("DELETE FROM `respuesta_detalle`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
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
}
