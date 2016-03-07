package com.example.xuxin.databasedemo;

import android.provider.BaseColumns;

/**
 * Use it later, seems complicated and less flexible
 * Define how the Database looks
 * ref: http://developer.android.com/training/basics/data-storage/databases.html
 */
public class TableScheme {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TableScheme(){}
    /* Inner class that defines the table contents */
    public static abstract class TableSchContent implements BaseColumns{
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_USER_SSN = "userssn";
        public static final String COLUMN_NAME_USER_NAME = "usrname";
    }

}
