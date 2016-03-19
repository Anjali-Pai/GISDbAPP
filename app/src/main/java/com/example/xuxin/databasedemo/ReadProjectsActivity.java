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
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Array;
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
    public void ShowDatabases(GridView gridview,TextView textview) {
        // build dynamic data and set values
        ArrayList<HashMap<String, Object>> tableRowItem = new ArrayList<HashMap<String, Object>>();
        // HashMap<String, Object> rowitem = new HashMap<String, Object>();

        // get the database path
        File app_root_file = getFilesDir().getParentFile();
        File[] files = app_root_file.listFiles();
        textview.append("\n app root:"+app_root_file.getName());
        if (files.length > 0) {
            for (File one_file : files
                    ) {
                if (one_file.isDirectory() && one_file.getName().contains("database")) {
                    textview.append("\nfind  database dir :"+one_file.getAbsolutePath());
                    for (File dbfile : one_file.listFiles()
                            ) {
                        if(dbfile.getName().endsWith(".db")) {
                            textview.append("\nfind  database:" + dbfile.getName());
                            HashMap<String, Object> rowitem = new HashMap<String, Object>();
                            rowitem.put("existeddb_row_dbname", dbfile.getName());
                            tableRowItem.add(rowitem);
                            // try to store a path into a array for click action,:)
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
                    // add listener
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        // ref http://developer.android.com/reference/android/widget/AdapterView.OnItemClickListener.html
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            openDataBase(position, id);
                        }
                    });
                    break;
                }
            }

        }
        else
        {textview.append("\n zeroes files in the root dir, call from show database");}
    }
    public void openDataBase(int pos,long row_Id){}
}

/**
 * log
 * 3/18/2016
 * finish file system to show stored files in the internal storage
 * show the existed database files
 * */
//        if(app_root_file.isDirectory()){
//            // show the file system
//            File[] files = app_root_file.listFiles();
//            if(files.length>0){
//                resultTextView.append("---------------File System------------------\n"); //Arrays.toString(files)
//                for (File one_file : files
//                     ) {
//                    resultTextView.append("..."+one_file.getName()+"\n");
//                    // show the database
//                    if(one_file.getName().contains("database")){
//                        String[] database_files = one_file.list();
//                        for (String dbname: database_files
//                             ) {
//                            resultTextView.append("......"+dbname+"\n");
//
//                        }
//                    }
//                }
//            }
//            else
//            {resultTextView.setText("file length < 0");}
//        }
//        else
//        {resultTextView.setText("There are no files in this app");}