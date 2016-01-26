package com.wastedtimecounter.helpers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Антон on 20.01.2016.
 */
public abstract class ProcessList {

    public static final String COLUMN_PROCESS_NAME = "process";
    public static final String COLUMN_PROCESS_PROP = "property";
    public static final String COLUMN_PROCESS_COUNT = "count";
    public static final String COLUMN_PROCESS_TIME = "time";

    private ContextWrapper contextWrapper;

    protected ProcessList(ContextWrapper contextWrapper) {
        this.contextWrapper = contextWrapper;
    }

    protected abstract boolean isFilteredByName(String pack);

    public void fillProcessList(ArrayList<HashMap<String,Object>> processListm,ArrayList<String> packages)
    {
        ActivityManager activityManager=(ActivityManager)contextWrapper.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos=activityManager.getRunningAppProcesses();
        HashMap<String,Object> hm;
        final PackageManager manager=contextWrapper.getApplicationContext().getPackageManager();

        for(int i=0;i<infos.size();i++)
        {
            String process=infos.get(i).processName;
            String packageList= Arrays.toString(infos.get(i).pkgList);
            if(!packageList.contains(process))
                process=infos.get(i).pkgList[0];
            if(!packages.contains(process)&&!isFilteredByName(process))
            {
                ApplicationInfo ai;
                String applicationName="";

                for(int j=0;j<infos.get(i).pkgList.length;j++)
                {
                    String thisPackage=infos.get(i).pkgList[j];

                    try
                    {
                        ai=manager.getApplicationInfo(thisPackage,0);
                    }
                    catch (final PackageManager.NameNotFoundException e)
                    {
                        ai=null;
                    }
                    if(j>0)applicationName+=" / ";
                    applicationName+=(String)(ai!=null?manager.getApplicationLabel(ai):"(unknown)");

                }
                packages.add(process);
                hm=new HashMap<>();
                hm.put(COLUMN_PROCESS_NAME,process);
                hm.put(COLUMN_PROCESS_PROP,applicationName);
                processListm.add(hm);
            }
        }
        packages.clear();

        for(HashMap<String,Object> e:processListm)
        {
            packages.add((String)e.get(COLUMN_PROCESS_NAME));
        }

    }
}
