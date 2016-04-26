package com.example.xuxin.databasedemo;

import android.Manifest;
import android.app.Activity;
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
import android.support.annotation.VisibleForTesting;
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
    private String TAG = "Read Table ACT";
    public final static String EXTRA_MESSAGE_For_FKTableInfo = "com.example.xuxin.databasedemo.FKTableInfo";
    public final static String EXTRA_MESSAGE_For_InsertDbTbInfo = "com.example.xuxin.databasedemo.InsertDbTbInfo";
    HashMap<String,HashMap<String,String>>_insDbTbInfo = new  HashMap<String,HashMap<String,String>>();
    int _fkID=-1;

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
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    Intent insertDataIntent = new Intent(view.getContext(),InsertDataActivity.class);
                    MySerializableIntent testData = new MySerializableIntent();
                    testData.setData(_insDbTbInfo);
                    insertDataIntent.putExtra(EXTRA_MESSAGE_For_InsertDbTbInfo,testData);
                    startActivity(insertDataIntent);
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
        HashMap<String,String> dbHashMap = new HashMap<>();
        dbHashMap.put("name",received_dbName);
        dbHashMap.put("path",db_path);
        _insDbTbInfo.put("Database",dbHashMap);
        HashMap<String,String> tbHashMap = new HashMap<>();
        tbHashMap.put("name",received_tableName);
        _insDbTbInfo.put("Table",tbHashMap);

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
//        final ArrayList<String> colNameList = new ArrayList<>();
        // to get table info
        Cursor tableInfoCur = db.rawQuery("PRAGMA table_info(" + received_tableName + ")", null);
        if (tableInfoCur.moveToFirst()) {
            // show the table info in the log
            //Log.i("Table info", "cid | name | type | notnull | dflt_value | pk");
            do {
                String tableInfoStr = "";
                for (int i = 0; i<tableInfoCur.getColumnCount();i++) {
                    tableInfoStr = tableInfoStr.concat(tableInfoCur.getString(i) == null ? " | ": tableInfoCur.getString(i)+" | ");
                }
                //Log.i("Table info", tableInfoStr);
                HashMap<String,String> fieldInfo = new HashMap<>();
                fieldInfo.put("type",tableInfoCur.getString(2));
                fieldInfo.put("pk",tableInfoCur.getString(5));
                TableInfo.put(tableInfoCur.getString(1),fieldInfo);
            } while (tableInfoCur.moveToNext());
        }
        tableInfoCur.close();
        Cursor tableFKCur = db.rawQuery("PRAGMA foreign_key_list(" + received_tableName + ")", null);
        //Log.i("FK info", "id | seq | table | from | to | on_update | on_delete | match");
        if(tableFKCur.moveToFirst()) {
            do {
                String fkInfoStr = "";
                for (int i = 0; i < tableFKCur.getColumnCount(); i++) {
                    fkInfoStr = fkInfoStr.concat( tableFKCur.getString(i) == null ? " | " : tableFKCur.getString(i))+" | ";
                }
                //Log.i("FK info", fkInfoStr);
                HashMap<String,String> FKFieldInfo = new HashMap<>();
                FKFieldInfo.put("table",tableFKCur.getString(2));
                FKFieldInfo.put("to",tableFKCur.getString(4));
                FKInfo.put(tableFKCur.getString(3),FKFieldInfo);
            }while (tableFKCur.moveToNext());
        }
        tableFKCur.close();
        myProcessData(TableInfo,FKInfo);

        // ... todo need to improve ...

        String openTableSql = String.format("SELECT * FROM %s;",received_tableName);
        Cursor rec = db.rawQuery(openTableSql, null);

        // because we need to keep the order with the edit text. more info @ convention
        // show column names
        TableRow colTableRow = new TableRow(this);
        for (int i = 0; i < rec.getColumnCount(); i++) {
            TextView colName = new TextView(this);
            String colNameStr = rec.getColumnName(i);
//            colNameList.add(colNameStr);
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
//        // insert data row
//        TableRow insertDataRow = new TableRow(this);
//        for (int j = 0; j < rec.getColumnCount(); j++) {
//            // todo check the field, some of them should not be set by user, like _id, _ starting and FK
//            EditText dataEditText = new EditText(this);
//            dataEditText.setText(String.format("Your %s",rec.getColumnName(j)));
//            insertDataRow.addView(dataEditText);
//        }
//        Button addBt = new Button(this);
//        addBt.setText(R.string.read_a_table_addData_tb);
//        addBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // todo: a lot of to do ... get data and set data
//                TableRow newDataRow = (TableRow) v.getParent();
//                Log.i("InsertOpe", String.format("name: %s, Child count: %d",
//                        newDataRow.toString(),newDataRow.getChildCount()));
//                ArrayList<String> insertDataList = new ArrayList<String>();
//
//                for(int i= 0; i< newDataRow.getChildCount()-1; i++){
//                    EditText et = (EditText) newDataRow.getChildAt(i);
//                    String dataVal = et.getText().toString();
//                    insertDataList.add(dataVal);
//                }
//                // todo check data input data: safe or data is null?
//                insertData(db_path,received_tableName,colNameList,TableInfo,FKInfo,insertDataList);
//                // refresh
//                recreate();
//            }
//        });
//        insertDataRow.addView(addBt);
//        if (tableLayout != null) {
//            tableLayout.addView(insertDataRow);
//        }
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

    void myProcessData(HashMap<String,HashMap<String,String>> TableInfo,
                       HashMap<String,HashMap<String,String>> FKInfo){
        for (String item:TableInfo.keySet()
             ) {
//            Log.i(TAG, item);
            HashMap<String,String> itemHasMap = new HashMap<>();
            HashMap<String,String> itemInfo = TableInfo.get(item);
            itemHasMap.put("type",itemInfo.get("type"));
            itemHasMap.put("pk",itemInfo.get("pk"));
            if(FKInfo.containsKey(item)){
                itemHasMap.put("fk","1");
                itemHasMap.put("fkTable",FKInfo.get(item).get("table"));
                itemHasMap.put("fkID",FKInfo.get(item).get("to"));
            }
            else
            {
                itemHasMap.put("fk","0");
                itemHasMap.put("fkTable","null");
                itemHasMap.put("fkID","null");
            }
            _insDbTbInfo.put(item,itemHasMap);
        }
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch(requestCode) {
//            case (1) : {
//                if (resultCode == Activity.RESULT_OK) {
//                   // todo set FK_ID
//                    _fkID = data.getIntExtra("id",-1);
//                    Log.i("Read a Table",  String.format("Receive FK: %d",_fkID));
//                }
//                break;
//            }
//        }
//    }

    // Internal data leak within a DataBuffer object detected!
//    void insertData(String dbPath, String tbName,
//                    ArrayList<String> colNameList,
//                    HashMap<String,HashMap<String,String>> tableInfo,
//                    HashMap<String,HashMap<String,String>> FKInfo,
//                    ArrayList<String> data){
//        // for now, get all the edit text value, including PK,FK, but in fact we do need them in this way
//        // and in the following functions, we ignore their value from user input
//        // todo set some edit text un-edit table
//        // open database
//        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
//        db.setForeignKeyConstraintsEnabled(true);
//
//        ContentValues insertDataCV = new ContentValues();
//
//        for (String dataStr:data
//             ) {
//            int index = data.indexOf(dataStr);
//            String fieldName = colNameList.get(index);
//            if(fieldName.equals("_deviceInfo")){
//                // it is device geog data
//                // Get the location manager
//                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//                Criteria criteria = new Criteria();
//                String bestProvider = locationManager.getBestProvider(criteria, false);
//                if (ActivityCompat.checkSelfPermission(this,
//                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                        ActivityCompat.checkSelfPermission(this,
//                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Request for permission, Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    Log.e("GPS","No permission");
//                    return;
//                }
//                Location location = locationManager.getLastKnownLocation(bestProvider);
//                Double lat,lon;
//                int deviceID=-1;
//                try {
//                    lat = location.getLatitude();
//                    lon = location.getLongitude();
//                    Log.i("GPS",String.format("Latitude: %f, Longitude: %f",lat,lon));
//                    // find lat, lon in the DeviceInfoTable, if not insert data
//                    // for now, ignore the name, find the first
//                    String gpsSQL = "SELECT * FROM DeviceInfoTable WHERE " +
//                            "_latitude = "+ lat + " AND _longitude = " + lon;
//                    Cursor gpsCur = db.rawQuery( gpsSQL,null);
//                    if(gpsCur.moveToFirst()){
//                        // find
//                        deviceID = gpsCur.getInt(0);
//                    }
//                    else{
//                        // insert data in DeviceInfoTable
//                        ContentValues deviceCV = new ContentValues();
//                        deviceCV.put("_latitude",lat);
//                        deviceCV.put("_longitude",lon);
//                        deviceCV.put("_name","xx");
//                        long  last_ins_id= db.insert("DeviceInfoTable",null,deviceCV);
//                        // insert data in the main table
//                        // SELECT last_insert_rowid();
//                        Cursor newDeviceIDCur = db.rawQuery("SELECT last_insert_rowid()",null);
//                        if(newDeviceIDCur.moveToFirst()){
//                            deviceID = newDeviceIDCur.getInt(0);
//                            Log.i("Last insert ID", String.format("java: %d vs SQL: %d",
//                                    last_ins_id,deviceID));
//                        }
//                        newDeviceIDCur.close();
//                    }
//                    gpsCur.close();
//                }
//                catch (NullPointerException e){
//                    Log.e("GPS",e.getMessage());
//                }
//                if(deviceID>0) {
//                    insertDataCV.put("_deviceInfo", deviceID);
//                }
//            }else{
//                // FK
//                if(FKInfo.containsKey(fieldName)){
//                    // tbName: fieldName <-> FKInfo.get(fieldName).get("table"): FKInfo.get(fieldName).get("to")
//                    String FKTableName = FKInfo.get(fieldName).get("table");
//                    String FKTable_Field = FKInfo.get(fieldName).get("to");
//                    // todo here should jump sth to show the FK table which is geog table
//                    // which list the available items that can be selected
//                    // and can add new geog data, by point the map to get the geog info
//                    // todo how to get a Result from an Activity, result FK_ID
//                    ArrayList<String> fkInfoList = new ArrayList<>();
//                    fkInfoList.add(dbPath);
//                    fkInfoList.add(FKTableName);
//
//                    Intent getFKIntent = new Intent(this.getApplicationContext(), GetFKActivity.class);
//                    getFKIntent.putStringArrayListExtra(EXTRA_MESSAGE_For_FKTableInfo,fkInfoList);
//                    startActivityForResult(getFKIntent, 1);
//                    // todo !!! it will still run ...
//                    // todo use drop list to show available items and add new -> adjust the get FK
//                    int FK_ID=_fkID;
//                    Log.i("FK ID", String.format("TO USE is  %d",FK_ID));
//                    if(FK_ID > 0) {
//                        // this time, we can ignore the incorrect fk
//                        // todo: but we need to ensure fk should be inserted in the database
//                        insertDataCV.put(fieldName, FK_ID);
//                    }
//                    else
//                    {
//                        Log.i("FK ERROR", "FK ID = -1");
//                    }
//                }
//                else
//                {   //not PK, common field
//                    if(tableInfo.get(fieldName).get("pk").equals("0")){
//                        insertDataCV.put(fieldName,dataStr);
//                    }
//                }
//            }
//        }
//        // todo ensure all field with type not null is not null, pk is except, because it is +1 auto
//        if(insertDataCV.size()>0){db.insert(tbName,null,insertDataCV);}
//        //close database
//        db.close();
//    }
}
/**
 * todo : show the data in a table, insert data, edit data and delete data
 * */