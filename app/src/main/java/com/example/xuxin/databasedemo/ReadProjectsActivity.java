package com.example.xuxin.databasedemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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

        String welcome_str = "There following is existed files in the internal storage:";
        String[] files_list = getApplicationContext().fileList();// this.getFilesDir().list(); // or try fileList()
        welcome_str = welcome_str +files_list.length;
        for (String str:files_list
             ) {welcome_str.concat("\n" + str);

        }
        TextView resultTextView = (TextView)findViewById(R.id.existed_projects_names);
        resultTextView.setText(welcome_str);
    }

}
