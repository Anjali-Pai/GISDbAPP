package com.example.xuxin.databasedemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Show existed databases in whole app root path
 * Click database to show the data, all the data
 * click del button to delete the database 
 * */
public class ReadProjectsActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE_For_DbName = "com.example.xuxin.databasedemo.DbName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_projects);
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

        TableLayout dbTableLayout = (TableLayout) findViewById(R.id.read_projects_dbTableLayout);

        File filesDir = getFilesDir();
        File app_root_file = filesDir.getParentFile();
        File[] files = app_root_file.listFiles();
        ArrayList<File> db_files = new ArrayList<File>();
        // get all the dababase file
        if(files.length>0){
            for (File one_file: files
                 ) {
                if(one_file.isDirectory() && one_file.getName().toLowerCase().contains("database")){
                    // this is a database directory
                    Collections.addAll(db_files, one_file.listFiles()); // so damn cool
                }
            }
        }
        // add databases to the table layout
        for (File f:db_files
             ) {
            final String dbFileName = f.getName();
            TableRow dbTableRow = new TableRow(this);
            TextView dbTextView = new TextView(this);
            dbTextView.setText(dbFileName);
            dbTableRow.addView(dbTextView);
            // add button to read and delete
            Button readBt = new Button(this);
            readBt.setText(R.string.read_projects_read_db);
            readBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),ReadAProjectActivity.class);
                    intent.putExtra(EXTRA_MESSAGE_For_DbName,dbFileName);
                    startActivity(intent);
                }
            });
            dbTableRow.addView(readBt);

            Button delBt = new Button(this);
            delBt.setText(R.string.read_projects_delete_db);
            delBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDatabase(dbFileName);
                    recreate();
                }
            });
            dbTableRow.addView(delBt);

            if (dbTableLayout != null) {
                dbTableLayout.addView(dbTableRow);
            }
        }
    }

//    // show a list of existed databases, and click to open a selected database
//    // need to fix problem
//    public void ShowDatabases(GridView gridview, final TextView textview) {
//        // build dynamic data and set values
//        ArrayList<HashMap<String, Object>> tableRowItem = new ArrayList<HashMap<String, Object>>();
//        // get the database path
//        File app_root_file = getFilesDir().getParentFile();
//        File[] files = app_root_file.listFiles();
//        textview.append("\nApp root name:"+app_root_file.getName());
//        if (files.length > 0) {
//            for (File one_file : files
//                    ) {
//                if (one_file.isDirectory() && one_file.getName().contains("database")) {
//                    textview.append("\nfind  database dir :"+one_file.getAbsolutePath());
//                    final ArrayList<File> db_files = new ArrayList<File>();
//                    for (File dbfile : one_file.listFiles()
//                            ) {
//                        if(dbfile.getName().endsWith(".db")) {
//                            textview.append("\nfind  database:" + dbfile.getName());
//                            HashMap<String, Object> rowitem = new HashMap<String, Object>();
//                            rowitem.put("existeddb_row_dbname", dbfile.getName());
//                            tableRowItem.add(rowitem);
//                            // try to store a path into a array for click action,:)
//                            db_files.add(dbfile);
//                        }
//                    }
//                    // adapter
//                    // ref: http://developer.android.com/reference/android/widget/SimpleAdapter.html#SimpleAdapter(android.content.Context, java.util.List<? extends java.util.Map<java.lang.String, ?>>, int, java.lang.String[], int[])
//                    SimpleAdapter tableAdapater = new SimpleAdapter(this,
//                            tableRowItem,
//                            R.layout.existeddbs_row,
//                            //from, string
//                            new String[]{"existeddb_row_dbname"},
//                            //to R.id
//                            new int[]{R.id.existeddb_row_dbname}
//                    );
//                    // add adapater to the gridview
//                    gridview.setAdapter(tableAdapater);
//                    // add click listener
//                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        // ref http://developer.android.com/reference/android/widget/AdapterView.OnItemClickListener.html
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            openSelectedDataBase(position, id, db_files.get(position), textview);
//                        }
//                    });
//                    // the following is to: show a context menu on long click
//                    // 1st step: register
//                    registerForContextMenu(gridview);
//                    //2nd step: Implement the onCreateContextMenu() method in your Activity or Fragment.
//                    // 3rd step:Implement onContextItemSelected().
//                    break;
//                }
//            }
//
//        }
//        else
//        {textview.append("\n zeroes files in the root dir, call from show database");}
//    }
//    public void openSelectedDataBase(int pos,long row_Id,File file,TextView textView){
//        textView.setText("Pos,id,file name:\n" + pos + ", " + row_Id + ", " + file.getName() + "\n");
//        try {
//            // open the database
//            SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(),null,Context.MODE_PRIVATE);
//            // get the table name
//            ArrayList<String> tables_name = new ArrayList<String>();
//            Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
//            int c_colcount = c.getColumnCount();
//            textView.append(String.format("There are %d tables in the database, which has %d columns\n", c.getCount(),c_colcount));
//            String c_title = "";
//            for(int i=0;i<c_colcount;i++){
//                c_title=c_title+c.getColumnName(i)+" ";
//            }
//            textView.append("columns names:"+c_title+"\n");
//
//            if (c.moveToFirst()) {
//                //table_name = c.getString(0);
//                while ( !c.isAfterLast() ) {
//                    tables_name.add(c.getString(0));
//                    c.moveToNext();
//                }
//            }
//            c.close();
//            textView.append("existed tables name: "+ Arrays.toString(tables_name.toArray()) +"\n");
//            // get the required table name
//            String table_name = tables_name.get(tables_name.size()-1);
//            // query all the data
//            String sql = "SELECT * FROM "+table_name+";";
//            textView.append("query sql: " + sql + "\n");
//            Cursor rec = db.rawQuery(sql, null);
//            rec.moveToLast();
//            int databaselen=rec.getCount();//total num
//            textView.append(String.format("the total data: %d\n",databaselen));
//            String title="";
//            int colCount = rec.getColumnCount();
//            for(int i=0;i<colCount;i++){
//                title=title+rec.getColumnName(i)+" ";
//            }
//            textView.append("Preview\n"+title+"\n");
//            //read data
//            for(int i=0;i<databaselen;i++){
//                rec.moveToPosition(i);
//                String row_info="";
//                for(int j=0;j<colCount;j++){
//                    row_info = row_info + rec.getString(j)+" ";
//                }
//                // for now do not preview before fixing ui problem
//                // todo preview data
//                // textView.append(row_info+"\n");
//            }
//            //close rec
//            rec.close();
//            //close database
//            db.close();
//
//        } catch (Exception ex){
//            textView.append(ex.getMessage());
//        }
//
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v,
//                                    ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.floatrd_context_menu, menu);
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        // ref: http://blog.csdn.net/zl594389970/article/details/14145753
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        View view = info.targetView; // row view, in this case it is a linear layout, see in existeddbs_row.xml
//        TextView dbnamerowview = (TextView)view.findViewById(R.id.existeddb_row_dbname); // the text view in the row view
//        String dbname = dbnamerowview.getText().toString();
//        switch (item.getItemId()) {
//            // delete database
//            case R.id.floatrd_context_menu_delete:
//                DatabaseCRUDHelper dbhelper = new DatabaseCRUDHelper();
//                dbhelper.deleteSelectedDatabase(getApplicationContext(),dbname);
//                return true;
//            // edit the table/database
//            case R.id.floatrd_context_menu_edit:
//                // start Database CRUD activity
//                Intent intent = new Intent(this,ReadAProjectActivity.class);
//                intent.putExtra(EXTRA_MESSAGE_For_DbName,dbname);
//                startActivity(intent);
//                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }

}

//  simple click the database to open it, the debug info just show in the Log
/***
 * todo: use one fragment to show a list of database on the left and another fragment to display tables in the middle and the table content on the right
 */
//  apply scroll view in case there are too much information to show in one screen
/** todo questions:
 * 1. many fragments in the container, how to replace it, with order, and in our will
 */

