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
    private List<HashMap<String,String>> _oldFieldInfo = new ArrayList<>();
    private String isEdit = "False";
    private String _dbPath ;

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

        LinkedHashMap<String, HashMap<String, String>> dbTbInfo = new LinkedHashMap<>();

        _oldFieldInfo = new ArrayList<>(); // initial ...
        // todo how the read the correct value
        Intent receivedIntent = getIntent();
        MySerializableIntent serIntent = (MySerializableIntent) receivedIntent.getSerializableExtra(ReadATableActivity.EXTRA_MESSAGE_For_InsertDbTbInfo);
        if(serIntent!=null) {
            dbTbInfo = serIntent.getData();
            isEdit = receivedIntent.getStringExtra(ReadATableActivity.EXTRA_MESSAGE_For_ModifyScheme);
            _dbPath = dbTbInfo.get("Database").get("path");
        }

        final TableLayout createTableLayout = (TableLayout) findViewById(R.id.create_a_project_tableLayout);
        final EditText dbNameET = (EditText) findViewById(R.id.create_a_project_dbName);
        final EditText tableNameET = (EditText) findViewById(R.id.create_a_project_tableName);
        final Button addBt = (Button) findViewById(R.id.create_a_project_addBt);
        final Button subBt = (Button) findViewById(R.id.create_a_project_submitBt);

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

        // store the changes of the existed fields, change to, keep data?, need to store in order
        // display info if it is edit operation
        // todo check the users' input, limit some input
        if (isEdit!=null && isEdit.toLowerCase().equals("true")){
            for (final String key: dbTbInfo.keySet()
                 ) {
                switch (key){
                    case "Database":
                        if (dbNameET != null) {
                            dbNameET.setText(dbTbInfo.get(key).get("name"));
                        }
                        break;
                    case "Table":
                        if (tableNameET != null) {
                            tableNameET.setText(dbTbInfo.get(key).get("name"));
                        }
                        break;
                    case "_id":
                        break;
                    case "_deviceInfo":
                        break;
                    default:
                        // add to the old field info
                        final HashMap<String,String> oldField = dbTbInfo.get(key);
                        oldField.put("name",key);
                        _oldFieldInfo.add(oldField);

                        //
                        final TableRow row = new TableRow(this);
                        Button delBt = new Button(this);
                        delBt.setText(R.string.create_a_project_delARow);
                        delBt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // add delete info
                                int index = _oldFieldInfo.indexOf(oldField);
                                oldField.put("delete","1");
                                _oldFieldInfo.set(index,oldField);
                                // delete this row
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
                        final ToggleButton isGeogDataTB = new ToggleButton(this);
                        isGeogDataTB.setTextOff("No");
                        isGeogDataTB.setTextOn("Yes");
                        final boolean isGeog = dbTbInfo.get(key).get("fk").equals("1");

                        if(dbTbInfo.get(key).get("fk").equals("1")) {
                            isGeogDataTB.setChecked(!isGeogDataTB.isChecked());
                        }
                        else
                        {
                            isGeogDataTB.setChecked(isGeogDataTB.isChecked());
                        }
                        // if geog switch button existed field was clicked, means is_geog_data status changes, do not need to
                        // show the keep data check box, because can't hold the data

                        isGeogDataTB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // check whether the geog data status is in accordance with toggle button status\
                                // 1: check, 0: un check
                                // if conflicts, do not add checkbox, if it is right, add check box, but need to make there is only one checkbox
                                if(isGeogDataTB.isChecked() == isGeog){
                                    // true = true OR false = false
                                    // add only one
                                    // checkbox
                                    if(((TableRow) v.getParent()).getChildCount()<4) {
                                        CheckBox checkBox = new CheckBox(v.getContext());
                                        checkBox.setText(R.string.create_a_project_keep_data);
                                        row.addView(checkBox);
                                    }
                                }
                                else
                                // delete the checkbox
                                {
                                    TableRow editRow = (TableRow) v.getParent();
                                    if(editRow.getChildCount()>3){
                                        for(int i=3; i<editRow.getChildCount();i++){
                                            editRow.removeViewAt(i);
                                        }
                                    }
                                }
                            }
                        });
                        row.addView(isGeogDataTB);

                        // default add the checkbox view
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
                    // procedure: collect data, assemble sql, exec sql

                    String databaseName = dbNameET != null ? dbNameET.getText().toString() : null;
                    String tableName = tableNameET != null ? tableNameET.getText().toString() : null;
                    List<Map<String,String>> inputDataList = new ArrayList<Map<String, String>>();

                    // the first row is the column name row, ignore it
                    for(int i = 1; i< (createTableLayout != null ? createTableLayout.getChildCount() : 0); i++){
                        // todo use old field info and new info to create new field info
                        //Log.i("Child in table",createTableLayout.getChildAt(i).toString());
                        Map<String,String> inputMap = new HashMap<String, String>();
                        try{
                            TableRow inputTableRow = (TableRow) createTableLayout.getChildAt(i);
                            // existed field or not
                            String fieldName = ((EditText) inputTableRow.getChildAt(1)).getText().toString();
                            if(i-1<_oldFieldInfo.size()){
                                inputMap = _oldFieldInfo.get(i-1);
                                inputMap.put("changeTo",fieldName);
                                // keep data or not
                                if(inputTableRow.getChildCount()>3) {
                                    if (((CheckBox) inputTableRow.getChildAt(3)).isChecked()) {
                                        inputMap.put("keepData", "1");
                                    }
                                    else
                                    {
                                        inputMap.put("keepData", "0");
                                    }
                                }
                                else {
                                    // change the geog data, so that may do not show the checkbox
//                                    Log.e(TAG, "existed field error!");
                                    inputMap.put("keepData", "0");
                                }
                            }
                            else {
                                inputMap.put("name", ((EditText) inputTableRow.getChildAt(1)).getText().toString());
                            }
                            ToggleButton tb = (ToggleButton) inputTableRow.getChildAt(2);
                            if (tb.isChecked()) {
                                inputMap.put("geog", "1");
                                inputMap.put("type", "INTEGER");
                            } else {
                                inputMap.put("geog", "0");
                                inputMap.put("type", "text");
                            }
                        }
                        catch (Exception ex){
                            Log.e(TAG,ex.getMessage());
                        }
                        inputDataList.add(inputMap);
                    }

                    // show the input data in the log to check
                    myShowInputDataInLog(inputDataList, databaseName, tableName);

                    // create sql commands: change old names, create tables, alter tables
                    /**** todo step
                     * do not consider _id and _deviceinfo
                     *                        edit?
                     *         no:                        yes
                     *                                rename old table name
                     *     create table               create table
                     *                                alter table
                     *
                     *  NOTE:
                     *  1. delete table, if keep data is not selected, two cases: changed name, or delete it
                     *   device table is not necessary to change, keep it and its name untouched
                     *  2. after deleting tables, the existed table should be changed their name, for next step
                     *  3. fk in the main table, if it is not deleted, just rename or not, we can just rename the fk table, that means
                     *  if field is a fk, rename it, do not need to rename it as temp table and then insert data and last delete temp table
                     *
                     */

                    if(isEdit.equals("False")){
                        // create table sql
                        Log.i(TAG, "Create SQL:\n" + myCreateSQL(inputDataList, tableName));
                        // action
                        SQLiteDatabase newDb = openOrCreateDatabase(databaseName+".db", Context.MODE_PRIVATE,null);
                        newDb.setForeignKeyConstraintsEnabled(true);
                        for (String sql:myCreateSQL(inputDataList, tableName)
                             ) {
                            // todo exception
                            newDb.execSQL(sql);
                        }
                        // close db
                        newDb.close();

                    }else {
                        // open the database
                        SQLiteDatabase newDb = SQLiteDatabase.openDatabase(_dbPath,null, Context.MODE_PRIVATE);
                        // todo !!!
//                        newDb.setForeignKeyConstraintsEnabled(true);

                        // modify the existed tables
                        Log.i(TAG, " Modify SQL:\n" + myModifyTables(inputDataList,tableName));
                        for (String sql:myModifyTables(inputDataList,tableName)
                             ) {
                            newDb.execSQL(sql);
                        }

                        // create table sql
                        ArrayList<String> createSQLs = myCreateSQL(inputDataList, tableName);
                        Log.i(TAG, "Create SQL:\n" + createSQLs.subList(1,createSQLs.size()));
                        for (String sql:createSQLs.subList(1,createSQLs.size())
                                ) {
                            newDb.execSQL(sql);
                        }

                        // insert old data
                        Log.i(TAG, "Insert SQL:\n"+ myInsertOldData(inputDataList,tableName));
                        for (String sql: myInsertOldData(inputDataList,tableName)
                                ) {
                            newDb.execSQL(sql);
                        }

                        // drop old tables
                        Log.i(TAG, "Drop SQL:\n"+myDropOldTables(inputDataList,tableName));
                        for (String sql: myDropOldTables(inputDataList,tableName)
                                ) {
                            newDb.execSQL(sql);
                        }
                        // todo figure out what is it, and does it work?
                        String createIndexSQL = String.format("CREATE INDEX _id ON %s (_id)",tableName);
                        newDb.execSQL(createIndexSQL);
                        // close db
                        newDb.close();
                    }

                    //go to read projects
                    Intent readIntent = new Intent(v.getContext(),ReadProjectsActivity.class);
                    startActivity(readIntent);
                }
            });

        }
    }

    void myShowInputDataInLog(List<Map<String,String>> inputDataList,String databaseName, String tableName){
        Log.i(TAG, databaseName + ", "+ tableName);
        StringBuilder logText = new StringBuilder();
        for (Map<String,String> m:inputDataList
             ) {
            for (String key:m.keySet()
                 ) {
                String val = m.get(key)==null? "nothing":m.get(key);
                logText.append(String.format("%s: %s| ",key,val));
            }
            logText.append("\n");
        }
        Log.i(TAG, logText.toString());
    }

    // create fk tables ?
    // create table users (
    // _id int, _device int, name text,
    // FOREIGN KEY(_deviceInfo) REFERENCES DeviceInfoTable(_id)
    // FOREIGN KEY(xxx) REFERENCES xxxTable(_id)
    // );
    // foreign key in sql ref: https://www.sqlite.org/foreignkeys.html
    // PRAGMA foreign_keys = ON;

    ArrayList<String> myCreateSQL(List<Map<String,String>> inputDataList, String tableName){

        ArrayList<String> createSQLs = new ArrayList<>(); // geog device table first, and then fk tables, last one is main create table
        StringBuilder createMainSQLSB = new StringBuilder();
        StringBuilder fkSB = new StringBuilder();

        // add geog table in the list first
        createSQLs.add("CREATE TABLE DeviceInfoTable " +
                "( _id INTEGER PRIMARY KEY, _name TEXT NOT NULL," +
                " _latitude REAL NOT NULL, _longitude REAL NOT NULL );");

        createMainSQLSB.append(String.format("CREATE TABLE %s (" +
                "_id INTEGER PRIMARY KEY, _deviceInfo INTEGER ", tableName));

        for (Map<String,String> m:inputDataList
             ) {
            if (m.get("geog").equals("1")) {
                // geog data
                // fk in main table
                String fkName = m.get("changeTo") == null? m.get("name"):m.get("changeTo");
                fkSB.append(String.format(", FOREIGN KEY(%s) REFERENCES %s(_id) ",
                            fkName,String.format("%sTable",fkName)));
                    // create fk tables for new geog field, or existed one with not keep data
                // but need to exclude the geog->non-geog, which does not need to create, means geog = 1 then create
                    // use pk to identify the new or existed one
                    if(m.get("pk")==null ||
                            ( m.get("pk")!=null &&
                                    m.get("keepData") !=null &&
                                    m.get("keepData").equals("0")

                            )
                            ) {
                        String fieldName = m.get("changeTo") == null? m.get("name"):m.get("changeTo");
                        createSQLs.add(String.format("CREATE TABLE %sTable " +
                                        "( _id INTEGER PRIMARY KEY, _name TEXT NOT NULL," +
                                        " _latitude REAL NOT NULL, _longitude NOT NULL );",
                                fieldName));
                    }
                    // add field in main table
                    String fieldName = m.get("changeTo") == null? m.get("name"):m.get("changeTo");
                    createMainSQLSB.append(String.format(", %s INTEGER", fieldName));
                } else
                // not geog data
                {
                    // not deleted ones
                    if(m.get("delete")== null || m.get("delete").equals("0")) {
                        String fieldName = m.get("changeTo") == null ? m.get("name") : m.get("changeTo");
                        createMainSQLSB.append(String.format(", %s TEXT", fieldName));
                    }
                }

        }

        // add fk field in the main sql
        createMainSQLSB.append(", FOREIGN KEY(_deviceInfo) REFERENCES DeviceInfoTable(_id)");
        if(!fkSB.toString().isEmpty()){
            createMainSQLSB.append(String.format(" %s",fkSB.toString()));
        }
        // the end of the create
        createMainSQLSB.append(" );");
        createSQLs.add(createMainSQLSB.toString());
        return createSQLs;
    }

    // for existed fk tables, rename its name or delete it
    // ALTER TABLE {tableName} RENAME TO TempOldTable;
    // for fk field,
    // keep data and not re-name: just rename fk tables here, do not need to create temp table and then delete it
    // not keep data: drop
    ArrayList<String> myModifyTables(List<Map<String,String>> inputDataList, String tableName){
        ArrayList<String> modifySQLs = new ArrayList<>();
        for (Map<String,String> m:inputDataList
             ) {
            // just for fk tables, so first should make sure whether it's a existed fk
            if(m.get("pk")!=null && m.get("fk")!=null && m.get("fk").equals("1")) { // existed fk
                if ((m.get("delete") != null && m.get("delete").equals("1")) ||
                        (m.get("keepData") != null && m.get("keepData").equals("0"))) {
                    // delete the table
                    modifySQLs.add(String.format("DROP TABLE %sTable;", m.get("name")));
                    continue;
                }
                if ((m.get("changeTo") != null && !m.get("changeTo").equals(m.get("name"))) &&
                        (m.get("keepData") != null && m.get("keepData").equals("1"))
                        ) {
                    // rename to the request table
                    modifySQLs.add(String.format("ALTER TABLE %sTable RENAME TO %sTable;",
                            m.get("name"), m.get("changeTo")));
                }
            }
        }
        // main table rename
        modifySQLs.add(String.format("ALTER TABLE %s RENAME TO %sOldTable;",
                tableName,tableName));

        return modifySQLs;
    }

    // INSERT INTO {tableName} (name, qty, rate) SELECT name, qty, rate FROM TempOldTable;
    ArrayList<String> myInsertOldData(List<Map<String,String>> inputDataList, String tableName){
        ArrayList<String> insertSQLs = new ArrayList<>();
        // for the main table inserting
        StringBuilder fromSB = new StringBuilder();
        StringBuilder toSB = new StringBuilder();
        fromSB.append("_id, _deviceInfo");
        toSB.append("_id, _deviceInfo");

        for (Map<String,String> m:inputDataList
                ) {
            // fk tables,maybe we can just rename, have done in other functions ..

            // main tables
            if(      m.get("pk") != null &&  // to show it is existed field
                    (m.get("delete")== null  || m.get("delete").equals("0")) &&
                    (m.get("keepData") != null && m.get("keepData").equals("1"))) {
                fromSB.append(String.format(", %s",m.get("name")));
                toSB.append(String.format(", %s",m.get("changeTo")));
            }
        }

        //
        insertSQLs.add(String.format("INSERT INTO %s ( %s ) " +
                "SELECT %s FROM %sOldTable;",
                tableName,toSB.toString(),fromSB.toString(),tableName));
        return insertSQLs;
    }

    // drop
    ArrayList<String> myDropOldTables(List<Map<String,String>> inputDataList, String tableName){
        ArrayList<String> dropSQLs = new ArrayList<>();

//        // delete the fk table the field name is removed from the list
//
//        for (Map<String,String> m:inputDataList
//                ) {
//            if( m.get("delete")!=null && m.get("delete").equals("1")){
//                dropSQLs.add(String.format("DROP TABLE %sTable;",m.get("name")));
//            }
//        }
        // main table
        dropSQLs.add(String.format("DROP TABLE %sOldTable;",tableName));
        return dropSQLs;
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
 *  todo data structure to save the field changes, new field, existed field be changed or be removed
 *  todo in the submission operation: alter table and keep the data...
 *  todo every key in the map, when to use it, first check its value is existed or not
 *  todo not null in the table scheme, when insert new data
 *
 *  todo add new geog data type in the table scheme, no data show in the table
 *  reason:
 *  when read table, use join (fk table), the default is the inner join, while the new fk table is empty
 *  so use outer join
 *  ref: http://www.runoob.com/sqlite/sqlite-joins.html
 *  ref: http://www.w3schools.com/sql/sql_join_left.asp
 *   question:
 *  1 code above onCreate
 *  such as one list
 *  does every time activity recreates but the list keep the previous values, not the re-initialized?
 *  read activity life
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
/**** todo: Performance
 * 1. here many operation are start from the first one to the last one,  ~N
 * 2.
 */
