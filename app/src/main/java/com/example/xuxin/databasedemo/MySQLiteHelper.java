package com.example.xuxin.databasedemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by XuXin on 2016/3/18.
 */
public class MySQLiteHelper extends SQLiteOpenHelper{
    public static final String TABLE_Name = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SSN = "ssn";
    public static final String COLUMN_Name = "name";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_Name + "(" + COLUMN_ID
            + " not null, " + COLUMN_SSN
            + " integer primary key, "
            + COLUMN_Name
            + " text not null);";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "default.db";

    public MySQLiteHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Name);
        onCreate(db);
    }
}
