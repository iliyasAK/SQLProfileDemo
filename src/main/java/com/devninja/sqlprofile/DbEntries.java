package com.devninja.sqlprofile;

public class DbEntries {

    public static final String DB_NAME = "Profiles.db";
    public static final String TABLE_NAME = "Profile";
    public static final int DB_VERSION = 1;
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_AGE = "age";
    public static final String COL_PHONE = "phone";
    public static final String COL_IMG = "picture";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " + COL_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME + " VARCHAR, " + COL_AGE +
            " VARCHAR, " + COL_PHONE + " VARCHAR, " + COL_IMG + " BLOB);";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
}
