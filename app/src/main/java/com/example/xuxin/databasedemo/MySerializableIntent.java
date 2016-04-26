package com.example.xuxin.databasedemo;


import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by XuXin on 2016/3/18.
 */
public class MySerializableIntent implements Serializable{
    private static final long serialVersionUID = 1L;
    private HashMap<String,HashMap<String,String>> data;
//    public MySerializableIntent(HashMap<String,HashMap<String,String>> inputData){
//        this.data=inputData;
//    }
    public void setData(HashMap<String,HashMap<String,String>> data){
        this.data = data;
    }
    public HashMap<String,HashMap<String,String>> getData(){
        return  this.data;
    }
}
