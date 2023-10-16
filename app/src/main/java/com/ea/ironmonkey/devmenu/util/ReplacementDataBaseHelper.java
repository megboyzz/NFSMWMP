package com.ea.ironmonkey.devmenu.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReplacementDataBaseHelper extends SQLiteOpenHelper {

    public static final String MAIN_TABLE_NAME = "Replacements";
    public static final String PATH_TO_REPLACED_ELEMENT = "Path";
    public static final String NAME_OF_BACKUPED_ELEMENT = "Original_element";
    private static final int DATABASE_VERSION = 1;

    public SQLiteDatabase getDatabase() {
        return database;
    }

    private SQLiteDatabase database;


    public ReplacementDataBaseHelper(Context context) {
        super(context, MAIN_TABLE_NAME + ".db", (SQLiteDatabase.CursorFactory) (db, masterQuery, editTable, query) -> null, DATABASE_VERSION);
        database = context.openOrCreateDatabase(MAIN_TABLE_NAME + ".db", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS " + MAIN_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PATH_TO_REPLACED_ELEMENT + " TEXT,"
                + NAME_OF_BACKUPED_ELEMENT + " TEXT);");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
