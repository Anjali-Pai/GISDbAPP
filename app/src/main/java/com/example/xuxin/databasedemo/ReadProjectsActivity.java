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
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Read existed projects:
 * list the existed databases
 * click to open it
 * data CRUD: create, read, upgrade and delete
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        TextView infoTextView = (TextView) findViewById(R.id.read_pro_debug_info);
        TextView resultTextView = (TextView) findViewById(R.id.existed_projects_names);

        File filesDir = getFilesDir();
        infoTextView.setText("File Path" + filesDir.getAbsolutePath());
        File app_root_file = filesDir.getParentFile();
        infoTextView.append("\nAPP root Path:" + app_root_file.getAbsolutePath());
        resultTextView.setText("===================File System===============\n");
        ShowFileSystem(app_root_file, resultTextView, 0);
        resultTextView.append("\n=================Databases==================\n");
        // get the grid view
        GridView tableGridView = (GridView) findViewById(R.id.existed_db_gridview);
        ShowDatabases(tableGridView,infoTextView);

    }

    /**
     * getDir
     * http://developer.android.com/reference/android/content/Context.html#getDir(java.lang.String, int)
     */
    public void ShowFileSystem(File dirfile, TextView textview, int level) {
        for (int i = 0; i < level; i++) {
            textview.append("---");
        }
        if (dirfile.isDirectory()) {
            textview.append(dirfile.getName() + "\n");
            level++;
            for (File one_file : dirfile.listFiles()
                    ) {
                ShowFileSystem(one_file, textview, level);
            }
        } else {
            textview.append(dirfile.getName() + "\n");
        }

    }

    // show a list of existed databases, and click to open a selected database
    // need to fix problem
    public void ShowDatabases(GridView gridview, final TextView textview) {
        // build dynamic data and set values
        ArrayList<HashMap<String, Object>> tableRowItem = new ArrayList<HashMap<String, Object>>();
        // get the database path
        File app_root_file = getFilesDir().getParentFile();
        File[] files = app_root_file.listFiles();
        textview.append("\nApp root name:"+app_root_file.getName());
        if (files.length > 0) {
            for (File one_file : files
                    ) {
                if (one_file.isDirectory() && one_file.getName().contains("database")) {
                    textview.append("\nfind  database dir :"+one_file.getAbsolutePath());
                    final ArrayList<File> db_files = new ArrayList<File>();
                    for (File dbfile : one_file.listFiles()
                            ) {
                        if(dbfile.getName().endsWith(".db")) {
                            textview.append("\nfind  database:" + dbfile.getName());
                            HashMap<String, Object> rowitem = new HashMap<String, Object>();
                            rowitem.put("existeddb_row_dbname", dbfile.getName());
                            tableRowItem.add(rowitem);
                            // try to store a path into a array for click action,:)
                            db_files.add(dbfile);
                        }
                    }
                    // adapter
                    // ref: http://developer.android.com/reference/android/widget/SimpleAdapter.html#SimpleAdapter(android.content.Context, java.util.List<? extends java.util.Map<java.lang.String, ?>>, int, java.lang.String[], int[])
                    SimpleAdapter tableAdapater = new SimpleAdapter(this,
                            tableRowItem,
                            R.layout.existeddbs_row,
                            //from, string
                            new String[]{"existeddb_row_dbname"},
                            //to R.id
                            new int[]{R.id.existeddb_row_dbname}
                    );
                    // add adapater to the gridview
                    gridview.setAdapter(tableAdapater);
                    // add click listener
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        // ref http://developer.android.com/reference/android/widget/AdapterView.OnItemClickListener.html
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            openSelectedDataBase(position, id, db_files.get(position), textview);
                        }
                    });
                    // the following is to: show a context menu on long click
                    // 1st step: register
                    registerForContextMenu(gridview);
                    //2nd step: Implement the onCreateContextMenu() method in your Activity or Fragment.
                    // 3rd step:Implement onContextItemSelected().
                    break;
                }
            }

        }
        else
        {textview.append("\n zeroes files in the root dir, call from show database");}
    }
    public void openSelectedDataBase(int pos,long row_Id,File file,TextView textView){
        textView.setText("Pos,id,file name:\n" + pos + ", " + row_Id + ", " + file.getName() + "\n");
        try {
            // open the database
            SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(),null,Context.MODE_PRIVATE);
            // get the table name
            ArrayList<String> tables_name = new ArrayList<String>();
            Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            int c_colcount = c.getColumnCount();
            textView.append(String.format("There are %d tables in the database, which has %d columns\n", c.getCount(),c_colcount));
            String c_title = "";
            for(int i=0;i<c_colcount;i++){
                c_title=c_title+c.getColumnName(i)+" ";
            }
            textView.append("columns names:"+c_title+"\n");

            if (c.moveToFirst()) {
                //table_name = c.getString(0);
                while ( !c.isAfterLast() ) {
                    tables_name.add(c.getString(0));
                    c.moveToNext();
                }
            }
            c.close();
            textView.append("existed tables name: "+ Arrays.toString(tables_name.toArray()) +"\n");
            // get the required table name
            String table_name = tables_name.get(tables_name.size()-1);
            // query all the data
            String sql = "SELECT * FROM "+table_name+";";
            textView.append("query sql: " + sql + "\n");
            Cursor rec = db.rawQuery(sql, null);
            rec.moveToLast();
            int databaselen=rec.getCount();//total num
            textView.append(String.format("the total data: %d\n",databaselen));
            String title="";
            int colCount = rec.getColumnCount();
            for(int i=0;i<colCount;i++){
                title=title+rec.getColumnName(i)+" ";
            }
            textView.append("Preview\n"+title+"\n");
            //read data
            for(int i=0;i<databaselen;i++){
                rec.moveToPosition(i);
                String row_info="";
                for(int j=0;j<colCount;j++){
                    row_info = row_info + rec.getString(j)+" ";
                }
                textView.append(row_info+"\n");
            }
            //close rec
            rec.close();
            //close database
            db.close();

        } catch (Exception ex){
            textView.append(ex.getMessage());
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.floatrd_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // ref: http://blog.csdn.net/zl594389970/article/details/14145753
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        View view = info.targetView; // row view, in this case it is a linear layout, see in existeddbs_row.xml
        TextView dbnamerowview = (TextView)view.findViewById(R.id.existeddb_row_dbname); // the text view in the row view
        String dbname = dbnamerowview.getText().toString();
        switch (item.getItemId()) {
            // delete database
            case R.id.floatrd_context_menu_delete:
                DatabaseCRUDHelper dbhelper = new DatabaseCRUDHelper();
                dbhelper.deleteSelectedDatabase(getApplicationContext(),dbname);
                return true;
            // edit the table/database
            case R.id.floatrd_context_menu_edit:
                // start Database CRUD activity
                Intent intent = new Intent(this,DatabaseCRUDActivity.class);
                intent.putExtra(EXTRA_MESSAGE_For_DbName,dbname);
                startActivity(intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}

// todo apply scroll view in case there are too much information to show in one screen

