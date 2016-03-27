package com.example.xuxin.databasedemo;
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


public class CreateProjectsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_projects);
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

        final TableLayout createTableLayout = (TableLayout) findViewById(R.id.create_project_table);
        EditText dbnameET = (EditText) findViewById(R.id.create_project_dbname);
        EditText tablnameET = (EditText) findViewById(R.id.create_project_tablename);

        Button addBt = (Button) findViewById(R.id.create_project_addButton);
        Button subBt = (Button) findViewById(R.id.create_project_submitButton);
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
                EditText valeditText = new EditText(v.getContext());
                tb.addView(valeditText);
                createTableLayout.addView(tb);
            }
        });

        subBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // the first row is the column name row, ignore it
                for(int i=1;i<createTableLayout.getChildCount();i++){
                    Log.i("Child in table",createTableLayout.getChildAt(i).toString());
                    try{
                        TableRow datarow = (TableRow) createTableLayout.getChildAt(i);
                        String namestr=((EditText)datarow.getChildAt(0)).getText().toString();
                        String typestr = ((EditText)datarow.getChildAt(1)).getText().toString();
                        Log.i("Info in create table",String.format("Name:%s,Type:%s",namestr,typestr));
                    }catch (Exception ex){
                        Log.e("child in the table",ex.getMessage());
                    }
                }
            }
        });
    }
}
/***
 * todo add table row dynamically
 * - done, but the final TableLayout how to understand it?
 * - Final class is complete in nature and can not be sub-classed or inherited. Several classes in Java are final e.g. String, Integer and other wrapper classes.
 *      Read more: http://javarevisited.blogspot.com/2011/12/final-variable-method-class-java.html#ixzz448dcvi9A
 * It means that if your final variable is a reference type (i.e. not a primitive like int),
 * then it's only the reference that cannot be changed.
 * It cannot be made to refer to a different object,
 * but the fields of the object it refers to can still be changed, if the class allows it.
 * http://stackoverflow.com/questions/1249917/final-variable-manipulation-in-java
 */
