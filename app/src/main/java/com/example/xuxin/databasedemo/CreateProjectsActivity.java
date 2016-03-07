package com.example.xuxin.databasedemo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CreateProjectsActivity extends AppCompatActivity {

    Intent intent = getIntent();
    // vars
    EditText editUserName,editUserSSN;
    SQLiteDatabase testDb;

    // Test click for insert data into the database
    public void TestInsert(View view){
//        TextView myTextView = (TextView)findViewById(R.id.outputshow);
//        EditText myEditView = (EditText) findViewById(R.id.inputval);
//        String inPutStr = myEditView.getText().toString();
//        myTextView.setText(inPutStr);

        // insert value
        EditText ssnEditText = (EditText)findViewById(R.id.userssn);
        String userSSN = ssnEditText.getText().toString();
        EditText nameEditText = (EditText)findViewById(R.id.username);
        String userName = nameEditText.getText().toString();
        String insertsql = "INSERT INTO users VALUES('"+userSSN+"','"+userSSN+"','"+userName+"');";
        testDb.execSQL(insertsql);
        showDatabase();
    }

    public void TestModify(View view){
        EditText ssnEditText = (EditText)findViewById(R.id.userssn);
        String userSSN = ssnEditText.getText().toString();
        EditText nameEditText = (EditText)findViewById(R.id.username);
        String userName = nameEditText.getText().toString();
        String modifysql = "UPDATE users SET ssn='"+userSSN+"',name='"+userName+"' WHERE ssn='"+userSSN+"';";
        testDb.execSQL(modifysql);
        showDatabase();
    }
    public void TestDelete(View view){
        EditText ssnEditText = (EditText)findViewById(R.id.userssn);
        String userSSN = ssnEditText.getText().toString();
        EditText nameEditText = (EditText)findViewById(R.id.username);
        String userName = nameEditText.getText().toString();
        String delsql = "DELETE FROM users WHERE ssn='"+userSSN+"'";
        testDb.execSQL(delsql);
        showDatabase();
    }
    public void TestCloseDB(View view){
        //testDb.close();
    }
    public void TestDeleteDB(View view){
        //testDb.close();
        //wth wrong
        //context.deleteDatabase(testDb);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        // open or create a database
        testDb = openOrCreateDatabase("Testusers", Context.MODE_PRIVATE,null);
        testDb.execSQL("DROP TABLE IF EXISTS users");
        //create users table
        testDb.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY not null, ssn INTEGER not null, name VARCHAR not null);");

        // insert default data
        String insrtsql =  "INSERT INTO users VALUES(1,11,'xuixn');";
        String insrtsql2 =  "INSERT INTO users VALUES(2,22,'alexxu');";
        testDb.execSQL(insrtsql);
        testDb.execSQL(insrtsql2);
        //
        String sql = "SELECT * FROM users;";
        Cursor rec = testDb.rawQuery(sql, null);
        rec.moveToLast();
        int databaselen=rec.getCount();//total num
        String title="";
        int colCount = rec.getColumnCount();
        for(int i=0;i<colCount;i++){
            title=title+rec.getColumnName(i)+" ";
        }

        TextView myTextView = (TextView)findViewById(R.id.debuginfo);
        myTextView.setText(databaselen+","+colCount+":"+title);

        // get the grid view
        GridView tableGridView = (GridView) findViewById(R.id.tablegridview);

        //Test for grid view
        // build dynamic data and set values
        ArrayList<HashMap<String,Object>> tableRowItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<databaselen;i++){
            rec.moveToPosition(i);
            HashMap<String, Object> rowitem = new HashMap<String, Object>();
            rowitem.put("row_id", rec.getString(0) + " ");
            rowitem.put("row_ssn", rec.getString(1) + " ");
            rowitem.put("row_name", rec.getString(2) + " ");
            tableRowItem.add(rowitem);
        }
        rec.close();
        // adapter
        // ref: http://developer.android.com/reference/android/widget/SimpleAdapter.html#SimpleAdapter(android.content.Context, java.util.List<? extends java.util.Map<java.lang.String, ?>>, int, java.lang.String[], int[])
        SimpleAdapter tableAdapater = new SimpleAdapter(this,
                tableRowItem,
                R.layout.table_row,
                //from, string
                new String[]{"row_id","row_ssn","row_name"},
                //to R.id
                new int[]{R.id.row_id,R.id.row_ssn,R.id.row_name}
        );
        // add adapater to the gridview
        tableGridView.setAdapter(tableAdapater);
        // add listener
        tableGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            // ref http://developer.android.com/reference/android/widget/AdapterView.OnItemClickListener.html
            public void onItemClick (AdapterView<?> parent, View view, int position, long id){
                updateData(position,id);
            }
        });
    }

    void showDatabase(){
        String sql = "SELECT * FROM users;";
        Cursor rec = testDb.rawQuery(sql, null);
        rec.moveToLast();
        int databaselen=rec.getCount();//total num
        String title="";
        int colCount = rec.getColumnCount();
        for(int i=0;i<colCount;i++){
            title=title+rec.getColumnName(i)+" ";
        }

        TextView myTextView = (TextView)findViewById(R.id.debuginfo);
        myTextView.setText(databaselen+","+colCount+":"+title);

        // get the grid view
        GridView tableGridView = (GridView) findViewById(R.id.tablegridview);

        //Test for grid view
        // build dynamic data and set values
        ArrayList<HashMap<String,Object>> tableRowItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<databaselen;i++){
            rec.moveToPosition(i);
            HashMap<String, Object> rowitem = new HashMap<String, Object>();
            rowitem.put("row_id", rec.getString(0) + " ");
            rowitem.put("row_ssn", rec.getString(1) + " ");
            rowitem.put("row_name", rec.getString(2) + " ");
            tableRowItem.add(rowitem);
        }
        rec.close();
        // adapter
        // ref: http://developer.android.com/reference/android/widget/SimpleAdapter.html#SimpleAdapter(android.content.Context, java.util.List<? extends java.util.Map<java.lang.String, ?>>, int, java.lang.String[], int[])
        SimpleAdapter tableAdapater = new SimpleAdapter(this,
                tableRowItem,
                R.layout.table_row,
                //from, string
                new String[]{"row_id","row_ssn","row_name"},
                //to R.id
                new int[]{R.id.row_id,R.id.row_ssn,R.id.row_name}
        );
        // add adapater to the gridview
        tableGridView.setAdapter(tableAdapater);
    }

    void updateData(int pos,long row_Id){
        // in this case, pos = row_id
        TextView myTextView = (TextView)findViewById(R.id.debuginfo);
        myTextView.setText("click@: "+pos+", "+row_Id);
        // search data in the database
        pos=pos+1;
        String searchsql = "SELECT * FROM users WHERE id='"+pos+"';";
        Cursor rec = testDb.rawQuery(searchsql,null);
        if(rec.moveToFirst()){
            EditText ssnEditText = (EditText)findViewById(R.id.userssn);
            ssnEditText.setText(rec.getString(1));
            EditText nameEditText = (EditText)findViewById(R.id.username);
            nameEditText.setText(rec.getString(2));
        }
        else {
            myTextView.setText("failed to find ");
        }
        rec.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
