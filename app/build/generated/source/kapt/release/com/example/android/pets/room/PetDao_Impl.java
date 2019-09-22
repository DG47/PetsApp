package com.example.android.pets.room;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.lifecycle.ComputableLiveData;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.InvalidationTracker.Observer;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public final class PetDao_Impl implements PetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfPet;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfPet;

  private final EntityDeletionOrUpdateAdapter __updateAdapterOfPet;

  private final SharedSQLiteStatement __preparedStmtOfDeletePet;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public PetDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPet = new EntityInsertionAdapter<Pet>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `Pet`(`id`,`name`,`type`,`breed`,`gender`,`weight`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Pet value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        stmt.bindLong(3, value.getType());
        if (value.getBreed() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getBreed());
        }
        stmt.bindLong(5, value.getGender());
        stmt.bindLong(6, value.getWeight());
      }
    };
    this.__deletionAdapterOfPet = new EntityDeletionOrUpdateAdapter<Pet>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `Pet` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Pet value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfPet = new EntityDeletionOrUpdateAdapter<Pet>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `Pet` SET `id` = ?,`name` = ?,`type` = ?,`breed` = ?,`gender` = ?,`weight` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Pet value) {
        stmt.bindLong(1, value.getId());
        if (value.getName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getName());
        }
        stmt.bindLong(3, value.getType());
        if (value.getBreed() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getBreed());
        }
        stmt.bindLong(5, value.getGender());
        stmt.bindLong(6, value.getWeight());
        stmt.bindLong(7, value.getId());
      }
    };
    this.__preparedStmtOfDeletePet = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM Pet WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM Pet";
        return _query;
      }
    };
  }

  @Override
  public long insert(Pet pet) {
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfPet.insertAndReturnId(pet);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(Pet pet) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfPet.handle(pet);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(Pet pet) {
    __db.beginTransaction();
    try {
      __updateAdapterOfPet.handle(pet);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deletePet(int petId) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePet.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      _stmt.bindLong(_argIndex, petId);
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeletePet.release(_stmt);
    }
  }

  @Override
  public void deleteAll() {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public LiveData<List<Pet>> getAll() {
    final String _sql = "SELECT * FROM Pet";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new ComputableLiveData<List<Pet>>(__db.getQueryExecutor()) {
      private Observer _observer;

      @Override
      protected List<Pet> compute() {
        if (_observer == null) {
          _observer = new Observer("Pet") {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
              invalidate();
            }
          };
          __db.getInvalidationTracker().addWeakObserver(_observer);
        }
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
          final int _cursorIndexOfName = _cursor.getColumnIndexOrThrow("name");
          final int _cursorIndexOfType = _cursor.getColumnIndexOrThrow("type");
          final int _cursorIndexOfBreed = _cursor.getColumnIndexOrThrow("breed");
          final int _cursorIndexOfGender = _cursor.getColumnIndexOrThrow("gender");
          final int _cursorIndexOfWeight = _cursor.getColumnIndexOrThrow("weight");
          final List<Pet> _result = new ArrayList<Pet>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Pet _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpType;
            _tmpType = _cursor.getInt(_cursorIndexOfType);
            final String _tmpBreed;
            _tmpBreed = _cursor.getString(_cursorIndexOfBreed);
            final int _tmpGender;
            _tmpGender = _cursor.getInt(_cursorIndexOfGender);
            final int _tmpWeight;
            _tmpWeight = _cursor.getInt(_cursorIndexOfWeight);
            _item = new Pet(_tmpId,_tmpName,_tmpType,_tmpBreed,_tmpGender,_tmpWeight);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    }.getLiveData();
  }

  @Override
  public Pet getPet(int petId) {
    final String _sql = "SELECT * FROM Pet WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, petId);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfName = _cursor.getColumnIndexOrThrow("name");
      final int _cursorIndexOfType = _cursor.getColumnIndexOrThrow("type");
      final int _cursorIndexOfBreed = _cursor.getColumnIndexOrThrow("breed");
      final int _cursorIndexOfGender = _cursor.getColumnIndexOrThrow("gender");
      final int _cursorIndexOfWeight = _cursor.getColumnIndexOrThrow("weight");
      final Pet _result;
      if(_cursor.moveToFirst()) {
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        final int _tmpType;
        _tmpType = _cursor.getInt(_cursorIndexOfType);
        final String _tmpBreed;
        _tmpBreed = _cursor.getString(_cursorIndexOfBreed);
        final int _tmpGender;
        _tmpGender = _cursor.getInt(_cursorIndexOfGender);
        final int _tmpWeight;
        _tmpWeight = _cursor.getInt(_cursorIndexOfWeight);
        _result = new Pet(_tmpId,_tmpName,_tmpType,_tmpBreed,_tmpGender,_tmpWeight);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Pet> searchPet(String text) {
    final String _sql = "SELECT * FROM Pet WHERE name || breed LIKE ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (text == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, text);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfName = _cursor.getColumnIndexOrThrow("name");
      final int _cursorIndexOfType = _cursor.getColumnIndexOrThrow("type");
      final int _cursorIndexOfBreed = _cursor.getColumnIndexOrThrow("breed");
      final int _cursorIndexOfGender = _cursor.getColumnIndexOrThrow("gender");
      final int _cursorIndexOfWeight = _cursor.getColumnIndexOrThrow("weight");
      final List<Pet> _result = new ArrayList<Pet>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Pet _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        final int _tmpType;
        _tmpType = _cursor.getInt(_cursorIndexOfType);
        final String _tmpBreed;
        _tmpBreed = _cursor.getString(_cursorIndexOfBreed);
        final int _tmpGender;
        _tmpGender = _cursor.getInt(_cursorIndexOfGender);
        final int _tmpWeight;
        _tmpWeight = _cursor.getInt(_cursorIndexOfWeight);
        _item = new Pet(_tmpId,_tmpName,_tmpType,_tmpBreed,_tmpGender,_tmpWeight);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Pet> searchType(int type) {
    final String _sql = "SELECT * FROM Pet WHERE type LIKE ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, type);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfName = _cursor.getColumnIndexOrThrow("name");
      final int _cursorIndexOfType = _cursor.getColumnIndexOrThrow("type");
      final int _cursorIndexOfBreed = _cursor.getColumnIndexOrThrow("breed");
      final int _cursorIndexOfGender = _cursor.getColumnIndexOrThrow("gender");
      final int _cursorIndexOfWeight = _cursor.getColumnIndexOrThrow("weight");
      final List<Pet> _result = new ArrayList<Pet>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Pet _item;
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        final String _tmpName;
        _tmpName = _cursor.getString(_cursorIndexOfName);
        final int _tmpType;
        _tmpType = _cursor.getInt(_cursorIndexOfType);
        final String _tmpBreed;
        _tmpBreed = _cursor.getString(_cursorIndexOfBreed);
        final int _tmpGender;
        _tmpGender = _cursor.getInt(_cursorIndexOfGender);
        final int _tmpWeight;
        _tmpWeight = _cursor.getInt(_cursorIndexOfWeight);
        _item = new Pet(_tmpId,_tmpName,_tmpType,_tmpBreed,_tmpGender,_tmpWeight);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
