package com.wastedtimecounter.helpers;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Антон on 20.01.2016.
 */
public class WastedApp extends Application {
    private ArrayList<String> packages=new ArrayList<>();
    private final ArrayList<HashMap<String,Object>> processList=new ArrayList<>();
    public ArrayList<HashMap<String,Object>> getProcessList()
    {
        return processList;
    }
    public ArrayList<String> getPackages()
    {
        return packages;
    }
}
