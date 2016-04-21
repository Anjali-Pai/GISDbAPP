package com.example.xuxin.databasedemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Show existed databases in whole app root path
 * Click database to show the data, all the data
 * click del button to delete the database 
 * */
public class ReadProjectsActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE_For_DbName = "com.example.xuxin.databasedemo.DbName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_projects);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        TableLayout dbTableLayout = (TableLayout) findViewById(R.id.read_projects_dbTableLayout);

        File filesDir = getFilesDir();
        File app_root_file = filesDir.getParentFile();
        File[] files = app_root_file.listFiles();
        ArrayList<File> db_files = new ArrayList<File>();
        // get all the database file
        if(files.length>0){
            for (File one_file: files
                 ) {
                if(one_file.isDirectory() && one_file.getName().toLowerCase().contains("database")){
                    // this is a database directory
                    Collections.addAll(db_files, one_file.listFiles()); // so damn cool
                }
            }
        }
        // add databases to the table layout
        for (File f:db_files
             ) {
            final String dbFileName = f.getName();
            TableRow dbTableRow = new TableRow(this);
            TextView dbTextView = new TextView(this);
            dbTextView.setText(dbFileName);
            dbTableRow.addView(dbTextView);
            // add button to read and delete
            Button readBt = new Button(this);
            readBt.setText(R.string.read_projects_read_db);
            readBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),ReadAProjectActivity.class);
                    intent.putExtra(EXTRA_MESSAGE_For_DbName,dbFileName);
                    startActivity(intent);
                }
            });
            dbTableRow.addView(readBt);

            Button delBt = new Button(this);
            delBt.setText(R.string.read_projects_delete_db);
            delBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDatabase(dbFileName);
                    recreate();
                }
            });
            dbTableRow.addView(delBt);

            if (dbTableLayout != null) {
                dbTableLayout.addView(dbTableRow);
            }
        }
    }
}

//  simple click the database to open it, the debug info just show in the Log
/***
 * todo: use one fragment to show a list of database on the left and another fragment to display tables in the middle and the table content on the right
 */
//  apply scroll view in case there are too much information to show in one screen
/** todo questions:
 * 1. many fragments in the container, how to replace it, with order, and in our will
 */

