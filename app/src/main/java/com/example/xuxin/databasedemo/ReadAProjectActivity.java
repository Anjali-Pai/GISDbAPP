package com.example.xuxin.databasedemo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.ArrayList;
/***
 * show all the tables in the database
 * click buttons to open/read table - then can edit them
 * delete table
 * */
public class ReadAProjectActivity extends AppCompatActivity {
    private String TAG = "Read A Pro";

    public final static String EXTRA_MESSAGE_For_DbTbName = "com.example.xuxin.databasedemo.DbTbName";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_a_project);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TableLayout tableTableLayout = (TableLayout) findViewById(R.id.read_a_project_tableTableLayout);
        Intent intent = getIntent();
        final String received_dbName = intent.getStringExtra(ReadProjectsActivity.EXTRA_MESSAGE_For_DbName);
        final String db_path = getFilesDir().getParent()+"/databases/"+received_dbName;
        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(db_path,null, Context.MODE_PRIVATE);
        // show the tables in the database
        // show the database info
        // --- context info ---
        // ref: http://bxbxbai.github.io/2014/07/16/context/
        // http://stackoverflow.com/questions/12610995/what-does-getactivity-mean

        ArrayList<String> tableNames = new ArrayList<String >();
        Cursor tablesName_cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        // show existed tables
        if (tablesName_cursor.moveToFirst()) {
            //table_name = c.getString(0);
            while (!tablesName_cursor.isAfterLast()) {
                tableNames.add(tablesName_cursor.getString(0));
                tablesName_cursor.moveToNext();
            }
        }
        tablesName_cursor.close();

        for (final String table_name:tableNames
             ) {
            final ArrayList<String> intentInfoList = new ArrayList<String>();
            intentInfoList.add(received_dbName);
            intentInfoList.add(table_name);

            TableRow nameRow = new TableRow(this);
            TextView nameTextView = new TextView(this);
            nameTextView.setText(table_name);
            nameRow.addView(nameTextView);
            final Button readBt = new Button(this);
            Button delBt = new Button(this);

            readBt.setText(R.string.read_a_project_read_tb);
            delBt.setText(R.string.read_a_project_delete_tb);

            readBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent readIntent = new Intent(v.getContext(),ReadATableActivity.class);
                    readIntent.putStringArrayListExtra(EXTRA_MESSAGE_For_DbTbName,intentInfoList);
                    startActivity(readIntent);
                }
            });
            delBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // delete table, it is kind of hard to do with FK
                    // for not not do it...

                }
            });

            nameRow.addView(readBt);
            nameRow.addView(delBt);

            if (tableTableLayout != null) {
                tableTableLayout.addView(nameRow);
            }
        }
        // close database
        if(db.isOpen()){db.close();}

   }

}
/***
 * todo: use fragments
 * todo: show to whole data in one table, read-only
 * */