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
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

import com.google.android.gms.drive.internal.StringListResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DebugActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String TAG ="Debug Act";
    int _test_num;
    GoogleMap _mymap;
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
        ContentValues values2 = new ContentValues();
        values.put("_id", 19920629);
        values.put("name", "alex");
        values2.put("_id", 19870705);
        values2.put("name", "ccc");
//        values.put("city","Nanjing");
//        values.put("country","China");
        defaultDb.insert("users", null, values);
        defaultDb.insert("users", null, values2);
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

    public void MutliTables(View view){
        /***
         * main table: _id,name,geog_id
         * geog table: _id,name,latitude,longitude
         * relationship 1:1
         * crud operation process in log
         * show data in the map
         *
         */
        // create a database
        SQLiteDatabase tablesDb = openOrCreateDatabase("mutlitablestest.db", Context.MODE_PRIVATE, null);
        // create a table
        tablesDb.execSQL("DROP TABLE IF EXISTS cities;");
        tablesDb.execSQL("DROP TABLE IF EXISTS geogdata;");
        tablesDb.execSQL("CREATE TABLE cities (_id INTEGER PRIMARY KEY, name VARCHAR, geog_id INTEGER);");
        tablesDb.execSQL("CREATE TABLE geogdata (_id INTEGER PRIMARY KEY, name VARCHAR, geog_lat REAL, geog_long REAL);");

        // insert data
        ContentValues values = new ContentValues();
        values.put("_id", 1);
        values.put("name", "HOME:Nanjing");
        values.put("geog_id",1);
        tablesDb.insert("cities", null, values);
        ContentValues geogcv = new ContentValues();
        geogcv.put("_id",1);
        geogcv.put("name","Nanjing");
        geogcv.put("geog_lat",32.0616667);
        geogcv.put("geog_long",118.7777778);
        tablesDb.insert("geogdata",null,geogcv);

        // find nanjing geog data to show data in the map (call helper function)
        String query_sql = "";
        //Cursor c = db.rawQuery("SELECT * FROM person WHERE age >= ?", new String[]{"33"});
        //Cursor mCursor = db.rawQuery("SELECT * FROM Table1, Table2 " +"WHERE Table1.id = Table2.id_table1 " +"GROUP BY Table1.data1", null);
        //ref: http://stackoverflow.com/questions/11029538/sqlite-query-from-multiple-tables-using-sqlitedatabase
        Cursor c = tablesDb.rawQuery("SELECT geogdata.* FROM cities,geogdata WHERE cities.geog_id = geogdata._id", null);
        c.moveToFirst();
        Double mylat,mylong;
        for (int i=0;i<c.getColumnCount();i++){
            Log.i("Info", String.format("%s: %s",c.getColumnName(i),c.getString(i)));
        }
        mylat = Double.parseDouble(c.getString(2));
        mylong = Double.parseDouble(c.getString(3));
        String mytitle = c.getString(1);
        _mymap.addMarker(new MarkerOptions()
        .position(new LatLng(mylat,mylong))
        .title(mytitle));
        // call the show data in map
        // async...?
        c.close();
        //close database
        tablesDb.close();
    }

    public void ResultFromAct(View view){
        Intent getFKIntent = new Intent(this.getApplicationContext(), GetFKActivity.class);
        startActivityForResult(getFKIntent, 1);
    }

    /**
     * getDir
     * http://developer.android.com/reference/android/content/Context.html#getDir(java.lang.String, int)
     */
    public void ShowFileSystem(File dirFile,StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append("--");
        }
        if (dirFile.isDirectory()) {
            sb.append(String.format("%s\n",dirFile.getName()));
            level++;
            for (File one_file : dirFile.listFiles()
                    ) {
                ShowFileSystem(one_file, sb, level);
            }
        } else {
            sb.append(String.format("%s\n",dirFile.getName()));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
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

        TextView textView = (TextView) findViewById(R.id.debug_textView);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.debug_map);
        mapFragment.getMapAsync(this);

        File filesDir = getFilesDir();
        File app_root_file = filesDir.getParentFile();

        // test test num
        _test_num++;
        Log.i(TAG, "num: "+_test_num);

        // show the file system in the Log
        Log.i("RedAct_File sys", String.format("File Path: %s\nApp root path: %s",
                filesDir.getAbsolutePath(),app_root_file.getAbsolutePath()));
        StringBuilder fileSysSB = new StringBuilder();
        ShowFileSystem(app_root_file,fileSysSB,0);
        if (textView != null) {
            textView.setText(fileSysSB.toString());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        _mymap = googleMap;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    // todo set FK_ID
                    int d = data.getIntExtra("id",-1);
                    Log.i("Debug Act", "onActivityResult: "+d);
                }
                break;
            }
        }
    }
}

/**
3/13/2016
TODO: use try-catch to deal with the exception
*/