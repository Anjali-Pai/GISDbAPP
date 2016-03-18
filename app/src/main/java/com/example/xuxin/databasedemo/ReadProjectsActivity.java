package com.example.xuxin.databasedemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.Arrays;

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

        String[] files_list = getApplicationContext().fileList();
        TextView infoTextView = (TextView) findViewById(R.id.read_pro_debug_info);
        if(files_list.length>0) {
            int file_num = files_list.length;
            infoTextView.setText("File number:"+Integer.toString(file_num)+"\n");
            String file_name = Arrays.toString(files_list);
            TextView resultTextView = (TextView) findViewById(R.id.existed_projects_names);
            resultTextView.setText(file_name);
        }
        else {
            // create a default one table-database for user
            String nofile_info = "There is no database already existed, a default test database is created for test now....\n";
            infoTextView.setText(nofile_info);
            String placeholder = "><";
            TextView resultTextView = (TextView) findViewById(R.id.existed_projects_names);
            resultTextView.setText(placeholder);
        }
    }

    public void CreateDefaultDatabase(){

    }

}
/*
 */
