package com.example.xuxin.databasedemo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DataVisualActivity extends FragmentActivity implements OnMapReadyCallback {

    private String TAG = "Data Visual ACT";
    private GoogleMap mMap;
    private ArrayList<LinkedHashMap<String,String>> _wholeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_visual);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get table data -> extract the geog data -> show them

        Intent receivedIntent = getIntent();
        MySerializableIntent serIntent = (MySerializableIntent)receivedIntent.getSerializableExtra(ReadATableActivity.EXTRA_MESSAGE_For_InsertDbTbInfo);
        Spinner spinner = (Spinner) findViewById(R.id.data_visual_spinner);
        final HashMap<String,HashMap<String,String>> receivedInfo = serIntent.getData();
        String dbName = receivedInfo.get("Database").get("name");
        final String dbPath = receivedInfo.get("Database").get("path");
        final String tableName = receivedInfo.get("Table").get("name");

        // to get the whole data
        _wholeData = myGetWholeData(receivedInfo);

        ArrayList<String> fkNames = new ArrayList<>();
        for (String key: receivedInfo.keySet()
             ) {
            if(!key.toLowerCase().equals("database")){
                if(!key.toLowerCase().equals("table")){
                    if(receivedInfo.get(key).get("fk").equals("1")){
                        fkNames.add(key);
                    }
                }
            }
        }
        String[] fkTables = new String[fkNames.size()];
        for (int i = 0; i< fkNames.size();i++){
            fkTables[i] = fkNames.get(i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, fkTables);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                myShowDataOnMap(receivedInfo,selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "onNothingSelected:");
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    ArrayList<LinkedHashMap<String,String>> myGetWholeData(HashMap<String,HashMap<String,String>> dbInfo){
        ArrayList<LinkedHashMap<String,String>> outData = new ArrayList<>();
        // get database info
        String dbName = dbInfo.get("Database").get("name");
        String dbPath = dbInfo.get("Database").get("path");
        String tableName = dbInfo.get("Table").get("name");

        String selectSQL = "select * ";
        StringBuilder fromSB = new StringBuilder();
        StringBuilder whereSB = new StringBuilder();
        whereSB.append("where");
        fromSB.append(String.format("from %s", tableName));
        for (String key: dbInfo.keySet()
             ) {
            if(!key.toLowerCase().equals("database"))
                if(!key.toLowerCase().equals("table"))
                if(dbInfo.get(key).get("fk").equals("1")){
                    fromSB.append(String.format(", %s",dbInfo.get(key).get("fkTable")));
                    whereSB.append(String.format(" %s.%s = %s.%s and",tableName,key,
                            dbInfo.get(key).get("fkTable"),
                            dbInfo.get(key).get("fkID")));
                }
        }
        String fromSQL = fromSB.toString();
        String whereSQL = whereSB.toString().substring(0,whereSB.toString().length()-3);
        String querySQL = selectSQL+fromSQL+" "+whereSQL;
        Log.i(TAG, querySQL);

        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
        Cursor cur = db.rawQuery(querySQL,null);
        //...
        for(int i=0; i<cur.getColumnCount();i++) {
            Log.i(TAG, String.format("%d:%s",i,cur.getColumnName(i)));
        }
        if(cur.moveToFirst()){
            do {
                LinkedHashMap<String,String> rowData = new LinkedHashMap<>();
                for(int i=0;i<cur.getColumnCount();i++){
                    rowData.put(cur.getColumnName(i),cur.getString(i));
                }
                outData.add(rowData);
            }while (cur.moveToNext());
        }
        cur.close();
        // close database
        db.close();
        return outData;

    }

    void myShowDataOnMap(HashMap<String,HashMap<String,String>> dbTbInfo, String selectedItem){
        // for now just show the data
        Log.i(TAG, "select: "+ selectedItem);

        ArrayList<LatLng> dataLatLng = new ArrayList<>();
        String dbPath = dbTbInfo.get("Database").get("path");
        String mainTable = dbTbInfo.get("Table").get("name");
        String fkTable = dbTbInfo.get(selectedItem).get("fkTable");

        ArrayList<String> existedFKids = new ArrayList<>();

        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
        Cursor cur = db.rawQuery("SELECT " + selectedItem +" FROM "+ mainTable,null);
        // need to consider geog data
        if(cur.moveToFirst()){
            do{
                // todo danger, whether it is null or not
                existedFKids.add(cur.getString(0));
                Log.i(TAG, "existed fk id:" + cur.getString(0));
            }while(cur.moveToNext());
        }
        else
        {
            Log.i(TAG, "find existed fk id failed");
        }
        cur.close();

        for (String exId:existedFKids
             ) {
            Cursor fkCur = db.rawQuery("SELECT * FROM "+ fkTable +" WHERE _id = ?",new String[]{exId});
            // find lat, lon index
            int cur_lat_index = -1;
            int cur_lon_index = -1;
            int cur_id_index = -1;
            if(fkCur.moveToFirst()) {
                for (int i = 0; i < fkCur.getColumnCount(); i++) {
                    if (fkCur.getColumnName(i).equals("_latitude")) {
                        cur_lat_index = i;
                        continue;
                    }
                    if (fkCur.getColumnName(i).equals("_longitude")) {
                        cur_lon_index = i;
                        continue;
                    }
                    if(fkCur.getColumnName(i).equals("_id")){
                        cur_id_index = i;
                    }
                }
            }
            if(fkCur.moveToFirst()) {
                if(cur_lat_index != -1 && cur_lon_index != -1 && cur_id_index !=-1)
                    {
                        do{
                            Double lat = fkCur.getDouble(cur_lat_index);
                            Double lon = fkCur.getDouble(cur_lon_index);
                            dataLatLng.add(new LatLng(lat,lon));
                        }while(fkCur.moveToNext());
                    }

            }
            fkCur.close();
        }

        // close database
        db.close();
        mMap.clear();
        for (LatLng pos: dataLatLng
             ) {
            mMap.addMarker(new MarkerOptions().position(pos));
        }

    }
}
/***
 * to-do-lists
 * todo: join table, get the whole data
 */
