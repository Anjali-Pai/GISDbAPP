package com.example.xuxin.databasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
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
    }

    //------- Activities -------
    public void CreateNewProjects(View view){
        Intent intent = new Intent(this,CreateAProjectActivity.class);
        startActivity(intent);
    }

    public void ReadExistedProjects(View view){
        Intent intent = new Intent(this,ReadProjectsActivity.class);
        startActivity(intent);
    }

    public void DebugTest(View view){
        Intent intent = new Intent(this,DebugActivity.class);
        startActivity(intent);
    }

}
/**
 * to-do-list
 * 03/27/2016
 *  compact the code, create method helper etc - not use helper
 *  scroll view applied in many activities - done
 * sql leak... need to solve it
 * = it is associated with refresh data, and each sql operation should
 * return a result to show the whether it is successful or not
 * in this case, failed operation may still keeps the database opened
 * 03/29/2016
 *  make create table like the sql command ??
 * data visualization
 * 04/06/2017
 * basic functions: collect data, one table database, show the data in the table
 * advanced: geographical database...
 * add map api key ...
 * ref: https://developers.google.com/maps/documentation/android-api/signup#console_name_api
 * how to deal with touch and get locations
 * helper table: geolat, geolong,name,id
 * show data in map by selecting different type which is connected to the helper table
 * e.g: main table scheme=(int _id,string name,*geog city)
 *      geog city scheme=(int _id,long lat, long long,string name)
 *      relationship: main:geo = 1:1
 *      use: in main table: field name: city, type: geog data => link to a table, (PK,FK), maintable.city.name = geogdata.name, which is
 *      set by algs and lat/long is set from users, input or by touching map to get values
 * ready to rebuild the projects and UI
 * Internal data leak within a DataBuffer object...
 * todo: edit table features after the created
 * todo: output data
 * pk crud may affect the fk
 * todo: comments uniform and to improve code
 * todo: cursor, get string (index), need to make the index is what we want? in case: null, other value,
 * todo: back operation ...
 * current location,
 * todo: read whole code, create whole structure, and write technical doc for next semester develop
 * todo: rename the activity name, logically
 *
 */

/***
 * TODO: UI
 *  0. manifest file: title name, icon...
 *  1. activity layout, setting?
 */
/** Safety *
 * Map api key is embedded in the manifest... be careful?
 */
/** TODO Road map
 * data on map -> UI improve
 *  get location by pointing on the map
 *  Design studying
 *  Output table/form ??  format?
 *  Add pic...
 *  arrange log... too many log here
 *  ...
 */

/**TODO QUESTIONS*
 * 1. http://stackoverflow.com/questions/32878888/android-idea-misc-xmls-languagelevel-tag-keeps-changing-jdks
 */
