package com.rstack.dephone;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 3/25/2018.
 */

public class DatabaseClass {

    HashMap<String, ArrayList<Integer>> appData = new HashMap<>();
    public void setAppListTodaysTimings(String pkgName, int hour, int min){
        ArrayList<Integer> appDataEntry = new ArrayList<>();
        appDataEntry.add(hour);
        appDataEntry.add(min);
        appData.put(pkgName,appDataEntry);
        Log.i("Applistusage",appData.toString());
    }

    public HashMap<String,ArrayList<Integer>> gerAppListTodaysTimings(){
        return appData;
    }

    public void getHourByPackageName(String pkgName){
        Log.d("hourpkg", "getHourByPackageName: "+pkgName+" "+appData.toString());
        //return appData.get(pkgName);

    }

    public void getMinByPackageName(String pkgName){
        //return appData.get(pkgName).get(1);
    }
}
