package com.example.wifiscanlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBAdapter {
    private static final  String DB_NAME = "WifiScanLogDB.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "WifiScans";
    private static final String KEY_NAME = "name";
    private static final String KEY_APINFO = "apInfo";

    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context context) {
        this.context = context;
    }

    public DBAdapter open() throws SQLException {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        if(dbHelper != null) dbHelper.close();
        if(db != null) db.close();
    }

    public void insert(String name, String apInfo) {
        String sql = "INSERT INTO %s (%s, %s) VALUES ('%s', '%s');";
        db.execSQL(String.format(sql, TABLE_NAME, KEY_NAME, KEY_APINFO, name, apInfo));
    }

    public void delete(String name) {
        String sql = "DELETE FROM %s WHERE %s LIKE '%s';";
        db.execSQL(String.format(sql, TABLE_NAME, KEY_NAME, name));
    }

    public ArrayList<ScanItem> get_all_scans() {
        ArrayList<ScanItem> scanItems = new ArrayList<ScanItem>();

        String sql = "SELECT * FROM %s;";
        Cursor c = db.rawQuery(String.format(sql, TABLE_NAME), null);

        if(c != null) {
            while(c.moveToNext()) {
                String name = c.getString(c.getColumnIndex("name"));
                String apInfo = c.getString(c.getColumnIndex("apInfo"));

                scanItems.add(new ScanItem(name, apInfo));
            }

            c.close();
        }

        return scanItems;
    }
}
