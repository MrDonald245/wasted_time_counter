package com.wastedtimecounter.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.wastedtimecounter.R;
import com.wastedtimecounter.adapters.ApplicationAdapter;
import com.wastedtimecounter.preferences.support.ThemeSupport;
import com.wastedtimecounter.services.ApplicationsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PackageManager packageManager = null;
    private List<ApplicationInfo> infoList = null;
    private ApplicationAdapter adapter = null;
    private ListView listView;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeSupport.setActivityTheme(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.appList);
        packageManager = getPackageManager();
        new LoadApplications().execute();
        startService(new Intent(this, ApplicationsListener.class));


    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Menu handler.
     *
     * @param item View which call the handler.
     * @return true if all is ok, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                openPreferenceActivity();
                return true;
            default:
                return false;
        }
    }


    /**
     * Show preference activity.
     */
    private void openPreferenceActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
