package com.example.xuxin.databasedemo;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class InsertDataActivity extends AppCompatActivity {
    //
    // todo change name
    // todo keep order
    public final static String EXTRA_MESSAGE_For_FKTableInfo = "com.example.xuxin.databasedemo.FKTableInfo";

    private String TAG = "Ins/Edit Act";
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
        final String received_ID = receivedIntent.getStringExtra(ReadATableActivity.EXTRA_MESSAGE_For_SelectedID);
        MySerializableIntent serIntent = (MySerializableIntent)receivedIntent.getSerializableExtra(ReadATableActivity.EXTRA_MESSAGE_For_InsertDbTbInfo);
        final LinkedHashMap<String,HashMap<String,String>> receivedInfo = serIntent.getData();
        String dbName = receivedInfo.get("Database").get("name");
        final String dbPath = receivedInfo.get("Database").get("path");
        final String tableName = receivedInfo.get("Table").get("name");

        //logTest(receivedInfo);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.insert_data_table);

        // --------------------------Edit Mode Preparation----------------------------------------//
        HashMap<String,String> editData = new HashMap<>();
        if(!received_ID.equals("-1")){
            // open database
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
            String querySQL= String.format("SELECT * FROM %s WHERE %s._id = %s ",tableName,tableName,received_ID);
            Cursor cur = db.rawQuery(querySQL,null);
            if(cur.moveToFirst()){
                for(int i = 0; i< cur.getColumnCount(); i++){
                    editData.put(cur.getColumnName(i),cur.getString(i));
                }
            }
            cur.close();
            //close database
            db.close();
        }

        // --------------------------Insert/Edit Mode --------------------------------------------//

        //for now get enough information, it is time to insert data
        // order list save the field name...
        // sub button to take insert operation
        final ArrayList<String> fieldNameList = new ArrayList<>();
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
                                // set text if it is a edit mode/activity
                                EditText edit = new EditText(this);
                                if(editData.containsKey(key))
                                {
                                    edit.setText(editData.get(key));
                                }
                                fieldTR.addView(edit);
                            }else {
                                // have ignored the _device info
                                // a fk add spinner
                                Spinner fkSP = new Spinner(this);
                                // read info from table, id:city_name @ lat, lon
                                // select add more, then start get fk act and return, re-create
                                final String FKTableName = itemHashMap.get("fkTable");
                                // consider the edit mode/act
                                String[] mItems  = getFKItems(dbPath,FKTableName,received_ID);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                fkSP.setAdapter(adapter);
                                fkSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        Log.i(TAG, "onItemSelected: "+
                                                parent.getItemAtPosition(position).toString());
                                        if(parent.getItemAtPosition(position).toString().
                                                toLowerCase().equals("add more")){
                                            Intent getFKIntent = new Intent(view.getContext(),GetFKActivity.class);
                                            ArrayList<String> fkInfoList = new ArrayList<>();
                                            fkInfoList.add(dbPath);
                                            fkInfoList.add(FKTableName);

                                            getFKIntent.putStringArrayListExtra(EXTRA_MESSAGE_For_FKTableInfo,fkInfoList);
                                            startActivityForResult(getFKIntent, 1);
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        Log.i(TAG, "onNothingSelected:");
                                    }
                                });
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
                TableLayout dataTable = (TableLayout) v.getParent().getParent();
                Log.i(TAG, dataTable.toString());
                // consider the edit operation
                myInsertData(dbPath,tableName,dataTable,fieldNameList,receivedInfo,received_ID);

            }
        });
        subTableRow.addView(subBT);
        if (tableLayout != null) {
            tableLayout.addView(subTableRow);
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            // 1 comes from get FK
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    long insert_id = data.getLongExtra("id",-1);
                    Log.i(TAG, "Received id: "+ insert_id);
                    recreate();
                }
                break;
            }
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

    String[] getFKItems(String dbPath, String tableName, String id){
        String[] returnString;
        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
        Cursor cur = db.rawQuery("SELECT * FROM "+ tableName +" ",null);
        if (cur.moveToFirst()) {
            String[] items = new String[cur.getCount()+2];
            items[0] = "...";
            items[1] = "Add more";
            int index = 2;
            do{
                items[index] = String.format("%s: %s @ lat: %s, lon: %s",
                        cur.getString(0), cur.getString(1), cur.getString(2), cur.getString(3));
                index++;

                if(cur.getString(0).equals(id)) {
                    items[0] = String.format("%s: %s @ lat: %s, lon: %s",
                            cur.getString(0), cur.getString(1), cur.getString(2), cur.getString(3));
                }
            }while(cur.moveToNext());
            returnString = items;
        }
        else
        {
            returnString = new String[2];
            returnString[0]="...";
            returnString[1]="Add more";
        }

        cur.close();
        //close database
        db.close();
        Log.i(TAG, "getFKItems: " + Arrays.toString(returnString));
        return returnString;
    }

    void myInsertData(
            String dbPath, String tableName,
            TableLayout table, ArrayList<String> fieldNameList,
            HashMap<String,HashMap<String,String>> tableInfo,
            String insertID){

        long last_insert_row ;
        ContentValues insertCV = new ContentValues();

        // insert geog data
        if(tableInfo.keySet().contains("_deviceInfo")){
            myInsertGeogData(insertCV,dbPath);
        }
        // collect data - > do insert action
        for(int i = 0; i < table.getChildCount(); i++){
            try{
                TableRow row = (TableRow) table.getChildAt(i);
                String fieldName = ((TextView) row.getChildAt(0)).getText().toString();
                if(fieldNameList.contains(fieldName)) {
                    if (tableInfo.get(fieldName).get("fk").toLowerCase().equals("0")) {
                        //not a fk
                        String val = ((EditText) row.getChildAt(1)).getText().toString();
                        insertCV.put(fieldName, val);
                    }
                    else
                    {
                     // it is a fk
                        // to parse spinner data, maybe use regex, regular expression
                        Spinner spinner = (Spinner) row.getChildAt(1);
                        String fkInfo =spinner.getSelectedItem().toString();
                        // parse it to get the fk_id
                        if(!fkInfo.toLowerCase().equals("..."))
                            if (!fkInfo.toLowerCase().equals("add more")){
                                String fkID = fkInfo.toLowerCase().substring(0,1);
                                Log.i(TAG, "fk id " + fkID);
                                insertCV.put(fieldName,fkID);
                            }
                    }
                }
            }
            catch (Exception ex){
                Log.e(TAG, ex.getMessage());
            }
        }

        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
        if(!insertID.equals("-1")) {
            last_insert_row = db.update(tableName,insertCV,"_id = ?", new String[]{insertID});
        }
        else {
            last_insert_row = db.insert(tableName, null, insertCV);
        }
        //close database
        db.close();

        // successful go back to previous activity
        Intent resultData = new Intent();
        resultData.putExtra("id",last_insert_row);
        Log.i(TAG, String.format("Send New Insert Data ID: %d",last_insert_row));
        setResult(Activity.RESULT_OK, resultData);
        finish();
    }


    void myInsertGeogData(ContentValues cv, String dbPath){
        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
         //Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Request for permission, Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG,"GPS No permission");
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat,lon;
        long deviceID=-1;
        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
            Log.i(TAG,String.format(" GPS Latitude: %f, Longitude: %f",lat,lon));
            // find lat, lon in the DeviceInfoTable, if not insert data
            // for now, ignore the name, find the first
            String gpsSQL = "SELECT * FROM DeviceInfoTable WHERE " +
                    "_latitude = "+ lat + " AND _longitude = " + lon;
                    Cursor gpsCur = db.rawQuery( gpsSQL,null);
                    if(gpsCur.moveToFirst()){
                        // find
                        deviceID = gpsCur.getLong(0);
                    }
                    else{
                        // insert data in DeviceInfoTable
                        ContentValues deviceCV = new ContentValues();
                        deviceCV.put("_latitude",lat);
                        deviceCV.put("_longitude",lon);
                        deviceCV.put("_name","Test's Nexus 9");  // default name
                        deviceID = db.insert("DeviceInfoTable",null,deviceCV);
                    }
                    gpsCur.close();

                }
                catch (NullPointerException e){
                    Log.e(TAG,e.getMessage());
                }
                if(deviceID>0) {
                    cv.put("_deviceInfo", deviceID);
                }
        // close database
        db.close();
    }

}
