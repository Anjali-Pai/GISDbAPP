package com.example.xuxin.databasedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by XuXin on 2016/3/19.
 */
public class DatabaseCRUDHelper {
    /***
     * Display all the data in the database
     * @param context  context
     * @param textView debug information textView
     * @param table destination table where display the data
     * @param db source database
     */
    public void readSelectedDatabase(Context context,TextView textView,TableLayout table,SQLiteDatabase db){
        ArrayList<String> tables_name = new ArrayList<String>();
        TableRow table_row = new TableRow(context);
        //table_row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));

        Cursor tablesname_cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        // show existed tables
        if (tablesname_cursor.moveToFirst()) {
            //table_name = c.getString(0);
            while ( !tablesname_cursor.isAfterLast() ) {
                tables_name.add(tablesname_cursor.getString(0));
                tablesname_cursor.moveToNext();
            }
        }
        tablesname_cursor.close();
        textView.append("existed tables: "+ Arrays.toString(tables_name.toArray()) +"\n");

        // get the required table name
        String table_name = tables_name.get(tables_name.size() - 1);
        // query all the data
        String sql = "SELECT * FROM "+table_name+";";
        textView.append("query sql: " + sql + "\n");
        Cursor rec = db.rawQuery(sql, null);
        // show column names
        int colCount = rec.getColumnCount();
        textView.append(String.format("Column count: %d, Data count: %d\n", colCount, rec.getCount()));
        for(int i=0;i<colCount;i++){
            TextView columnname_textview = new TextView(context);
            // ref: http://stackoverflow.com/questions/1528988/create-tablelayout-programatically
            //columnname_textview.setLayoutParams(new TableRow.LayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)));
            columnname_textview.setText(rec.getColumnName(i));
            table_row.addView(columnname_textview);
        }
        table.addView(table_row);
        // red line
        TextView lineview = new TextView(context);
        lineview.setHeight(2);
        lineview.setBackgroundColor(Color.RED);
        table.addView(lineview);
        //read data
        rec.moveToFirst();
        for(int i=0;i<rec.getCount();i++){
            TableRow data_row = new TableRow(context);
            //data_row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
            for(int j=0;j<rec.getColumnCount();j++){
                TextView data_textview = new TextView(context);
                //data_textview.setLayoutParams(new TableRow.LayoutParams( new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)));
                data_textview.setText(rec.getString(j));
                data_row.addView(data_textview);
            }
            table.addView(data_row);
            if(!rec.isAfterLast()) {
                rec.moveToNext();
            }
        }
        //close rec
        rec.close();
        //close database
        db.close();
        }
    // create the input table for create table
    // return a list of editview id
    public void createCreateDataTable(Context context,TextView textView,TableLayout table,SQLiteDatabase db){
        final String database_path = db.getPath();
        //ArrayList<Integer> editviewids = new ArrayList<Integer>();
        ArrayList<EditText > ets = new ArrayList<EditText>();
        // get the required table name
        TableRow table_row = new TableRow(context);
        Cursor tablesname_cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        String newway_get_table_name="";
        tablesname_cursor.moveToLast();
        newway_get_table_name = tablesname_cursor.getString(0);
        tablesname_cursor.close();
        // query all the data
        String sql = "SELECT * FROM "+newway_get_table_name+";";
        textView.append(String.format("Information from create table:\n%s\n",sql));
        Cursor rec = db.rawQuery(sql, null);
        // show column names
        int colCount = rec.getColumnCount();
        textView.append(String.format("Column count: %d, Data count: %d\n", colCount, rec.getCount()));
        for(int i=0;i<colCount;i++){
            TextView columnname_textview = new TextView(context);
            columnname_textview.setText(rec.getColumnName(i));
            EditText inputText = new EditText(context);
            //int editviewid = View.generateViewId();
            //inputText.setId(editviewid);
            //editviewids.add(editviewid);
            ets.add(inputText);
            table_row.addView(columnname_textview);
            table_row.addView(inputText);
        }
        final ArrayList<EditText> editTexts = ets;
        Button bt = new Button(context);
        bt.setText("Insert");
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> datastrs = new ArrayList<String>();
                for (EditText et : editTexts
                        ) {
                    datastrs.add(et.getText().toString());
                }
                SQLiteDatabase database = SQLiteDatabase.openDatabase(database_path,null, Context.MODE_PRIVATE);
                // todo show the result information to the user

                try {
                    createDataInSelectedDatabase(datastrs,database);
                    //result_info.setText("Insert data Successfully!");
                }catch (Exception ex){
                    //result_info.setText(ex.getMessage());
                    Log.e("Insert data",ex.getMessage());
                }

            }
        });

        table_row.addView(bt);
        table.addView(table_row);
        //close rec
        rec.close();
        //close db
        db.close();
        //return editviewids;
            }

    // todo insert data: AsyncTask and refresh database display
    // the store order in the datastr should be accordance withe columnames
    public void createDataInSelectedDatabase(ArrayList<String> datastr, SQLiteDatabase db) {
        // get table name
        Cursor tablesname_cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        String table_name = "";
        tablesname_cursor.moveToLast();
        table_name = tablesname_cursor.getString(0);
        tablesname_cursor.close();

        //get columns
        // query all the data
        String sql = "SELECT * FROM " + table_name + ";";
        Cursor rec = db.rawQuery(sql, null);
        ArrayList<String> columnnames = new ArrayList<String>();
        for (int i = 0; i < rec.getColumnCount(); i++) {
            columnnames.add(rec.getColumnName(i));
        }
        rec.close();
        // todo need to check the number of the datastr and columnames, they should be same
        // insert data by content values
        ContentValues cv = new ContentValues();
        for (int i = 0; i < columnnames.size(); i++) {
            cv.put(columnnames.get(i), datastr.get(i));
        }
        db.insert(table_name, null, cv);
        db.close();
    }

    public void deleteSelectedDatabase(Context context, String dbname) {
                // todo close db first, then delete it
                context.deleteDatabase(dbname);
            }
        }

/**
 * todo it seems that every method needs to know the table in the database and the column names
 *
 */