package mx.com.getic.encuestasoxxo.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import mx.com.getic.encuestasoxxo.data.local.entities.CuestionarioEntity;
import mx.com.getic.encuestasoxxo.data.local.entities.PreguntaEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CuestionarioDao_Impl implements CuestionarioDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CuestionarioEntity> __insertionAdapterOfCuestionarioEntity;

  private final EntityInsertionAdapter<PreguntaEntity> __insertionAdapterOfPreguntaEntity;

  private final SharedSQLiteStatement __preparedStmtOfBorrarPreguntasDe;

  public CuestionarioDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCuestionarioEntity = new EntityInsertionAdapter<CuestionarioEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cuestionario_cache` (`id`,`plazaId`,`nombre`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CuestionarioEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPlazaId());
        statement.bindString(3, entity.getNombre());
      }
    };
    this.__insertionAdapterOfPreguntaEntity = new EntityInsertionAdapter<PreguntaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pregunta_cache` (`id`,`cuestionarioId`,`texto`,`orden`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PreguntaEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCuestionarioId());
        statement.bindString(3, entity.getTexto());
        statement.bindLong(4, entity.getOrden());
      }
    };
    this.__preparedStmtOfBorrarPreguntasDe = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pregunta_cache WHERE cuestionarioId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object guardar(final CuestionarioEntity cuestionario,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCuestionarioEntity.insert(cuestionario);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object guardarPreguntas(final List<PreguntaEntity> preguntas,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPreguntaEntity.insert(preguntas);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object borrarPreguntasDe(final int cuestionarioId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfBorrarPreguntasDe.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cuestionarioId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfBorrarPreguntasDe.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object obtenerPorPlaza(final int plazaId,
      final Continuation<? super CuestionarioEntity> $completion) {
    final String _sql = "SELECT * FROM cuestionario_cache WHERE plazaId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, plazaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CuestionarioEntity>() {
      @Override
      @Nullable
      public CuestionarioEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlazaId = CursorUtil.getColumnIndexOrThrow(_cursor, "plazaId");
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final CuestionarioEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpPlazaId;
            _tmpPlazaId = _cursor.getInt(_cursorIndexOfPlazaId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            _result = new CuestionarioEntity(_tmpId,_tmpPlazaId,_tmpNombre);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object obtenerPreguntas(final int cuestionarioId,
      final Continuation<? super List<PreguntaEntity>> $completion) {
    final String _sql = "SELECT * FROM pregunta_cache WHERE cuestionarioId = ? ORDER BY orden";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cuestionarioId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PreguntaEntity>>() {
      @Override
      @NonNull
      public List<PreguntaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCuestionarioId = CursorUtil.getColumnIndexOrThrow(_cursor, "cuestionarioId");
          final int _cursorIndexOfTexto = CursorUtil.getColumnIndexOrThrow(_cursor, "texto");
          final int _cursorIndexOfOrden = CursorUtil.getColumnIndexOrThrow(_cursor, "orden");
          final List<PreguntaEntity> _result = new ArrayList<PreguntaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PreguntaEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCuestionarioId;
            _tmpCuestionarioId = _cursor.getInt(_cursorIndexOfCuestionarioId);
            final String _tmpTexto;
            _tmpTexto = _cursor.getString(_cursorIndexOfTexto);
            final int _tmpOrden;
            _tmpOrden = _cursor.getInt(_cursorIndexOfOrden);
            _item = new PreguntaEntity(_tmpId,_tmpCuestionarioId,_tmpTexto,_tmpOrden);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
