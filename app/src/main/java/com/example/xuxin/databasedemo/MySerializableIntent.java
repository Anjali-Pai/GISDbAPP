package com.example.xuxin.databasedemo;


import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by XuXin on 2016/3/18.
 */
public class MySerializableIntent implements Serializable{
    private static final long serialVersionUID = 1L;
    private LinkedHashMap<String,HashMap<String,String>> data;
//    public MySerializableIntent(LinkedHashMap<String,HashMap<String,String>> inputData){
//        this.data=inputData;
//    }
    public void setData(LinkedHashMap<String,HashMap<String,String>> data){
        this.data = data;
    }
    public LinkedHashMap<String,HashMap<String,String>> getData(){
        return  this.data;
    }
}
