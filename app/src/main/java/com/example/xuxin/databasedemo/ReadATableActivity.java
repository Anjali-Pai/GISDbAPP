package com.example.xuxin.databasedemo;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
        db.setForeignKeyConstraintsEnabled(true);
        // todo: read table info: column, PK, FK
        // todo: re-design the data structure to store the table info and FK info
        // store in the hash map: key-value: name: type = > xx: common type, _id: PK
        // ref: http://stackoverflow.com/questions/14721484/get-name-and-type-from-pragma-table-info
        // ref: http://stackoverflow.com/questions/11753871/getting-the-type-of-a-column-in-sqlite
        // ref: https://www.sqlite.org/pragma.html
        // cid | name | type | notnull | dflt_value | pk
        final HashMap<String,HashMap<String,String>> TableInfo = new HashMap<String, HashMap<String,String>>();
        final HashMap<String,HashMap<String,String>> FKInfo = new HashMap<String, HashMap<String,String>>();
        final ArrayList<String> colNameList = new ArrayList<>();
        // to get table info
        Cursor tableInfoCur = db.rawQuery("PRAGMA table_info(" + received_tableName + ")", null);
        if (tableInfoCur.moveToFirst()) {
            // show the table info in the log
            Log.i("Table info", "cid | name | type | notnull | dflt_value | pk");
            do {
                String tableInfoStr = "";
                for (int i = 0; i<tableInfoCur.getColumnCount();i++) {
                    tableInfoStr = tableInfoStr.concat(tableInfoCur.getString(i) == null ? " | ": tableInfoCur.getString(i)+" | ");
                }
                Log.i("Table info", tableInfoStr);
                HashMap<String,String> fieldInfo = new HashMap<>();
                fieldInfo.put("type",tableInfoCur.getString(2));
                fieldInfo.put("pk",tableInfoCur.getString(5));
                TableInfo.put(tableInfoCur.getString(1),fieldInfo);
            } while (tableInfoCur.moveToNext());
        }
        tableInfoCur.close();
        Cursor tableFKCur = db.rawQuery("PRAGMA foreign_key_list(" + received_tableName + ")", null);
        Log.i("FK info", "id | seq | table | from | to | on_update | on_delete | match");
        if(tableFKCur.moveToFirst()) {
            do {
                String fkInfoStr = "";
                for (int i = 0; i < tableFKCur.getColumnCount(); i++) {
                    fkInfoStr = fkInfoStr.concat( tableFKCur.getString(i) == null ? " | " : tableFKCur.getString(i))+" | ";
                }
                Log.i("FK info", fkInfoStr);
                HashMap<String,String> FKFieldInfo = new HashMap<>();
                FKFieldInfo.put("table",tableFKCur.getString(2));
                FKFieldInfo.put("to",tableFKCur.getString(4));
                FKInfo.put(tableFKCur.getString(3),FKFieldInfo);
            }while (tableFKCur.moveToNext());
        }
        tableFKCur.close();

        // ... todo need to improve ...

        String openTableSql = String.format("SELECT * FROM %s;",received_tableName);
        Cursor rec = db.rawQuery(openTableSql, null);

        // because we need to keep the order with the edit text. more info @ convention
        // show column names
        TableRow colTableRow = new TableRow(this);
        for (int i = 0; i < rec.getColumnCount(); i++) {
            TextView colName = new TextView(this);
            String colNameStr = rec.getColumnName(i);
            colNameList.add(colNameStr);
            // ref: http://stackoverflow.com/questions/1528988/create-tablelayout-programatically
            colName.setText(colNameStr);
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
                // todo: a lot of to do ... get data and set data
                TableRow newDataRow = (TableRow) v.getParent();
                Log.i("InsertOpe", String.format("name: %s, Child count: %d",
                        newDataRow.toString(),newDataRow.getChildCount()));
                ArrayList<String> insertDataList = new ArrayList<String>();

                for(int i= 0; i< newDataRow.getChildCount()-1; i++){
                    EditText et = (EditText) newDataRow.getChildAt(i);
                    String dataVal = et.getText().toString();
                    insertDataList.add(dataVal);
                }
                // todo check data input data: safe or data is null?
                insertData(db_path,received_tableName,colNameList,TableInfo,FKInfo,insertDataList);
                // refresh
                recreate();
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
                // todo add button delete and edit button
            }
            if (tableLayout != null) {
                tableLayout.addView(dataTableRow);
            }
            if (!rec.isAfterLast()) {
                rec.moveToNext();
            }
        }
        // close rec
        rec.close();
        // close database
        if(db.isOpen()){db.close();}
    }

    // Internal data leak within a DataBuffer object detected!
    void insertData(String dbPath, String tbName,
                    ArrayList<String> colNameList,
                    HashMap<String,HashMap<String,String>> tableInfo,
                    HashMap<String,HashMap<String,String>> FKInfo,
                    ArrayList<String> data){
                // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
        db.setForeignKeyConstraintsEnabled(true);

        ContentValues insertSBData = new ContentValues();

        for (String dataStr:data
             ) {
            int index = data.indexOf(dataStr);
            String fieldName = colNameList.get(index);
            if(fieldName.equals("_deviceInfo")){
                // it is device geog data
                // Get the location manager
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String bestProvider = locationManager.getBestProvider(criteria, false);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Request for permission, Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.e("GPS","No permission");
                    return;
                }
                Location location = locationManager.getLastKnownLocation(bestProvider);
                Double lat,lon;
                try {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    Log.i("GPS",String.format("Latitude:%f, Longitude:%f",lat,lon));
                    // find lat, lon in the DeviceInfoTable, if not insert data
                    // for now, ignore the name, find the first
                    String gpsSQL = "SELECT * FROM DeviceInfoTable WHERE " +
                            "_latitude = "+ lat + " AND _longitude = " + lon;
                    Cursor gpsCur = db.rawQuery( gpsSQL,null);
                    if(gpsCur.moveToFirst()){
                        int deviceID = gpsCur.getInt(0);
                        ContentValues existed_deviceCV = new ContentValues();
                        existed_deviceCV.put("_deviceInfo",deviceID);
                        db.insert(tbName,null,existed_deviceCV);
                    }
                    else{
                        // insert data in DeviceInfoTable
                        ContentValues deviceCV = new ContentValues();
                        deviceCV.put("_latitude",lat);
                        deviceCV.put("_longitude",lon);
                        deviceCV.put("_name","xx");
                        db.insert("DeviceInfoTable",null,deviceCV);
                        // insert data in the main table
                        // SELECT last_insert_rowid();
                        Cursor newDeviceIDCur = db.rawQuery("SELECT last_insert_rowid()",null);
                        if(newDeviceIDCur.moveToFirst()){
                            int deviceID = newDeviceIDCur.getInt(0);
                            ContentValues cv = new ContentValues();
                            cv.put("_deviceInfo",deviceID);
                            db.insert(tbName,null,cv);
                        }
                        newDeviceIDCur.close();
                    }
                    gpsCur.close();


                }
                catch (NullPointerException e){
                    Log.e("GPS",e.getMessage());
                }


            }else{
                // FK
                if(FKInfo.containsKey(fieldName)){
                    // tbName: fieldName <-> FKInfo.get(fieldName).get("table"): FKInfo.get(fieldName).get("to")
                    String FKTable = FKInfo.get(fieldName).get("table");
                    String FKTable_Field = FKInfo.get(fieldName).get("to");
                    // todo here should jump sth to show the FK table which is geog table
                    // which list the available items that can be selected
                    // and can add new geog data, by point the map to get the geog info
                    // todo how to get return the value from the next activity?
                }
                else
                {   //not PK
                    if(!tableInfo.get(fieldName).get("pk").equals("1")){
                        insertSBData.put(colNameList.get(index),dataStr);
                    }
                }
            }
            if(insertSBData.size()>0){db.insert(tbName,null,insertSBData);}

        }
        //close database
        db.close();
    }
}
/**
 * todo : show the data in a table, insert data, edit data and delete data
 * */