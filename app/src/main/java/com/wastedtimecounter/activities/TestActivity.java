package com.wastedtimecounter.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.wastedtimecounter.R;
import com.wastedtimecounter.adapters.ApplicationAdapter;
import com.wastedtimecounter.helpers.ProcessList;
import com.wastedtimecounter.helpers.WastedApp;
import com.wastedtimecounter.services.ApplicationsListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wastedtimecounter.helpers.ProcessList.COLUMN_PROCESS_COUNT;
import static com.wastedtimecounter.helpers.ProcessList.COLUMN_PROCESS_NAME;
import static com.wastedtimecounter.helpers.ProcessList.COLUMN_PROCESS_PROP;
import static com.wastedtimecounter.helpers.ProcessList.COLUMN_PROCESS_TIME;

public class TestActivity extends AppCompatActivity implements ApplicationsListener.ServiceCallback {

    private ArrayList<HashMap<String, Object>> processList;
    private ApplicationsListener bgService;
    private ProcessListAdapter adapter = null;
    private ListView listView = null;
    ProgressBar name;
    TextView packageName;
    private List<ApplicationInfo> applicationInfos;
    private ApplicationAdapter dialogAdapter;
    private PackageManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        listView = (ListView) findViewById(R.id.appListTest);

        createAdapter();
        startService(new Intent(this, ApplicationsListener.class));
        this.bindService(new Intent(this, ApplicationsListener.class),
                serviceConnection, Context.BIND_AUTO_CREATE);
        manager=getPackageManager();
        new customDialogLoader().execute();
    }

    private List<ApplicationInfo> checkInstApps(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> arrayList = new ArrayList<>();
        for (ApplicationInfo info : list) {
            try {
                if (manager.getLaunchIntentForPackage(info.packageName) != null)
                    arrayList.add(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    private void createAdapter() {
        processList = ((WastedApp) getApplication()).getProcessList();
        adapter = new ProcessListAdapter(this, processList, R.layout.list_app_item,
                new String[]{COLUMN_PROCESS_NAME, COLUMN_PROCESS_PROP},
                new int[]{android.R.id.text1, android.R.id.text2});

        listView.setAdapter(adapter);
    }

    @Override
    public void sendResult(int resultCode, Bundle b) {

        Long L = b.getLong("time");
        if (L != 0)
            name.setProgress(L.intValue());
        adapter.notifyDataSetChanged();
        Log.d("changed", "window");

    }

    private void initCustomDialog(ApplicationAdapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private class customDialogLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            applicationInfos = checkInstApps(manager.getInstalledApplications(PackageManager.GET_META_DATA));
            dialogAdapter = new ApplicationAdapter(TestActivity.this, R.layout.custom_dialog_item, applicationInfos);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            initCustomDialog(dialogAdapter);
            super.onPostExecute(aVoid);
        }
    }

    private class ProcessListAdapter extends SimpleAdapter {

        /**
         * Constructor
         *
         * @param context  The context where the View associated with this SimpleAdapter is running
         * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
         *                 Maps contain the data for each row, and should include all the entries specified in
         *                 "from"
         * @param resource Resource identifier of a view layout that defines the views for this list
         *                 item. The layout file should include at least those named views defined in "to"
         * @param from     A list of column names that will be added to the Map associated with each
         *                 item.
         * @param to       The views that should display column in the "from" parameter. These should all be
         *                 TextViews. The first N views in this list are given the values of the first N columns
         */
        public ProcessListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View res = super.getView(position, convertView, parent);

            name = (ProgressBar) res.findViewById(R.id.res_progress);
            packageName = (TextView) res.findViewById(R.id.app_paackage);


            // String count = (processList.get(position).get(COLUMN_PROCESS_TIME)).toString();
            String seconds = (String) processList.get(position).get(COLUMN_PROCESS_NAME);

            // name.setText(count);

            packageName.setText(seconds);

            return res;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ApplicationsListener.LocalBinder binder = (ApplicationsListener.LocalBinder) service;
            bgService = binder.getService();
            bgService.setCallback(TestActivity.this);
            bgService.start();
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            bgService = null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (bgService != null) {
            bgService.setCallback(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bgService != null) {
            bgService.setCallback(null);
        }
    }
}
