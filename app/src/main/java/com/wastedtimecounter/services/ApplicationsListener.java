package com.wastedtimecounter.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.wastedtimecounter.activities.MainActivity;
import com.wastedtimecounter.helpers.ProcessList;
import com.wastedtimecounter.helpers.WastedApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import static com.wastedtimecounter.helpers.ProcessList.COLUMN_PROCESS_COUNT;
import static com.wastedtimecounter.helpers.ProcessList.COLUMN_PROCESS_NAME;
import static com.wastedtimecounter.helpers.ProcessList.COLUMN_PROCESS_PROP;
import static com.wastedtimecounter.helpers.ProcessList.COLUMN_PROCESS_TIME;

public class ApplicationsListener extends Service {


    private boolean initialized = false;
    private final IBinder mIBinder = new LocalBinder();
    private ServiceCallback callback = null;
    private Timer timer = null;
    private final Handler mHandler = new Handler();
    private String foreground = null;
    private ArrayList<HashMap<String, Object>> processList;
    private ArrayList<String> packages;
    private Date split = null;
    private Bundle b=new Bundle();

    public static int SERVICE_PERIOD = 5000;

    private final ProcessList pl=new ProcessList(this) {
        @Override
        protected boolean isFilteredByName(String pack) {
            //return !pack.equals("com.viber.voip");
            return false;
        }
    };

    public interface ServiceCallback {
        void sendResult(int resultCode, Bundle b);
    }

    public class LocalBinder extends Binder {
        public ApplicationsListener getService() {
            return ApplicationsListener.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialized = true;
        processList = ((WastedApp) getApplication()).getProcessList();
        packages = ((WastedApp) getApplication()).getPackages();

    }

    public void setCallback(ServiceCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (initialized)
            return mIBinder;
        return null;
    }

    private boolean addToStatistics(String target) {
        boolean changed = false;

            Date now = new Date();
            if (!TextUtils.isEmpty(target)) {
                if (!target.equals(foreground)) {
                    int i;
                    if (foreground != null && split != null) {
                        i = packages.indexOf(foreground);
                        long delta = (now.getTime() - split.getTime()) / 1000;
                        Long time = (Long) processList.get(i).get(COLUMN_PROCESS_TIME);
                        if (time != null) {
                            time += delta;
                            b.putLong("time", time);
                            Log.d("Adding", time.toString());
                        } else {

                            time = delta;
                            Log.d("Adding time in else", time.toString());
                        }
                        processList.get(i).put(ProcessList.COLUMN_PROCESS_TIME, time);
                    }
                    i = packages.indexOf(target);
                    Integer count = (Integer) processList.get(i).get(COLUMN_PROCESS_COUNT);
                    if (count != null) count++;
                    else {
                        count = new Integer(1);
                        Log.d("count", "count integer 1");
                    }
                    processList.get(i).put(COLUMN_PROCESS_COUNT, count);

                    foreground = target;
                    split = now;
                    changed = true;
                }
            }

            return changed;
    }
    public void start()
    {
        if(timer==null)
        {
            timer=new Timer();
            timer.schedule(new MonitoringTimeTask(),500,SERVICE_PERIOD);
            Log.d("Test","In start service");

        }
    }
    public void stop()
    {
        timer.cancel();
        timer.purge();
        timer=null;
    }
    private class MonitoringTimeTask extends TimerTask
    {

        @Override
        public void run() {
            fillProcessList();
            ActivityManager activityManager=(ActivityManager) ApplicationsListener.this.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo>taskInfos=activityManager.getRunningTasks(1);
            String current=taskInfos.get(0).topActivity.getPackageName();

            if(addToStatistics(current)&&callback!=null)
            {



                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.sendResult(1, b);
                    }
                });
                Log.d("test","in run ");
            }
        }
    }


    private void fillProcessList()
    {
        Log.d("test","filling list");
        Log.d("tt",COLUMN_PROCESS_NAME);
        Log.d("tt",COLUMN_PROCESS_TIME);
        pl.fillProcessList(processList,packages);
    }


}

