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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

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
        final HashMap<String,HashMap<String,String>> receivedInfo = serIntent.getData();
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Test Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    void myShowDataOnMap(HashMap<String,HashMap<String,String>> DbTbInfo, String selectedItem){
        // for now just show the data
        ArrayList<LatLng> dataLatLng = new ArrayList<>();
        String dbPath = DbTbInfo.get("Database").get("path");
        String fkTable = DbTbInfo.get(selectedItem).get("fkTable");
        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
        Cursor cur = db.rawQuery("SELECT * FROM "+ fkTable,null);
        if(cur.moveToFirst()){
            // find lat, lon index
            int cur_lat_index = -1;
            int cur_lon_index = -1;
            for(int i =0;i< cur.getColumnCount(); i++){
                if(cur.getColumnName(i).equals("_latitude")){
                    cur_lat_index = i;
                }
                if(cur.getColumnName(i).equals("_longitude")){
                    cur_lon_index = i;
                }
            }
           if(cur_lat_index != -1 && cur_lon_index != -1){
               do{
                   Double lat = cur.getDouble(cur_lat_index);
                   Double lon = cur.getDouble(cur_lon_index);
                  dataLatLng.add(new LatLng(lat,lon));
               }while(cur.moveToNext());
           }

        }
        cur.close();
        // close database
        db.close();
        mMap.clear();
        for (LatLng pos: dataLatLng
             ) {
            mMap.addMarker(new MarkerOptions().position(pos));
        }

    }
}
