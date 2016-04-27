package com.example.xuxin.databasedemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
//        _mymap = googleMap;

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

    public void myShowAlertDialog(Context context){
        // pop up a dialog alert dialog
        // todo we can pop up as many dialogs as we need to collect enough data by the field

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Title");
        // Set up the input
        final EditText input = new EditText(context);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                _m_text = input.getText().toString();
//                Log.i("Alert Dialog",_m_text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
