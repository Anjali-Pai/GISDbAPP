package com.example.xuxin.databasedemo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ReadATableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_a_table);
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

        TableLayout tableLayout = (TableLayout) findViewById(R.id.read_a_table_tableTableLayout);
        Intent intent = getIntent();
        // database name + Table name
        ArrayList<String> DbTbList = intent.getStringArrayListExtra(ReadAProjectActivity.EXTRA_MESSAGE_For_DbTbName);
        String received_dbName = DbTbList.get(0);
        final String db_path = getFilesDir().getParent()+"/databases/"+received_dbName;
        final String received_tableName = DbTbList.get(1);

        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(db_path,null, Context.MODE_PRIVATE);
        // ...
        String openTableSql = String.format("SELECT * FROM %s;",received_tableName);
        Cursor rec = db.rawQuery(openTableSql, null);
        // show column names
        TableRow colTableRow = new TableRow(this);
        for (int i = 0; i < rec.getColumnCount(); i++) {
            TextView colName = new TextView(this);
            // ref: http://stackoverflow.com/questions/1528988/create-tablelayout-programatically
            colName.setText(rec.getColumnName(i));
            colTableRow.addView(colName);
        }
        if (tableLayout != null) {
            tableLayout.addView(colTableRow);
        }
        // red line
        TextView redLine= new TextView(this);
        redLine.setHeight(2);
        redLine.setBackgroundColor(Color.RED);
        if (tableLayout != null) {
            tableLayout.addView(redLine);
        }
        // insert data row
        TableRow insertDataRow = new TableRow(this);
        for (int j = 0; j < rec.getColumnCount(); j++) {
            // todo check the field, some of them should not be set by user, like _id, _ starting and FK
            EditText dataEditText = new EditText(this);
            dataEditText.setText(String.format("Your %s",rec.getColumnName(j)));
            insertDataRow.addView(dataEditText);
        }
        Button addBt = new Button(this);
        addBt.setText(R.string.read_a_table_addData_tb);
        addBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: get data and set data
                // insertData();

            }
        });
        insertDataRow.addView(addBt);
        if (tableLayout != null) {
            tableLayout.addView(insertDataRow);
        }
        // show data
        rec.moveToFirst();
        for (int i = 0; i < rec.getCount(); i++) {
            TableRow dataTableRow = new TableRow(this);
            for (int j = 0; j < rec.getColumnCount(); j++) {
                TextView dataTextView = new TextView(this);
                dataTextView.setText(rec.getString(j));
                dataTableRow.addView(dataTextView);
            }
            if (tableLayout != null) {
                tableLayout.addView(dataTableRow);
            }
        }
        // close rec
        rec.close();
        // close database
        if(db.isOpen()){db.close();}
    }

    void insertData(String dbPath, String tbName, HashMap<String, String> data){

    }
}
/**
 * todo : show the data in a table, insert data, edit data and delete data
 * */