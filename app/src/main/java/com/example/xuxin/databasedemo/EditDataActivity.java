package com.example.xuxin.databasedemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class EditDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);
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

        Intent receivedIntent = getIntent();
        String selected_ID = receivedIntent.getStringExtra(ReadATableActivity.EXTRA_MESSAGE_For_SelectedID);
        MySerializableIntent serIntent = (MySerializableIntent)receivedIntent.getSerializableExtra(ReadATableActivity.EXTRA_MESSAGE_For_EditDbTbInfo);
        final HashMap<String,HashMap<String,String>> receivedInfo = serIntent.getData();
        String dbName = receivedInfo.get("Database").get("name");
        final String dbPath = receivedInfo.get("Database").get("path");
        final String tableName = receivedInfo.get("Table").get("name");

        Button submitBt = (Button) findViewById(R.id.edit_data_submit);
        TableLayout table = (TableLayout) findViewById(R.id.edit_data_edit_table);

        // show the data



    }

}