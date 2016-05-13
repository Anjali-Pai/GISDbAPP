package com.example.xuxin.databasedemo;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class CreateAProjectActivity extends AppCompatActivity {
    private String TAG = "Create ACT";
    private LinkedHashMap<String, HashMap<String, String>> receivedInfo;
    private String isEdit = "False";

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

        // todo how the read the correct value
        Intent receivedIntent = getIntent();
        MySerializableIntent serIntent = (MySerializableIntent) receivedIntent.getSerializableExtra(ReadATableActivity.EXTRA_MESSAGE_For_InsertDbTbInfo);
        if(serIntent!=null) {
            receivedInfo = serIntent.getData();
            isEdit = receivedIntent.getStringExtra(ReadATableActivity.EXTRA_MESSAGE_For_ModifyScheme);
        }

        final TableLayout createTableLayout = (TableLayout) findViewById(R.id.create_a_project_tableLayout);
        final EditText dbNameET = (EditText) findViewById(R.id.create_a_project_dbName);
        final EditText tableNameET = (EditText) findViewById(R.id.create_a_project_tableName);
        final Button addBt = (Button) findViewById(R.id.create_a_project_addBt);
        final Button subBt = (Button) findViewById(R.id.create_a_project_submitBt);

        // todo remove the type spinner, use string when create a table, in submission operation
        // show the column names: del, field name, is geog data?
        TableRow first_row = new TableRow(this);
        TextView delOpt_cell = new TextView(this);
        TextView fieldName_cell = new TextView(this);
        TextView isGeogData_cell = new TextView(this);
        delOpt_cell.setText(R.string.create_a_project_delARow);
        fieldName_cell.setText(R.string.create_a_project_fieldName);
        isGeogData_cell.setText(R.string.create_a_project_isGeogData);
        first_row.addView(delOpt_cell);
        first_row.addView(fieldName_cell);
        first_row.addView(isGeogData_cell);
        if (createTableLayout != null) {
            createTableLayout.addView(first_row);
        }

        // display info if it is edit operation
        // todo check the users' input, limit some input
        if (isEdit!=null && isEdit.toLowerCase().equals("true")){
            for (String key:receivedInfo.keySet()
                 ) {
                switch (key){
                    case "Database":
                        if (dbNameET != null) {
                            dbNameET.setText(receivedInfo.get(key).get("name"));
                        }
                        break;
                    case "Table":
                        if (tableNameET != null) {
                            tableNameET.setText(receivedInfo.get(key).get("name"));
                        }
                        break;
                    case "_id":
                        break;
                    case "_deviceInfo":
                        break;
                    default:
                        TableRow row = new TableRow(this);
                        Button delBt = new Button(this);
                        delBt.setText(R.string.create_a_project_delARow);
                        delBt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // delete this row
//                                Log.i("Delete Button", String.format("Parent:%s, grandparent:%s", v.getParent(), v.getParent().getParent()));
                                TableRow delRow = (TableRow) v.getParent();
                                if (delRow != null) {
                                    if (createTableLayout != null) {
                                        createTableLayout.removeView(delRow);
                                    }
                                }
                            }
                        });
                        row.addView(delBt);

                        EditText nameEditText = new EditText(this);
                        nameEditText.setText(key);
                        row.addView(nameEditText);
                        //  switch
                        ToggleButton isGeogDataTB = new ToggleButton(this);
                        isGeogDataTB.setTextOff("No");
                        isGeogDataTB.setTextOn("Yes");
                        if(receivedInfo.get(key).get("fk").equals("1")) {
                            isGeogDataTB.setChecked(!isGeogDataTB.isChecked());
                        }
                        else
                        {
                            isGeogDataTB.setChecked(isGeogDataTB.isChecked());
                        }
                        row.addView(isGeogDataTB);

                        // checkbox
                        CheckBox checkBox = new CheckBox(this);
                        checkBox.setText(R.string.create_a_project_keep_data);
                        row.addView(checkBox);

                        if(createTableLayout!=null){
                            createTableLayout.addView(row);
                        }
                        break;
                }
            }
        }


        // add row operation
        if (addBt != null) {
            addBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View myView = v;
                    final TableRow newTableRow = new TableRow(v.getContext());

                    Button delBt = new Button(v.getContext());
                    delBt.setText(R.string.create_a_project_delARow);
                    delBt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Log.i("Delete Button", String.format("Parent:%s, grandparent:%s", v.getParent(), v.getParent().getParent()));
                            TableRow delRow = (TableRow) v.getParent();
                            if (delRow != null) {
                                if (createTableLayout != null) {
                                createTableLayout.removeView(delRow);
                                }
                            }
                        }
                    });
                    newTableRow.addView(delBt);

                    EditText nameEditText = new EditText(v.getContext());
                    newTableRow.addView(nameEditText);
                    // toggle button to show it is the geo_data or not
                    // ref: http://developer.android.com/guide/topics/ui/controls/togglebutton.html
                    // switch
                    // http://developer.android.com/reference/android/widget/Switch.html
                    // http://stackoverflow.com/questions/23358822/how-to-custom-switch-button
                    ToggleButton isGeodataTb = new ToggleButton(v.getContext());
//                    isGeodataTb.setText(R.string.create_a_project_isGeogData_activate);
                    // trick to set text and redraw
                    // ref: http://stackoverflow.com/questions/3784292/how-can-i-get-working-dynamic-togglebutton-text-under-android/3792554#3792554
                    // here use this trick may cost a lot
                    isGeodataTb.setTextOff("No");
                    isGeodataTb.setTextOn("Yes");
                    isGeodataTb.setChecked(isGeodataTb.isChecked());
                    //isGeodataTb.setChecked(!isGeodataTb.isChecked());
//                    isGeodataTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                            if(isChecked){
//                                //  delete existed added can selected type cell
//                                Log.i("Toggle", "select true, children num: "+ newTableRow.getChildCount());
//                                if(newTableRow.getChildCount()>3){
//                                    Log.i("Toggle", "select true, need to delete select type cell");
//                                    for(int i = 3; i < newTableRow.getChildCount();i++){
//                                        newTableRow.removeViewAt(i);
//                                    }
//                                }
//                            }else {
//                                if(newTableRow.getChildCount()<4) // can use this way to do sth in the inner class
//                                {
//                                    Log.i("Toggle", "select false, children num: "+ newTableRow.getChildCount());
//                                    // and add only one type spinner
//                                    // then can select type
//                                    Spinner typeSpinner = new Spinner(myView.getContext());
//                                    // Create an ArrayAdapter using the string array and a default spinner layout
//                                    ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(myView.getContext(),
//                                            R.array.data_types, R.layout.support_simple_spinner_dropdown_item);
//                                    // Specify the layout to use when the list of choices appears
//                                    spAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                                    // apply the adapter to the spinner
//                                    typeSpinner.setAdapter(spAdapter);
//                                    // response to the user select
//                                    typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                        @Override
//                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                            // here to check the data type?
//                                            Log.i("Spinner", String.format("postion: %d,id: %s, Selected Item:%s",
//                                                    position, Long.toString(id), parent.getItemAtPosition(position).toString()));
//                                        }
//
//                                        @Override
//                                        public void onNothingSelected(AdapterView<?> parent) {
//                                            Log.i("Spinner", "Nothing selected ><");
//                                        }
//                                    });
//                                    // add spinner into the view
//                                    newTableRow.addView(typeSpinner);
//                                }
//                            }
//                        }
//                    });
                    newTableRow.addView(isGeodataTb);

                    if (createTableLayout != null) {
                        createTableLayout.addView(newTableRow);
                    }
                }
            });
        }

        // submission operation
        if (subBt != null) {
            // todo after submitting, show the result success or not, exit the create activity,

            subBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String databaseName = dbNameET != null ? dbNameET.getText().toString() : null;
                    String tableName = tableNameET != null ? tableNameET.getText().toString() : null;
                    Log.i(TAG,String.format("Submission: Database name: %s, Table name: %s",databaseName,tableName));
                    List<Map<String,String>> inputValList = new ArrayList<Map<String, String>>();

                    // the first row is the column name row, ignore it
                    for(int i = 1; i< (createTableLayout != null ? createTableLayout.getChildCount() : 0); i++){
                        //Log.i("Child in table",createTableLayout.getChildAt(i).toString());
                        Map<String,String> dataRowMap = new HashMap<String, String>();
                        try{
                            TableRow dataRow = (TableRow) createTableLayout.getChildAt(i);
                            dataRowMap.put("name",((EditText) dataRow.getChildAt(1)).getText().toString());
                            ToggleButton tb = (ToggleButton) dataRow.getChildAt(2);
                            if(tb.isChecked()){
                                dataRowMap.put("geog","1");
                                dataRowMap.put("type","null");
                            }
                            else{
                                dataRowMap.put("geog","0");
                                dataRowMap.put("type","text");
                            }

                            if(dataRow.getChildCount()>3) {
                                if (((CheckBox) dataRow.getChildAt(3)).isChecked()) {
                                    dataRowMap.put("keepData", "1");
                                }
                                else
                                {
                                    dataRowMap.put("keepData", "0");
                                }
                            }
                            else
                            {
                                dataRowMap.put("keepData", "0");
                            }
                        }catch (Exception ex){
                            Log.e(TAG,ex.getMessage());
                        }
                        inputValList.add(dataRowMap);
                    }

                    // ...
                    StringBuilder createSB = new StringBuilder();
                    createSB.append(String.format("Create %sTable ",tableName));

                    String createSQL = createSB.toString();




                    String mainTableValComponent = ""; // not null
                    String mainTableFKCom = "";
                    // minor tables
                    List<String> minorTableList = new ArrayList<String>();
                    for (Map<String,String> m:inputValList
                         ) {
                        //show the input in the log
                        Log.i(TAG, String.format("Check input name:%s, is geog:%s, type:%s, keep data:%s",
                                m.get("name"),m.get("geog"),m.get("type"),m.get("keepData")));
                        //sad thing about string to boolean
                        // ref: http://stackoverflow.com/questions/1538755/how-to-convert-string-object-to-boolean-object
                        if(m.get("geog").toLowerCase().equals("1")) {
                            //
                            mainTableValComponent = mainTableValComponent.concat(
                                    String.format("%s INTEGER, ", m.get("name")));
                            //  FOREIGN KEY(trackartist) REFERENCES artist(artistid)
                            mainTableFKCom = mainTableFKCom.concat(
                                    String.format("FOREIGN KEY(%s) REFERENCES %s(_id), ",
                                            m.get("name"),String.format("%sTable",m.get("name"))));
                            minorTableList.add(String.format(
                                    "CREATE TABLE %sTable " +
                                            "( _id INTEGER PRIMARY KEY, _name TEXT NOT NULL," +
                                            " _latitude REAL NOT NULL, _longitude NOT NULL );",
                                    m.get("name")
                            ));
                        }
                        else{
                            mainTableValComponent = mainTableValComponent.concat(String.format("%s %s, ",
                                    m.get("name"),m.get("type")));
                        }
                    }
                    // remove the ",space"
                    if(mainTableFKCom.length()>0) {
                        mainTableFKCom = mainTableFKCom.substring(0, mainTableFKCom.length() - 2);
                    }
                    mainTableValComponent = mainTableValComponent.substring(0, mainTableValComponent.length() - 2);
                    // create tables

                    //  ...
                    // foreign key in sql ref: https://www.sqlite.org/foreignkeys.html
                    // PRAGMA foreign_keys = ON;

                    // must-have table
                    // sql can use xxtable ? as table name?
                    String createDeviceInfoSQL = String.format("CREATE TABLE DeviceInfoTable " +
                            "( _id INTEGER PRIMARY KEY, _name TEXT NOT NULL," +
                            " _latitude REAL NOT NULL, _longitude REAL NOT NULL );");
                    String createMainTableSQL;
                    if(mainTableFKCom.length()>0) {
                        createMainTableSQL = String.format("CREATE TABLE %s (" +
                                        "_id INTEGER PRIMARY KEY, _deviceInfo INTEGER, %s, " +
                                        "FOREIGN KEY(_deviceInfo) REFERENCES DeviceInfoTable(_id), %s);",
                                tableName, mainTableValComponent, mainTableFKCom);
                    }
                    else
                    {
                        createMainTableSQL = String.format("CREATE TABLE %s (" +
                                        "_id INTEGER PRIMARY KEY, _deviceInfo INTEGER, %s, " +
                                        "FOREIGN KEY(_deviceInfo) REFERENCES DeviceInfoTable(_id));",
                                tableName,mainTableValComponent);
                    }

                    // check in log
                    Log.i("SQL", String.format("main:%s\nminor:%s",createMainTableSQL,createDeviceInfoSQL));
                    for (String str:minorTableList
                         ) {
                        Log.i("FK",str);
                    }
//                    // action
//                    SQLiteDatabase newDb = openOrCreateDatabase(databaseName+".db", Context.MODE_PRIVATE,null);
//                    newDb.execSQL(createDeviceInfoSQL);
//                    for (String str:minorTableList
//                         ) {
//                        newDb.execSQL(str);
//                    }
//                    newDb.execSQL(createMainTableSQL);
//                    // todo need to add drop the same name table
//                    // todo check it is edit or not...
//                    // newdb.execSQL(String.format("DROP TABLE IF EXISTS %s;", tableName));
//                    newDb.close();
//
//                    // go to read projects
//                    Intent readIntent = new Intent(v.getContext(),ReadProjectsActivity.class);
//                    startActivity(readIntent);
                }
            });

        }
    }
}
/***
 *  question: add table row dynamically
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
 *  drop list for the type - by spinner - need to improve
 * set the default _id as the default primary key, and deal with many primary keys, add";" every sql command
 * ref http://www.sqlite.org/lang_createtable.html
 * ref: http://stackoverflow.com/questions/734689/sqlite-primary-key-on-multiple-columns
 * ref: http://www.runoob.com/sqlite/sqlite-create-table.html
 * two spinner every line, int/varchar |primary key/not null
 * ensure automatically _id + 1
 * fix problem: default scheme, not additional field added, created table failed
 *  add delete row
 *  todo can be edited
 *  change type to the string  and can be null only, do not need to select the type, for now
 *  todo in the submission operation: alter table and keep the data...
 *
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
