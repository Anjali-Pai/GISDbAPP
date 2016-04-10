package com.example.xuxin.databasedemo;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
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

import java.io.FileOutputStream;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DebugActivity extends AppCompatActivity implements OnMapReadyCallback {
    public void CreateAFile(View view) {
        //test: create a new file
        String filename = "myfile";
        String outputString = "Hello world!";

        try {
            FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(outputString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //test: create a new database for test
        // way1
        // create a database
        SQLiteDatabase defaultDb = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        // create a table
        defaultDb.execSQL("DROP TABLE IF EXISTS users");
        defaultDb.execSQL("CREATE TABLE users (_id INTEGER PRIMARY KEY, name VARCHAR)");
        // insert data
        ContentValues values = new ContentValues();
        values.put("_id", 19920629);
        values.put("name", "alex");
//        values.put("city","Nanjing");
//        values.put("country","China");
        defaultDb.insert("users", null, values);
        //close database
        defaultDb.close();
//        // create database? way2
//        MySQLiteHelper database =  new MySQLiteHelper(getApplicationContext());
//        SQLiteDatabase db = database.getWritableDatabase();
//        // insert values
//        ContentValues values = new ContentValues();
//        values.put(MySQLiteHelper.COLUMN_Name,"Xin");
//        values.put(MySQLiteHelper.COLUMN_SSN, 19920629);
//        //values.put(MySQLiteHelper.COLUMN_ID,1);
//        //db.insert(MySQLiteHelper.TABLE_Name,MySQLiteHelper.COLUMN_Name,values);
//        // close
//        database.close();
    }

    public void DeleteFile(View view) {
        String filename = "myfile";
        getApplicationContext().deleteFile(filename);
    }

    public void GPSInfo(View view) {
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

        }
        catch (NullPointerException e){
            Log.e("GPS",e.getMessage());

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.debug_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // add marker
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
        // touch to get lat and lng
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){

            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("POS",String.format("Lat:%f, Lng:%f",latLng.latitude,latLng.longitude));
            }
        } );
    }
}

/**
3/13/2016
TODO: use try-catch to deal with the exception
*/