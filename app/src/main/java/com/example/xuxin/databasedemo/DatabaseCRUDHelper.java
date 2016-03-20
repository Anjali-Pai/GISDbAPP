package com.example.xuxin.databasedemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by XuXin on 2016/3/19.
 */
public class DatabaseCRUDHelper {
    public void readSelectedDatabase(Context context,TextView textView,TableLayout table,SQLiteDatabase db){

        ArrayList<String> tables_name = new ArrayList<String>();
        // ref: http://stackoverflow.com/questions/1528988/create-tablelayout-programatically
        TableRow.LayoutParams tlparams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT); // for view inside
        TableRow table_row = new TableRow(context);
        table_row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));

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
        textView.append(String.format("Column count: %d, Data count: %d", colCount, rec.getCount()));
        for(int i=0;i<colCount;i++){
            TextView columnname_textview = new TextView(context);
            columnname_textview.setLayoutParams(tlparams);
            columnname_textview.setText(rec.getColumnName(i));
            table_row.addView(columnname_textview);
        }
        table.addView(table_row);
//        TextView lineview = new TextView(context);
//        lineview.setLayoutParams(tlparams);
//        lineview.setHeight(2);
//        lineview.setBackgroundColor(255);
//        table.addView(lineview);
        //read data
        rec.moveToFirst();
        for(int i=0;i<rec.getCount();i++){
            TableRow data_row = new TableRow(context);
            data_row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
            for(int j=0;j<rec.getColumnCount();j++){
                TextView data_textview = new TextView(context);
                data_textview.setLayoutParams(tlparams);
                data_textview.setText(rec.getString(j));
                table_row.addView(data_textview);
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
    public void deleteSelectedDatabase(Context context, String dbname){
//        db.close();
//        String db_path = db.getPath();
        context.deleteDatabase(dbname);
    }
}
/**
 * log
 * 03/19/2016
 * show the data successfully
 * layout problems
 */
