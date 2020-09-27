package com.example.wifiscanlog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final  String DB_NAME = "WifiScanLogDB.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "WifiScans";
    private static final String KEY_NAME = "name";
    private static final String KEY_APINFO = "apInfo";

    private Context context;

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT);";
        db.execSQL(String.format(sql, TABLE_NAME, KEY_NAME, KEY_APINFO));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF NOT EXISTS %s;";
        db.execSQL(String.format(sql, TABLE_NAME));
    }
}
