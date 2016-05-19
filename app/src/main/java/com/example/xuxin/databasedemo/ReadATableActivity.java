package com.example.xuxin.databasedemo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ReadATableActivity extends AppCompatActivity {

    private String TAG = "Read Table ACT";
    public  final static String EXTRA_MESSAGE_For_ModifyScheme = "com.example.xuxin.databasedemo.ModifyScheme";
    public final static String EXTRA_MESSAGE_For_InsertDbTbInfo = "com.example.xuxin.databasedemo.InsertDbTbInfo";
//    public final static String EXTRA_MESSAGE_For_EditDbTbInfo = "com.example.xuxin.databasedemo.EditDbTbInfo";
    public final static String EXTRA_MESSAGE_For_SelectedID = "com.example.xuxin.databasedemo.SelectedID";
    LinkedHashMap<String,HashMap<String,String>> _insDbTbInfo = new LinkedHashMap<>();
//    int _fkID=-1;

    private StringBuilder _outPutSB = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_a_table);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent insertDataIntent = new Intent(view.getContext(),InsertDataActivity.class);
                    MySerializableIntent testData = new MySerializableIntent();
                    testData.setData(_insDbTbInfo);
                    insertDataIntent.putExtra(EXTRA_MESSAGE_For_InsertDbTbInfo,testData);
                    insertDataIntent.putExtra(EXTRA_MESSAGE_For_SelectedID,"-1");
                    startActivityForResult(insertDataIntent,1);
                }
            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button dataVisualBT = (Button) findViewById(R.id.read_a_table_data_visualization_BT);
        Button outPutBT = (Button) findViewById(R.id.read_a_table_output_BT);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.read_a_table_tableTableLayout);
        Intent intent = getIntent();
        // database name + Table name
        ArrayList<String> DbTbList = intent.getStringArrayListExtra(ReadAProjectActivity.EXTRA_MESSAGE_For_DbTbName);
        String received_dbName = DbTbList.get(0);
        final String db_path = getFilesDir().getParent()+"/databases/"+received_dbName;
        final String received_tableName = DbTbList.get(1);
        HashMap<String,String> dbHashMap = new HashMap<>();
        dbHashMap.put("name",received_dbName);
        dbHashMap.put("path",db_path);
        _insDbTbInfo.put("Database",dbHashMap);
        HashMap<String,String> tbHashMap = new HashMap<>();
        tbHashMap.put("name",received_tableName);
        _insDbTbInfo.put("Table",tbHashMap);

        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(db_path,null, Context.MODE_PRIVATE);
        db.setForeignKeyConstraintsEnabled(true);
        // read table info: column, PK, FK
        // re-design the data structure to store the table info and FK info
        // store in the hash map: key-value: name: type = > xx: common type, _id: PK
        // ref: http://stackoverflow.com/questions/14721484/get-name-and-type-from-pragma-table-info
        // ref: http://stackoverflow.com/questions/11753871/getting-the-type-of-a-column-in-sqlite
        // ref: https://www.sqlite.org/pragma.html
        // cid | name | type | notnull | dflt_value | pk
        final LinkedHashMap<String,HashMap<String,String>> TableInfo = new LinkedHashMap<>();
        final LinkedHashMap<String,HashMap<String,String>> FKInfo = new LinkedHashMap<>();
//        final ArrayList<String> colNameList = new ArrayList<>();
        // to get table info
        Cursor tableInfoCur = db.rawQuery("PRAGMA table_info(" + received_tableName + ")", null);
        if (tableInfoCur.moveToFirst()) {
            // show the table info in the log
            //Log.i("Table info", "cid | name | type | notnull | dflt_value | pk");
            do {
                String tableInfoStr = "";
                for (int i = 0; i<tableInfoCur.getColumnCount();i++) {
                    tableInfoStr = tableInfoStr.concat(tableInfoCur.getString(i) == null ? " | ": tableInfoCur.getString(i)+" | ");
                }
                //Log.i("Table info", tableInfoStr);
                HashMap<String,String> fieldInfo = new HashMap<>();
                fieldInfo.put("type",tableInfoCur.getString(2));
                fieldInfo.put("pk",tableInfoCur.getString(5));
                TableInfo.put(tableInfoCur.getString(1),fieldInfo);
            } while (tableInfoCur.moveToNext());
        }
        tableInfoCur.close();

        Cursor tableFKCur = db.rawQuery("PRAGMA foreign_key_list(" + received_tableName + ")", null);
        //Log.i("FK info", "id | seq | table | from | to | on_update | on_delete | match");
        if(tableFKCur.moveToFirst()) {
            do {
                String fkInfoStr = "";
                for (int i = 0; i < tableFKCur.getColumnCount(); i++) {
                    fkInfoStr = fkInfoStr.concat( tableFKCur.getString(i) == null ? " | " : tableFKCur.getString(i))+" | ";
                }
                //Log.i("FK info", fkInfoStr);
                HashMap<String,String> FKFieldInfo = new HashMap<>();
                FKFieldInfo.put("table",tableFKCur.getString(2));
                FKFieldInfo.put("to",tableFKCur.getString(4));
                FKInfo.put(tableFKCur.getString(3),FKFieldInfo);
            }while (tableFKCur.moveToNext());
        }
        tableFKCur.close();
        myProcessData(TableInfo,FKInfo);

        // ... todo need to improve ...
        // join table then show the data
        // get the no pk,fk field name,
        ArrayList<String> normalFieldNames = new ArrayList<>();
        ArrayList<String>  colNames = new ArrayList<>();
        for (String key:_insDbTbInfo.keySet()
             ) {
            if(!key.toLowerCase().equals("database"))
                if(!key.toLowerCase().equals("table")){
                    if(_insDbTbInfo.get(key).get("fk").equals("0")){
                        normalFieldNames.add(key);
                    }
                }
        }
        StringBuilder selectSQLSB = new StringBuilder();
        selectSQLSB.append("SELECT ");
        for (String str:normalFieldNames
             ) {
            selectSQLSB.append(String.format("%s.%s, ",received_tableName,str));
            colNames.add(String.format("%s",str));
        }
        StringBuilder joinOnSB = new StringBuilder();
        for (String str: FKInfo.keySet()
             ) {
            String fkTableName = FKInfo.get(str).get("table");
            joinOnSB.append(String.format("LEFT OUTER JOIN %s on %s.%s = %s._id ",
                    fkTableName,received_tableName,str,fkTableName));
            selectSQLSB.append(String.format("%s._name, ",fkTableName));
            colNames.add(String.format("%s.name",str));
        }
        String selectSQL = selectSQLSB.toString().substring(0,selectSQLSB.length()-2);
        String fromSQL = "FROM " + received_tableName;
        String joinOnSQL = joinOnSB.toString();

        String querySQL = String.format("%s %s %s",selectSQL,fromSQL,joinOnSQL);
        Log.i(TAG, "SQL:"+ querySQL);

        Cursor rec = db.rawQuery(querySQL, null);

        // show column names
        TableRow colTableRow = new TableRow(this);
        for (int i = 0; i < rec.getColumnCount(); i++) {
            TextView colName = new TextView(this);
            String colNameStr = colNames.get(i); // rec.getColumnName(i);
            // ref: http://stackoverflow.com/questions/1528988/create-tablelayout-programatically
            colName.setText(colNameStr);
            colTableRow.addView(colName);

            _outPutSB.append(String.format("%s, ",colNameStr));
        }
        if (tableLayout != null) {
            tableLayout.addView(colTableRow);
        }
        // red line
        TextView redLine= new TextView(this);
        redLine.setHeight(2);
        redLine.setBackgroundColor(Color.RED);
        if (tableLayout != null) {
            tableLayout.addView(redLine);
        }

        final int idIndex = colNames.indexOf("_id");
        // show data
        _outPutSB.append("\n");
        if(rec.moveToFirst()){
            do{
                TableRow dataTableRow = new TableRow(this);
                final String id = rec.getString(idIndex);

                for (int j = 0; j < rec.getColumnCount(); j++) {
                    TextView dataTextView = new TextView(this);
                    String fieldName = rec.getString(j);
                    dataTextView.setText(fieldName);
                    dataTableRow.addView(dataTextView);
                    _outPutSB.append(String.format("%s, ",fieldName));
                }
                _outPutSB.append("\n");

                //  add button delete and edit button
                Button editBt = new Button(this);
                editBt.setText(R.string.menu_edit);
                editBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent editIntent = new Intent(v.getContext(),InsertDataActivity.class);
                        // send _insDbTbInfo and id, which is to be edited
                        MySerializableIntent testData = new MySerializableIntent();
                        testData.setData(_insDbTbInfo);
                        editIntent.putExtra(EXTRA_MESSAGE_For_InsertDbTbInfo,testData);
                        editIntent.putExtra(EXTRA_MESSAGE_For_SelectedID,id);
                        startActivityForResult(editIntent,2);
                    }
                });
                dataTableRow.addView(editBt);
                Button delBt = new Button(this);
                delBt.setText(R.string.menu_delete);
                delBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDeleteData(db_path,received_tableName,id);
                    }
                });
                dataTableRow.addView(delBt);
                if (tableLayout != null) {
                    tableLayout.addView(dataTableRow);
                }
            }
            while (rec.moveToNext());
        }

        // close rec
        rec.close();
        // close database
        if(db.isOpen()){db.close();}

        //-----------------------------Modify Table Scheme----------------------------------------//
        // send modify flag and table info
        Button modifySchemeBT = (Button) findViewById(R.id.read_a_table_editScheme_BT);
        if(modifySchemeBT!=null){
            modifySchemeBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //open the create a project activity
                    Intent modifyIntent = new Intent(v.getContext(),CreateAProjectActivity.class);
                    modifyIntent.putExtra(EXTRA_MESSAGE_For_ModifyScheme,"True");
                    MySerializableIntent testData = new MySerializableIntent();
                    testData.setData(_insDbTbInfo);
                    modifyIntent.putExtra(EXTRA_MESSAGE_For_InsertDbTbInfo,testData);
                    startActivity(modifyIntent);
                }
            });
        }

        //------------------------------------------data visualization----------------------------//
        if(dataVisualBT!=null){
            dataVisualBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dataVisualIntent = new Intent(v.getContext(),DataVisualActivity.class);
                    MySerializableIntent testData = new MySerializableIntent();
                    testData.setData(_insDbTbInfo);
                    dataVisualIntent.putExtra(EXTRA_MESSAGE_For_InsertDbTbInfo,testData);
                    startActivity(dataVisualIntent);
                }
            });
        }
        // -------------------------------------------data output --------------------------------//
        if(outPutBT!=null){
            // todo save file
            // todo may use dot command to output? ref: http://stackoverflow.com/questions/6076984/how-do-i-save-the-result-of-a-query-as-a-csv-file
            outPutBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(my_isExternalStorageWritable()){
                        String root;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
                            File myDir = new File(root + "/GIS");
                            myDir.mkdirs();
                            String fname = received_tableName+ ".csv";
                            File file = new File(myDir, fname);
                            if (file.exists())
                                file.delete();
                            try {
                                FileOutputStream stream = new FileOutputStream(file);
                                try {

                                    stream.write(_outPutSB.toString().getBytes());
                                } finally {
                                    stream.close();
                                }
                                Toast.makeText(v.getContext(),"Saved in "+file.getPath(), Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Log.i(TAG, "Need kitkat ");
                        }
                    }
                    else
                    {
                        Log.i(TAG, "write permission");
                    }
                }
            });
        }
    }

    // in fact, it is not necessary to keep the fk info in saved order ( by using linked hash map)
    void myProcessData(LinkedHashMap<String,HashMap<String,String>> TableInfo,
                       LinkedHashMap<String,HashMap<String,String>> FKInfo){
        for (String item:TableInfo.keySet()
             ) {
//            Log.i(TAG, item);
            HashMap<String,String> itemHasMap = new HashMap<>();
            HashMap<String,String> itemInfo = TableInfo.get(item);
            itemHasMap.put("type",itemInfo.get("type"));
            itemHasMap.put("pk",itemInfo.get("pk"));
            if(FKInfo.containsKey(item)){
                itemHasMap.put("fk","1");
                itemHasMap.put("fkTable",FKInfo.get(item).get("table"));
                itemHasMap.put("fkID",FKInfo.get(item).get("to"));
            }
            else
            {
                itemHasMap.put("fk","0");
                itemHasMap.put("fkTable","null");
                itemHasMap.put("fkID","null");
            }
            _insDbTbInfo.put(item,itemHasMap);
        }
    }

    void myDeleteData(String dbPath, String tableName, String ID){
        // open database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath,null, Context.MODE_PRIVATE);
        //delete data
        int res = db.delete(tableName,"_id = ?",new String[]{ID});
        // close database
        db.close();
        Log.i(TAG, "myDeleteData: Result  "+ res);
        recreate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            // 1 comes from insert data
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    long insert_id = data.getLongExtra("id",-1);
                    Log.i(TAG, "Received id: "+ insert_id);
                    recreate();
                }
                break;
            }
            case(2):{
                // 2 comes from edit data
                if (resultCode == Activity.RESULT_OK) {
                    long insert_id = data.getLongExtra("id",-1);
                    Log.i(TAG, "Received id: "+ insert_id);
                    recreate();
                }
                break;
            }
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // Respond to the action bar's Up/Home button
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /* Checks if external storage is available for read and write */
    public boolean my_isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
/**
 *  : show the data in a table, insert data, edit data and delete data
 *  : data visualization
 *  todo: read field name in order, col names use two lists to get its required data, too complicated, waste time and source
 *
 * */