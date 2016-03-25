package com.example.xuxin.databasedemo;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DatabaseCRUDActivity extends AppCompatActivity {
    private DatabaseCRUDHelper  dbcrudhelper = new DatabaseCRUDHelper();
    private ActionMode mActionMode;
    // long click + action mode ref:http://developer.android.com/guide/topics/ui/menus.html#CAB
    // todo improve the ui, it is strange because the top information line (wifi, battery, etc) display just a white
    // 1st implment the actionmode callback interface
    // 2nd: call startActionMode() to enable the cotextual action mode, in response to a long-click on view

    // for action mode
    // todo: get selected information
    // ref: http://developer.android.com/reference/android/widget/AdapterView.OnItemSelectedListener.html
    // ref: http://stackoverflow.com/questions/12939627/passing-id-of-listview-item-to-actionmode-callback-object
    // ref: http://stackoverflow.com/questions/20365052/how-to-retrieve-the-listview-selected-item-in-contextual-action-bar
    ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // called when the action mode is created, startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.data_crud_menu,menu);
            return true;
        }
        // called each time the action mode is shown, always called after onCreateActionMode.
        // but may be called multiple times if the mode is invalidated
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        // called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {

                default:
                    return false;
            }
        }

        // called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_crud);
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

        TextView textView = (TextView) findViewById(R.id.DbCURD_info);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.dbcrud_table);
        TableLayout inserttable = (TableLayout) findViewById(R.id.dbcrud_insert_table);

        Intent intent = getIntent();
        String received_dbname = intent.getStringExtra(ReadProjectsActivity.EXTRA_MESSAGE_For_DbName);
        textView.setText(String.format("Database name: %s\n", received_dbname));
        final String db_path = getFilesDir().getParent()+"/databases/"+received_dbname;
        textView.append(String.format("Access to: %s\n", db_path));

        SQLiteDatabase db = SQLiteDatabase.openDatabase(db_path,null, Context.MODE_PRIVATE);
        // show the database info
        // ref: http://bxbxbai.github.io/2014/07/16/context/
        dbcrudhelper.readSelectedDatabase(DatabaseCRUDActivity.this,this,textView,tableLayout,db); // in this method, context should be activity context
        // create input table
        if(!db.isOpen()){db = SQLiteDatabase.openDatabase(db_path,null, Context.MODE_PRIVATE);}
        dbcrudhelper.createCreateDataTable(this, textView, inserttable, db);
        if(db.isOpen()){db.close();}

        // add contextaul menu in the tablelayout
        for(int i = 2;i < tableLayout.getChildCount();i++){
            Log.i("TableRow",String.format("@row%d,child: %s",i,tableLayout.getChildAt(i).toString()));
            // ignore the child 0 (column name row)and 1 (textview for line)
            TableRow tb = (TableRow) tableLayout.getChildAt(i);
            // get the primary table
            tb.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mActionMode != null) {
                        Log.i("ActionMode","Already have a ActionMode instance");
                        return false;
                    }
                    Log.i("ActionMode","Null instance, to create...");
                    mActionMode =startActionMode(mActionModeCallback);
                    v.setSelected(true);
                    return true;
                }
            });
        }

    }
}
