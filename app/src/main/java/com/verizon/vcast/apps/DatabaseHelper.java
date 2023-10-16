package com.verizon.vcast.apps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DATABASE_NAME = "timeStore.db";
    private static final int DATABASE_VERSION = 1;
    private static final String INSERT = "insert into table1(name) values (?)";
    private static final String INSERT_RUNTIME = "insert into tableLastRunTime(name) values (?)";
    private static final String TABLE_NAME = "table1";
    private static final String TABLE_NAME_LAST_RUN_TIME = "tableLastRunTime";
    private Context context;
    private SQLiteDatabase db = new OpenHelper(this.context).getWritableDatabase();
    private SQLiteStatement insertStmt = this.db.compileStatement(INSERT);
    private SQLiteStatement insertStmt_runtime = this.db.compileStatement(INSERT_RUNTIME);

    private static class OpenHelper extends SQLiteOpenHelper {
        OpenHelper(Context context) {
            super(context, DatabaseHelper.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("CREATE TABLE tableLastRunTime (id INTEGER PRIMARY KEY, name TEXT)");
            sQLiteDatabase.execSQL("CREATE TABLE table1 (id INTEGER PRIMARY KEY, name TEXT)");
        }

        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            Log.w("Example", "Upgrading database, this will drop tables and recreate.");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS table1");
            sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tableLastRunTime");
            onCreate(sQLiteDatabase);
        }
    }

    public DatabaseHelper(Context context2) {
        this.context = context2;
    }

    public void cleanup() {
        if (this.db != null) {
            try {
                this.db.close();
            } catch (Exception e) {
            }
            this.db = null;
        }
    }

    public void deleteAll() {
        this.db.delete(TABLE_NAME, null, null);
    }

    public void deleteRuntime() {
        this.db.delete(TABLE_NAME_LAST_RUN_TIME, null, null);
    }

    public long insert(String str) {
        this.insertStmt.bindString(1, str);
        return this.insertStmt.executeInsert();
    }

    public long insertRunTime(String str) {
        this.insertStmt_runtime.bindString(1, str);
        return this.insertStmt_runtime.executeInsert();
    }

    public List<String> selectAll() {
        ArrayList arrayList = new ArrayList();
        Cursor query = this.db.query(TABLE_NAME, new String[]{"name"}, null, null, null, null, "name desc");
        if (query.moveToFirst()) {
            do {
                arrayList.add(query.getString(0));
            } while (query.moveToNext());
        }
        if (query != null && !query.isClosed()) {
            query.close();
        }
        return arrayList;
    }

    public List<String> selectRunTime() {
        ArrayList arrayList = new ArrayList();
        Cursor query = this.db.query(TABLE_NAME_LAST_RUN_TIME, new String[]{"name"}, null, null, null, null, "name desc");
        if (query.moveToFirst()) {
            do {
                arrayList.add(query.getString(0));
            } while (query.moveToNext());
        }
        if (query != null && !query.isClosed()) {
            query.close();
        }
        return arrayList;
    }
}
