package com.example.xuxin.databasedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

public class DebugActivity extends AppCompatActivity {
    public void CreateAFile(View view){
        //test: create a new file
        String filename = "myfile";
        String outputString = "Hello world!";

        try {
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(outputString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //test: create a new database for test
        // way1
        // create a database
        SQLiteDatabase defaultDb = openOrCreateDatabase("test.db",Context.MODE_PRIVATE,null);
        // create a table
        defaultDb.execSQL("DROP TABLE IF EXISTS users");
        defaultDb.execSQL("CREATE TABLE users (_id INTEGER PRIMARY KEY, name VARCHAR)");
        // insert data
        ContentValues values = new ContentValues();
        values.put("_id",19920629);
        values.put("name","alex");
        defaultDb.insert("users",null,values);
        //close database
        defaultDb.close();
//        // create database? way2
//        MySQLiteHelper database =  new MySQLiteHelper(getApplicationContext());
//        SQLiteDatabase db = database.getWritableDatabase();
//        // insert values
//        ContentValues values = new ContentValues();
//        values.put(MySQLiteHelper.COLUMN_Name,"Xin");
//        values.put(MySQLiteHelper.COLUMN_SSN, 19920629);
//        //values.put(MySQLiteHelper.COLUMN_ID,1);
//        //db.insert(MySQLiteHelper.TABLE_Name,MySQLiteHelper.COLUMN_Name,values);
//        // close
//        database.close();
    }

    public void DeleteFile(View view){
        String filename = "myfile";
        getApplicationContext().deleteFile(filename);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}

/*
Log:
3/13/2016
add two methods: create a file and delete a file
TODO: use try-catch to deal with the exception
3/17/2016
still fail to find the stored files, maybe the database file is not stored or the way to find files is wrong
== the database is stored, but cant to find it by file list method?
== both way can create database files, the problem is to find them
== got the way to find the database in the internal storage, the internal files system is that
..../ files <= call getFilesDir() can get it
    / database <= in this sub directory, the database file is in
 */