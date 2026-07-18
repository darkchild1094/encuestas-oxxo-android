package mx.com.getic.encuestasoxxo.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
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
import mx.com.getic.encuestasoxxo.data.local.entities.EncuestaEntity;
import mx.com.getic.encuestasoxxo.data.local.entities.RespuestaDetalleEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EncuestaDao_Impl implements EncuestaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EncuestaEntity> __insertionAdapterOfEncuestaEntity;

  private final EntityInsertionAdapter<RespuestaDetalleEntity> __insertionAdapterOfRespuestaDetalleEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarcarSincronizada;

  public EncuestaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEncuestaEntity = new EntityInsertionAdapter<EncuestaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `encuesta` (`id`,`usuarioId`,`tiendaId`,`cuestionarioId`,`comentario`,`fechaCreacionLocal`,`sincronizado`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EncuestaEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindLong(2, entity.getUsuarioId());
        statement.bindLong(3, entity.getTiendaId());
        statement.bindLong(4, entity.getCuestionarioId());
        if (entity.getComentario() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getComentario());
        }
        statement.bindString(6, entity.getFechaCreacionLocal());
        final int _tmp = entity.getSincronizado() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
    this.__insertionAdapterOfRespuestaDetalleEntity = new EntityInsertionAdapter<RespuestaDetalleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `respuesta_detalle` (`id`,`encuestaId`,`preguntaId`,`calificacion`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RespuestaDetalleEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getEncuestaId());
        statement.bindLong(3, entity.getPreguntaId());
        statement.bindLong(4, entity.getCalificacion());
      }
    };
    this.__preparedStmtOfMarcarSincronizada = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE encuesta SET sincronizado = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object guardarEncuesta(final EncuestaEntity encuesta,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEncuestaEntity.insert(encuesta);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object guardarRespuestas(final List<RespuestaDetalleEntity> respuestas,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRespuestaDetalleEntity.insert(respuestas);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object marcarSincronizada(final String encuestaId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarcarSincronizada.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, encuestaId);
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
          __preparedStmtOfMarcarSincronizada.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object pendientesDeSincronizar(
      final Continuation<? super List<EncuestaEntity>> $completion) {
    final String _sql = "SELECT * FROM encuesta WHERE sincronizado = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EncuestaEntity>>() {
      @Override
      @NonNull
      public List<EncuestaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsuarioId = CursorUtil.getColumnIndexOrThrow(_cursor, "usuarioId");
          final int _cursorIndexOfTiendaId = CursorUtil.getColumnIndexOrThrow(_cursor, "tiendaId");
          final int _cursorIndexOfCuestionarioId = CursorUtil.getColumnIndexOrThrow(_cursor, "cuestionarioId");
          final int _cursorIndexOfComentario = CursorUtil.getColumnIndexOrThrow(_cursor, "comentario");
          final int _cursorIndexOfFechaCreacionLocal = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacionLocal");
          final int _cursorIndexOfSincronizado = CursorUtil.getColumnIndexOrThrow(_cursor, "sincronizado");
          final List<EncuestaEntity> _result = new ArrayList<EncuestaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EncuestaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final int _tmpUsuarioId;
            _tmpUsuarioId = _cursor.getInt(_cursorIndexOfUsuarioId);
            final int _tmpTiendaId;
            _tmpTiendaId = _cursor.getInt(_cursorIndexOfTiendaId);
            final int _tmpCuestionarioId;
            _tmpCuestionarioId = _cursor.getInt(_cursorIndexOfCuestionarioId);
            final String _tmpComentario;
            if (_cursor.isNull(_cursorIndexOfComentario)) {
              _tmpComentario = null;
            } else {
              _tmpComentario = _cursor.getString(_cursorIndexOfComentario);
            }
            final String _tmpFechaCreacionLocal;
            _tmpFechaCreacionLocal = _cursor.getString(_cursorIndexOfFechaCreacionLocal);
            final boolean _tmpSincronizado;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSincronizado);
            _tmpSincronizado = _tmp != 0;
            _item = new EncuestaEntity(_tmpId,_tmpUsuarioId,_tmpTiendaId,_tmpCuestionarioId,_tmpComentario,_tmpFechaCreacionLocal,_tmpSincronizado);
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

  @Override
  public Object respuestasDe(final String encuestaId,
      final Continuation<? super List<RespuestaDetalleEntity>> $completion) {
    final String _sql = "SELECT * FROM respuesta_detalle WHERE encuestaId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, encuestaId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RespuestaDetalleEntity>>() {
      @Override
      @NonNull
      public List<RespuestaDetalleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEncuestaId = CursorUtil.getColumnIndexOrThrow(_cursor, "encuestaId");
          final int _cursorIndexOfPreguntaId = CursorUtil.getColumnIndexOrThrow(_cursor, "preguntaId");
          final int _cursorIndexOfCalificacion = CursorUtil.getColumnIndexOrThrow(_cursor, "calificacion");
          final List<RespuestaDetalleEntity> _result = new ArrayList<RespuestaDetalleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RespuestaDetalleEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpEncuestaId;
            _tmpEncuestaId = _cursor.getString(_cursorIndexOfEncuestaId);
            final int _tmpPreguntaId;
            _tmpPreguntaId = _cursor.getInt(_cursorIndexOfPreguntaId);
            final int _tmpCalificacion;
            _tmpCalificacion = _cursor.getInt(_cursorIndexOfCalificacion);
            _item = new RespuestaDetalleEntity(_tmpId,_tmpEncuestaId,_tmpPreguntaId,_tmpCalificacion);
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

  @Override
  public Object todas(final Continuation<? super List<EncuestaEntity>> $completion) {
    final String _sql = "SELECT * FROM encuesta ORDER BY fechaCreacionLocal DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EncuestaEntity>>() {
      @Override
      @NonNull
      public List<EncuestaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsuarioId = CursorUtil.getColumnIndexOrThrow(_cursor, "usuarioId");
          final int _cursorIndexOfTiendaId = CursorUtil.getColumnIndexOrThrow(_cursor, "tiendaId");
          final int _cursorIndexOfCuestionarioId = CursorUtil.getColumnIndexOrThrow(_cursor, "cuestionarioId");
          final int _cursorIndexOfComentario = CursorUtil.getColumnIndexOrThrow(_cursor, "comentario");
          final int _cursorIndexOfFechaCreacionLocal = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaCreacionLocal");
          final int _cursorIndexOfSincronizado = CursorUtil.getColumnIndexOrThrow(_cursor, "sincronizado");
          final List<EncuestaEntity> _result = new ArrayList<EncuestaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EncuestaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final int _tmpUsuarioId;
            _tmpUsuarioId = _cursor.getInt(_cursorIndexOfUsuarioId);
            final int _tmpTiendaId;
            _tmpTiendaId = _cursor.getInt(_cursorIndexOfTiendaId);
            final int _tmpCuestionarioId;
            _tmpCuestionarioId = _cursor.getInt(_cursorIndexOfCuestionarioId);
            final String _tmpComentario;
            if (_cursor.isNull(_cursorIndexOfComentario)) {
              _tmpComentario = null;
            } else {
              _tmpComentario = _cursor.getString(_cursorIndexOfComentario);
            }
            final String _tmpFechaCreacionLocal;
            _tmpFechaCreacionLocal = _cursor.getString(_cursorIndexOfFechaCreacionLocal);
            final boolean _tmpSincronizado;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSincronizado);
            _tmpSincronizado = _tmp != 0;
            _item = new EncuestaEntity(_tmpId,_tmpUsuarioId,_tmpTiendaId,_tmpCuestionarioId,_tmpComentario,_tmpFechaCreacionLocal,_tmpSincronizado);
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
