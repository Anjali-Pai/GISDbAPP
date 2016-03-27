package com.example.xuxin.databasedemo;

import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class EditDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);
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

        Intent intent = getIntent();
        String db_path = intent.getStringExtra(DatabaseCRUDHelper.Db_Path);
        String table_name = intent.getStringExtra(DatabaseCRUDHelper.Table_Name);
        String primary_key = intent.getStringExtra(DatabaseCRUDHelper.Primary_Key);
        ArrayList<String> columnname_list = intent.getStringArrayListExtra(DatabaseCRUDHelper.ColumnName_List);
        ArrayList<String> fieldval_list = intent.getStringArrayListExtra(DatabaseCRUDHelper.FieldVal_List);

        Button submitBt = (Button) findViewById(R.id.edit_data_submit);
        TableLayout table = (TableLayout) findViewById(R.id.edit_data_edittable);
        // show the data
        // todo ensure columnname_list and fieldval_list have the same total number
        for(int i=0; i< columnname_list.size();i++){
            TableRow row = new TableRow(this);
            TextView lableView = new TextView(this);
            EditText editdataView = new EditText(this);
            lableView.setText(columnname_list.get(i));
            editdataView.setText(fieldval_list.get(i));

            row.addView(lableView);
            row.addView(editdataView);
            table.addView(row);
        }

        final String databasepath = db_path;
        final String tablename = table_name;
        final String primarykey = primary_key;

        // submit button click listener: edit the data and return to the previous activity
        submitBt.setOnClickListener(new View.OnClickListener() {
            // todo check whether it is editable... or updated able
            TableLayout tableLayout = (TableLayout)findViewById(R.id.edit_data_edittable);
            SQLiteDatabase aimed_db = SQLiteDatabase.openDatabase(databasepath,null, Context.MODE_PRIVATE);

            @Override
            public void onClick(View v) {
                // button is view? in this case, the view is the Button
                Log.i("Button","Clicked in the EditDataAct, view name: "+ v.toString());
                ContentValues cv = new ContentValues();
                for(int i=0;i<tableLayout.getChildCount();i++){
                    try {
                        TableRow tr = (TableRow) tableLayout.getChildAt(i);
                        String data_name = ((TextView) tr.getChildAt(0)).getText().toString();
                        String data_val = ((EditText) tr.getChildAt(1)).getText().toString();
                        Log.i("DataPair",String.format("%s:%s",data_name,data_val));
                        cv.put(data_name,data_val);
                    }catch (Exception ex){
                        Log.e("Exception@SubmitClick",ex.getMessage());
                    }
                }

                try {
                    aimed_db.update(tablename,cv,"_id=?",new String[]{primarykey});
                    aimed_db.close();
                    if(aimed_db.isOpen()){aimed_db.close();}
                    finish();
                }catch (Exception ex){
                    Log.e("Update data ex",ex.getMessage());
                }

            }
        });

    }

}