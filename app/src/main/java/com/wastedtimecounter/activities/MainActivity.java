package com.wastedtimecounter.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.wastedtimecounter.adapters.ApplicationAdapter;
import com.wastedtimecounter.R;
import com.wastedtimecounter.services.ApplicationsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PackageManager packageManager = null;
    private List<ApplicationInfo> infoList = null;
    private ApplicationAdapter adapter = null;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.appList);
        packageManager = getPackageManager();
        new LoadApplications().execute();
        startService(new Intent(this, ApplicationsListener.class));
    }

    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName))
                    applist.add(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return applist;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, null, "Loading");

        }

        @Override
        protected Void doInBackground(Void... params) {
            infoList = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            adapter = new ApplicationAdapter(MainActivity.this, R.layout.list_app_item, infoList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            listView.setAdapter(adapter);
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}
