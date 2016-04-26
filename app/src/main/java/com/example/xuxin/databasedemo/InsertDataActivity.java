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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InsertDataActivity extends AppCompatActivity {

    private String TAG = "Ins Act";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);
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
        MySerializableIntent serIntent = (MySerializableIntent)receivedIntent.getSerializableExtra(ReadATableActivity.EXTRA_MESSAGE_For_InsertDbTbInfo);
        HashMap<String,HashMap<String,String>> receivedInfo = serIntent.getData();
        String dbName = receivedInfo.get("Database").get("name");
        String dbPath = receivedInfo.get("Database").get("path");
        String tableName = receivedInfo.get("Table").get("name");

        //logTest(receivedInfo);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.insert_data_table);
        //for now get enough information, it is time to insert data
        // order list save the field name...
        // sub button to take insert operation
        ArrayList<String> fieldNameList = new ArrayList<>();
        for (String key:receivedInfo.keySet()
             ) {
            if(!key.toLowerCase().equals("database"))
                if(!key.toLowerCase().equals("table"))
                    if(!key.toLowerCase().equals("_deviceinfo"))
                        if(receivedInfo.get(key).get("pk").toLowerCase().equals("0")){
                            HashMap<String,String> itemHashMap = receivedInfo.get(key);
                            fieldNameList.add(key);
                            TableRow fieldTR = new TableRow(this);
                            TextView textview = new TextView(this);
                            textview.setText(key);
                            fieldTR.addView(textview);
                            if(itemHashMap.get("fk").equals("0")){
                                // not a fk
                                EditText edit = new EditText(this);
                                fieldTR.addView(edit);
                            }else {
                                // ignore the _device info
                                // a fk add spinner
                                Spinner fkSP = new Spinner(this);
                                // todo ....
                                // read info from table, id:city_name @ lat, lon
                                // select add more, then start get fk act and return, re-create
                                // todo need to adjust manifest file if necessary
                                String[] mItems  = getFKItems(dbPath,itemHashMap.get("fkTable"));
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                fkSP.setAdapter(adapter);
                                fieldTR.addView(fkSP);
                            }
                            if (tableLayout != null) {
                                tableLayout.addView(fieldTR);
                        }
                    }
        }
        TableRow subTableRow = new TableRow(this);
        Button subBT = new Button(this);
        subBT.setText(R.string.accept);
        subBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // insert all the data
                //
            }
        });
        subTableRow.addView(subBT);
        if (tableLayout != null) {
            tableLayout.addView(subTableRow);
        }



    }

    void logTest(HashMap<String,HashMap<String,String>> receivedInfo){
        String dbName = receivedInfo.get("Database").get("name");
        String dbPath = receivedInfo.get("Database").get("path");
        String tableName = receivedInfo.get("Table").get("name");
        Log.i(TAG, dbName+":"+dbPath+":"+tableName);
        for (String key:receivedInfo.keySet()
                ) {
            if(!key.toLowerCase().equals("database")){
                if(!key.toLowerCase().equals("table")){
                    Log.i(TAG, key);
                    HashMap<String,String> itemInfo = receivedInfo.get(key);
                    for (String str: itemInfo.keySet()
                            ) {
                        Log.i(TAG, str+":"+itemInfo.get(str));
                    }
                }
            }

        }
    }

    String[] getFKItems(String dbPath, String tableName){
        String[] returnString;
        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
        Cursor cur = db.rawQuery("SELECT * FROM "+ tableName +" ",null);
        if (cur.moveToFirst()) {
            String[] items = new String[cur.getCount()+1];
            items[0] = "Add more";
            int index = 1;
            do{
                items[index] = String.format("%s:%s @ lat: %s, lon: %s",
                        cur.getString(0),cur.getString(1),cur.getString(2),cur.getString(3));
                index++;
            }while(cur.moveToNext());
            returnString = items;
        }
        else
        {
            returnString = new String[1];
            returnString[0]="Add more";
        }

        cur.close();
        //close database
        db.close();
        return returnString;
    }


}
