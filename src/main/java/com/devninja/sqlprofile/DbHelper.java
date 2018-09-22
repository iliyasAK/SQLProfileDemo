package com.devninja.sqlprofile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    private final String TAG = getClass().getSimpleName() + " MyLog";

    public DbHelper(Context context) {
        super(context, DbEntries.DB_NAME, null, DbEntries.DB_VERSION);
    }

    public void queryData(String sql){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }
    public void insertData(String name, String age, String phone, byte[] picture){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("age", age);
        values.put("phone", phone);
        values.put("picture", picture);

        db.insert(DbEntries.TABLE_NAME, null, values);
        db.close();
    }

    public int updateData(String name, String age, String phone, byte[] picture, int id){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbEntries.COL_NAME, name);
        values.put(DbEntries.COL_AGE, age);
        values.put(DbEntries.COL_PHONE, phone);
        values.put(DbEntries.COL_IMG, picture);

        String whereClause = DbEntries.COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        int rows = db.update(DbEntries.TABLE_NAME, values, whereClause, whereArgs);
        db.close();
    return rows;
    }

    public int deleteData(int id){
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = DbEntries.COL_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};

        int count = db.delete(DbEntries.TABLE_NAME, whereClause, whereArgs);
        db.close();
        return count;
    }

    public Cursor getData(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(DbEntries.TABLE_NAME, null, null, null, null, null, null);

    return cursor;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbEntries.CREATE_TABLE);
        Log.w(TAG, "Table Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbEntries.DROP_TABLE);
        onCreate(db);
    }
}
