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
        final LinkedHashMap<String,HashMap<String,String>> receivedInfo = serIntent.getData();
        String dbName = receivedInfo.get("Database").get("name");
        final String dbPath = receivedInfo.get("Database").get("path");
        final String tableName = receivedInfo.get("Table").get("name");

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

    void myShowDataOnMap(HashMap<String,HashMap<String,String>> dbTbInfo, String selectedItem){
        mMap.clear();

        Log.i(TAG, "select: "+ selectedItem);

        String dbPath = dbTbInfo.get("Database").get("path");
        String mainTable = dbTbInfo.get("Table").get("name");
        String fkTable = dbTbInfo.get(selectedItem).get("fkTable");
        String fkID = dbTbInfo.get(selectedItem).get("fkID");

        String selectSQL = "select * ";
        String fromSQL = "from "+ mainTable + ", "+ fkTable+" ";
        String whereSQL = String.format("where %s.%s = %s.%s",mainTable,selectedItem,fkTable,fkID);
        String querySQL = selectSQL+fromSQL+whereSQL;
        Log.i(TAG, querySQL);
        // open database
        SQLiteDatabase db1 = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
        Cursor cursor = db1.rawQuery(querySQL,null);
        int lat_index = -1;
        int lon_index = -1;
        // get lat, lon index
        if(cursor.moveToFirst()){
            for (int i=0; i<cursor.getColumnCount();i++){
                if(lat_index!=-1 && lon_index != -1){
                    break;
                }
                if(lat_index==-1 && cursor.getColumnName(i).equals("_latitude")){
                    lat_index = i;
                    continue;
                }
                if(lon_index==-1 && cursor.getColumnName(i).equals("_longitude")){
                    lon_index = i;
                }
            }
        }
//        Log.i(TAG, String.format("lat index: %d, lon index: %d",lat_index,lon_index));
        //get data from
        if(cursor.moveToFirst()){
            do {
                for(int i=0;i<cursor.getCount();i++){
                    StringBuilder infoSB= new StringBuilder();
                    LatLng pos = new LatLng(cursor.getDouble(lat_index),cursor.getDouble(lon_index));
                    for(int j=0; j< cursor.getColumnCount();j++){
                        infoSB.append(String.format("%s | ",cursor.getString(j)));
                    }
                    mMap.addMarker(new MarkerOptions().position(pos).title(infoSB.toString()));
                }
            }while(cursor.moveToNext());
        }

        cursor.close();
        // close database
        db1.close();

    }
}
/***
 * to-do-lists
 * todo: join table, get the whole data
 */
