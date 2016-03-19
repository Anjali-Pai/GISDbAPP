package com.example.xuxin.databasedemo;

import android.provider.BaseColumns;

/**
 * Use it later, seems complicated and less flexible
 * Define how the Database looks
 * ref: http://developer.android.com/training/basics/data-storage/databases.html
 * --or--
 * ref: http://www.vogella.com/tutorials/AndroidSQLite/article.html#sqliteoverview
 *  This class is our model and contains the data we will save in the database and show in the user interface.
 *  for the app requirement, use generic later
 */
public class TableScheme {
//    // To prevent someone from accidentally instantiating the contract class,
//    // give it an empty constructor.
//    public TableScheme(){}
//    /* Inner class that defines the table contents */
//    public static abstract class TableSchContent implements BaseColumns{
//        public static final String TABLE_NAME = "users";
//        public static final String COLUMN_NAME_USER_SSN = "userssn";
//        public static final String COLUMN_NAME_USER_NAME = "usrname";
//    }
    private int id;
    private int userssn;
    private String username;

    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getUserssn(){
        return userssn;
    }
    public void setUserssn(int ssn){
        this.userssn = ssn;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String name){
        this.username = name;
    }
    @Override
    public String toString() {
        return username + "," + userssn;
    }
}
