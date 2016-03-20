package com.example.xuxin.databasedemo;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

public class DatabaseCRUDActivity extends AppCompatActivity {
    private DatabaseCRUDHelper  dbcrudhelper = new DatabaseCRUDHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_crud);
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

        TextView textView = (TextView) findViewById(R.id.DbCURD_info);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.dbcrud_table);
        Intent intent = getIntent();
        String received_dbname = intent.getStringExtra(ReadProjectsActivity.EXTRA_MESSAGE_For_DbName);
        textView.setText(String.format("Database name: %s\n", received_dbname));
        String db_path = getFilesDir().getParent()+"/databases/"+received_dbname;
        textView.append(String.format("Access to: %s\n",db_path));
        SQLiteDatabase db = SQLiteDatabase.openDatabase(db_path,null, Context.MODE_PRIVATE);
        // show the database info
        // ref: http://bxbxbai.github.io/2014/07/16/context/
        dbcrudhelper.readSelectedDatabase(this,textView,tableLayout,db); // in this method, context should be activity context

    }


}

/**
 * log
 * 03/19/2016
 * listview and gridview does not support the addView method
 * tablelayout support it
 * but, it maybe to difficult to handle the onclick event...
 * for now use table layout
 *
 */
