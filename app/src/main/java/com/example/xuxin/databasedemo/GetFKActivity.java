package com.example.xuxin.databasedemo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class GetFKActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String TAG = "Get FK ACT";
    GoogleMap _mMap;
    String _dbPath;
    String _tableName;
    LatLng _latlang;
    // show the data
    // touch map to add
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_fk);
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
        ArrayList<String> receivedInfo = receivedIntent.getStringArrayListExtra(InsertDataActivity.EXTRA_MESSAGE_For_FKTableInfo);
        TableLayout tableTableLayout = (TableLayout) findViewById(R.id.get_fk_tableTableLayout);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.get_fk_map);
        mapFragment.getMapAsync(this);

        // db path, table name
        String dbPath = receivedInfo.get(0);
        String tableName = receivedInfo.get(1);
        _dbPath = dbPath;
        _tableName = tableName;

        //myShowAlertDialog(this); // it works

        TableRow insTableRow = new TableRow(this);
        final EditText insET = new EditText(this);
        insTableRow.addView(insET);
        final Button insBt = new Button(this);
        insBt.setText(R.string.ClickToCreate);
        insBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_latlang!=null){
//                    ContentValues cv = new ContentValues();
                    String fkName = insET.getText().toString();
                    myInsertData(fkName,_latlang);
                    _latlang = null;
                }
                else
                {
                    Log.i("GET FK", "no lat lng ");
                }
            }
        });
        insTableRow.addView(insBt);
        // get current location
        Button getCurBT = new Button(this);
        getCurBT.setText(R.string.get_fk_get_current_location);
        getCurBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String bestProvider = locationManager.getBestProvider(criteria, false);
                if (ActivityCompat.checkSelfPermission(v.getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(v.getContext(),
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

                try {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                   _latlang = new LatLng(lat,lon);
                    _mMap.addMarker(new MarkerOptions().position(_latlang));
                }
                catch (NullPointerException e){
                    Log.e(TAG,e.getMessage());
                }
            }
        });
        insTableRow.addView(getCurBT);

        if (tableTableLayout != null) {
            tableTableLayout.addView(insTableRow);
        }

        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
        db.setForeignKeyConstraintsEnabled(true);

        // show the data
        String allDataSQL = "SELECT * FROM "+ tableName;
        Cursor allDataCur = db.rawQuery(allDataSQL,null);
        // show column names
        TableRow colTableRow = new TableRow(this);
        for (int i = 0; i < allDataCur.getColumnCount(); i++) {
            TextView colName = new TextView(this);
            String colNameStr = allDataCur.getColumnName(i);
            // ref: http://stackoverflow.com/questions/1528988/create-tablelayout-programatically
            colName.setText(colNameStr);
            colTableRow.addView(colName);
        }
        if (tableTableLayout != null) {
            tableTableLayout.addView(colTableRow);
        }
        // red line
        TextView redLine= new TextView(this);
        redLine.setHeight(2);
        redLine.setBackgroundColor(Color.RED);
        if (tableTableLayout != null) {
            tableTableLayout.addView(redLine);
        }
        if(allDataCur.moveToFirst()){
            do{
                TableRow dataTableRow = new TableRow(this);
                final long fkID = allDataCur.getInt(0);
                for (int j = 0; j < allDataCur.getColumnCount(); j++) {
                    TextView dataTextView = new TextView(this);
                    dataTextView.setText(allDataCur.getString(j));
                    dataTableRow.addView(dataTextView);
                }
                // add select button
                Button selBt = new Button(this);
                selBt.setText(R.string.accept);
                selBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent resultData = new Intent();
                        resultData.putExtra("id",fkID);
                        Log.i("GET FK", String.format("Send FK: %d",fkID));
                        setResult(Activity.RESULT_OK, resultData);
                        finish();
                    }
                });
                dataTableRow.addView(selBt);
                if (tableTableLayout != null) {
                    tableTableLayout.addView(dataTableRow);
                }
            }while (allDataCur.moveToNext());
        }
        allDataCur.close();

        // close db
        db.close();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        _mMap = googleMap;

        // todo set title ~ to improve UI
        // touch to get lat and lng
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("POS",String.format("Lat: %f, Lng: %f",latLng.latitude,latLng.longitude));
                _latlang = latLng;
                googleMap.clear();
                // add marker
                 googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Marker"));

            }
        } );
    }

    public void myInsertData(String name, LatLng latLng){

        // insert data to the database
        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(_dbPath,null, Context.MODE_PRIVATE);
        ContentValues cv = new ContentValues();
        cv.put("_name",name);
        cv.put("_latitude",latLng.latitude);
        cv.put("_longitude",latLng.longitude);
        db.insert(_tableName,null,cv);
        // close
        db.close();
        //
        recreate();
    }

}
/****
 * todo: accept click does not work, may be the source activity does not receive the correct id
 */
