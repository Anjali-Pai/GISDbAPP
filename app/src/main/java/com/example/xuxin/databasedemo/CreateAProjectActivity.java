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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;


public class CreateAProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_a_project);
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

        final TableLayout createTableLayout = (TableLayout) findViewById(R.id.create_a_project_tableLayout);
        final EditText dbNameET = (EditText) findViewById(R.id.create_a_project_dbName);
        final EditText tableNameET = (EditText) findViewById(R.id.create_a_project_tableName);

        final Button addBt = (Button) findViewById(R.id.create_a_project_addBt);
        final Button subBt = (Button) findViewById(R.id.create_a_project_submitBt);

        TableRow column_row = new TableRow(this);
        TextView col_fieldname = new TextView(this);
        TextView col_fieldval = new TextView(this);
        col_fieldname.setText("Field name");
        col_fieldval.setText("Type");
        column_row.addView(col_fieldname);
        column_row.addView(col_fieldval);
        createTableLayout.addView(column_row);

        addBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow tb = new TableRow(v.getContext());
                EditText nameeditText = new EditText(v.getContext());
                tb.addView(nameeditText);
                Spinner typesp = new Spinner(v.getContext());
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> spadapter = ArrayAdapter.createFromResource(v.getContext(),
                        R.array.data_types, R.layout.support_simple_spinner_dropdown_item);
                // Specify the layout to use when the list of choices appears
                spadapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                // apply the adapter to the spinner
                typesp.setAdapter(spadapter);
                // response to the user select
                typesp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // todo here to check the data type?
                        Log.i("Spinner",String.format("postion: %d,id: %s, Selected Item:%s",
                                position, Long.toString(id),parent.getItemAtPosition(position).toString()));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.i("Spinner","Nothing selected ><");
                    }
                });
                // add spinner into the view
                tb.addView(typesp);
                createTableLayout.addView(tb);
            }
        });

        subBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> fieldnamelist  = new ArrayList<String>();
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
                        fieldnamelist.add(((EditText) datarow.getChildAt(0)).getText().toString());
                        // ref: http://stackoverflow.com/questions/1947933/how-to-get-spinner-value
                        Spinner typesp = (Spinner) datarow.getChildAt(1);
                        fieldtypelist.add(typesp.getSelectedItem().toString());
                        //Log.i("Spinner",String.format("Value:%s",typesp.getSelectedItem().toString()));
                    }catch (Exception ex){
                        Log.e("child in the table",ex.getMessage());
                    }
                }

                //
                for(int i=0;i<fieldnamelist.size();i++){
                    //check the two lists
                    Log.i("Name & Type",String.format("name: %s, type: %s",fieldnamelist.get(i),fieldtypelist.get(i)));
                    tablecomp = tablecomp.concat(String.format("%s %s, ",fieldnamelist.get(i),fieldtypelist.get(i)));
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
