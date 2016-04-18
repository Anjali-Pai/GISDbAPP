package com.example.xuxin.databasedemo;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;


public class CreateAProjectActivity extends AppCompatActivity {
    private boolean _canAddTypeCell = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_a_project);
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

        final TableLayout createTableLayout = (TableLayout) findViewById(R.id.create_a_project_tableLayout);
        final EditText dbNameET = (EditText) findViewById(R.id.create_a_project_dbName);
        final EditText tableNameET = (EditText) findViewById(R.id.create_a_project_tableName);
        final Button addBt = (Button) findViewById(R.id.create_a_project_addBt);
        final Button delBt = (Button) findViewById(R.id.create_a_project_delBt);
        final Button subBt = (Button) findViewById(R.id.create_a_project_submitBt);

        TableRow first_row = new TableRow(this);
        TextView fieldName_cell = new TextView(this);
        TextView isGeogData_cell = new TextView(this);
        TextView fieldType_cell = new TextView(this);
        fieldName_cell.setText(R.string.create_a_project_fieldName);
        isGeogData_cell.setText(R.string.create_a_project_isGeogData);
        fieldType_cell.setText(R.string.create_a_project_fieldType);
        first_row.addView(fieldName_cell);
        first_row.addView(isGeogData_cell);
        first_row.addView(fieldType_cell);
        if (createTableLayout != null) {
            createTableLayout.addView(first_row);
        }

        if (addBt != null) {
            addBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View myView = v;
                    final TableRow newTableRow = new TableRow(v.getContext());
                    EditText nameEditText = new EditText(v.getContext());
                    newTableRow.addView(nameEditText);
                    // toggle button to show it is the geo_data or not
                    // ref: http://developer.android.com/guide/topics/ui/controls/togglebutton.html
                    // switch
                    // http://developer.android.com/reference/android/widget/Switch.html
                    // http://stackoverflow.com/questions/23358822/how-to-custom-switch-button
                    ToggleButton isGeodataTb = new ToggleButton(v.getContext());
                    isGeodataTb.setTextOff("No");
                    isGeodataTb.setTextOn("Yes");
                    isGeodataTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                Log.i("Toggle", "select true, children num: "+ newTableRow.getChildCount());
                            }else {
                                if(newTableRow.getChildCount()<3) // can use this way to do sth in the inner class
                                {
                                    Log.i("Toggle", "select false, children num: "+ newTableRow.getChildCount());
                                    // todo first show OFF...
                                    // and add only one type spinner
                                    // then can select type
                                    Spinner typeSpinner = new Spinner(myView.getContext());
                                    // Create an ArrayAdapter using the string array and a default spinner layout
                                    ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(myView.getContext(),
                                            R.array.data_types, R.layout.support_simple_spinner_dropdown_item);
                                    // Specify the layout to use when the list of choices appears
                                    spAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                    // apply the adapter to the spinner
                                    typeSpinner.setAdapter(spAdapter);
                                    // response to the user select
                                    typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            // here to check the data type?
                                            Log.i("Spinner", String.format("postion: %d,id: %s, Selected Item:%s",
                                                    position, Long.toString(id), parent.getItemAtPosition(position).toString()));
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {
                                            Log.i("Spinner", "Nothing selected ><");
                                        }
                                    });
                                    // add spinner into the view
                                    newTableRow.addView(typeSpinner);
                                }
                            }
                        }
                    });
                    newTableRow.addView(isGeodataTb);

                    if (createTableLayout != null) {
                        createTableLayout.addView(newTableRow);
                    }
                }
            });
        }

        if(delBt != null){
            Log.i("Delete button","existed!");
        }

        if (subBt != null) {
            subBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> fieldnameList  = new ArrayList<String>();
                    ArrayList<String> fieldtypelist = new ArrayList<String>();

                    String databasename = dbNameET.getText().toString();
                    String tablename = tableNameET.getText().toString();
                    // default value
                    String tablecomp="_id INTEGER, GeoLat REAL NOT NULL, GeoLong REAL NOT NULL,";
                    Log.i("Db&Table",String.format("Database name: %s, Table name: %s",databasename,tablename));
                    // the first row is the column name row, ignore it
                    for(int i=1;i<createTableLayout.getChildCount();i++){
                        //Log.i("Child in table",createTableLayout.getChildAt(i).toString());
                        try{
                            TableRow datarow = (TableRow) createTableLayout.getChildAt(i);
                            fieldnameList.add(((EditText) datarow.getChildAt(0)).getText().toString());
                            // ref: http://stackoverflow.com/questions/1947933/how-to-get-spinner-value
                            Spinner typesp = (Spinner) datarow.getChildAt(1);
                            fieldtypelist.add(typesp.getSelectedItem().toString());
                            //Log.i("Spinner",String.format("Value:%s",typesp.getSelectedItem().toString()));
                        }catch (Exception ex){
                            Log.e("child in the table",ex.getMessage());
                        }
                    }

                    //
                    for(int i=0;i<fieldnameList.size();i++){
                        //check the two lists
                        Log.i("Name & Type",String.format("name: %s, type: %s",fieldnameList.get(i),fieldtypelist.get(i)));
                        tablecomp = tablecomp.concat(String.format("%s %s, ",fieldnameList.get(i),fieldtypelist.get(i)));
                    }
                    tablecomp = tablecomp.substring(0,tablecomp.length()-1); // remove the ",space" => for now remove the "space"
                    Log.i("SQL-CreateTable", tablecomp);
                    // create new database with one table
                    // todo check the file which has the same name, or use time stamp/guid
                    SQLiteDatabase newdb = openOrCreateDatabase(databasename+".db", Context.MODE_PRIVATE,null);
                    newdb.execSQL(String.format("DROP TABLE IF EXISTS %s;", tablename));
                    newdb.execSQL(String.format("CREATE TABLE %s (%s PRIMARY KEY( _id));", tablename, tablecomp));
                    newdb.close();
                }
            });
        }
    }
}
/***
 * todo question: add table row dynamically
 * - done, but the final TableLayout how to understand it?
 * - Final class is complete in nature and can not be sub-classed or inherited. Several classes in Java are final e.g. String, Integer and other wrapper classes.
 *      Read more: http://javarevisited.blogspot.com/2011/12/final-variable-method-class-java.html#ixzz448dcvi9A
 * It means that if your final variable is a reference type (i.e. not a primitive like int),
 * then it's only the reference that cannot be changed.
 * It cannot be made to refer to a different object,
 * but the fields of the object it refers to can still be changed, if the class allows it.
 * http://stackoverflow.com/questions/1249917/final-variable-manipulation-in-java
 * A final variable can only be initialized once, in my case, it is ok to do so
 * and we can declare final variable first, and then set the value. it is ok, in my test
 * todo drop list for the type - by spinner - need to improve
 * set the default _id as the default primary key, and deal with many primary keys, add";" every sql command
 * ref http://www.sqlite.org/lang_createtable.html
 * ref: http://stackoverflow.com/questions/734689/sqlite-primary-key-on-multiple-columns
 * ref: http://www.runoob.com/sqlite/sqlite-create-table.html
 * two spinner every line, int/varchar |primary key/not null
 * ensure automatically _id + 1
 * fix problem: default scheme, not additional field added, created table failed
 * todo add delete row
 */

/***
 * SQLite data types
 * http://www.runoob.com/sqlite/sqlite-data-types.html
 * Null
 * Integer: Int, Integer,...
 * Real
 * Text: Character(20), Varchar(255),...
 * Blob
 */
