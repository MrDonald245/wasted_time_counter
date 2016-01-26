package com.wastedtimecounter.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.wastedtimecounter.R;
import com.wastedtimecounter.adapters.CustomDialogAdapter;
import com.wastedtimecounter.fragments.FragmentPageAdapter;
import com.wastedtimecounter.preferences.support.ThemeSupport;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    String arr[];
    ViewPager viewPager;
    TabLayout tabLayout;
    Spinner spinner;
    PackageManager packageManager;
    ArrayList<String> checkedValue;
    FloatingActionButton fab;


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeSupport.setActivityTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        packageManager=getPackageManager();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        fab=(FloatingActionButton)findViewById(R.id.addAppsButton);


        this.arr = new String[]{"Day", "Week", "Month"};
        spinner = (Spinner) findViewById(R.id.spinner_nav);
        initSpinner();
        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager(),
                MainActivity.this, 1, arr));
        tabLayout.setupWithViewPager(viewPager);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCustomDialogItems();
            }
        });

        // Give the TabLayout the ViewPager


    }

    /**
     * {@inheritDoc}
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private void initCustomDialogItems()
    {
        final List<ApplicationInfo> packageInfos=packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        final List<ApplicationInfo> applicationInfoList=packageManager.getInstalledApplications(0);

        try
        {
            applicationInfoList.clear();
            for(int i=0;i<packageInfos.size();i++)
            {
                ApplicationInfo info=packageInfos.get(i);
                if((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                {
                    try
                    {
                        applicationInfoList.add(packageInfos.get(i));
                        Collections.sort(packageInfos, new Comparator<ApplicationInfo>() {
                            @Override
                            public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
                                return lhs.loadLabel(getPackageManager()).toString().compareToIgnoreCase(rhs.loadLabel(getPackageManager()).toString());
                            }
                        });
                    }
                    catch (NullPointerException e)
                    {
                        e.printStackTrace();
                    }
                    CustomDialogAdapter adapter=new CustomDialogAdapter(this,packageInfos,packageManager);
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create();
                    builder.show();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void initSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arr);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Calendar calendar = Calendar.getInstance();
                Resources res = getResources();
                switch (position) {
                    case 0: {
                        String args[] = new String[calendar.get(Calendar.DAY_OF_MONTH)];
                        for (int i = calendar.get(Calendar.DAY_OF_MONTH); i >= 1; i--) {

                            String month = formatMonth(Calendar.MONTH - 1, Locale.getDefault());
                            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) - i + 1);

                            String date = day + " " + month;
                            args[i - 1] = date;
                        }
                        Collections.reverse(Arrays.asList(args));
                        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager(),
                                MainActivity.this, calendar.get(Calendar.DAY_OF_MONTH), args));

                        tabLayout.setupWithViewPager(viewPager);
                        break;
                    }
                    case 1: {
                        String args[] = new String[calendar.get(Calendar.WEEK_OF_MONTH)];
                        for (int i = calendar.get(Calendar.WEEK_OF_MONTH); i >= 1; i--) {

                            String wom = String.valueOf(calendar.get(Calendar.WEEK_OF_MONTH) - i + 1);

                            String date = wom + " " + getString(R.string.week_of_month);
                            args[i - 1] = date;
                        }
                        Collections.reverse(Arrays.asList(args));
                        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager(),
                                MainActivity.this, calendar.get(Calendar.WEEK_OF_MONTH), args));

                        tabLayout.setupWithViewPager(viewPager);
                        break;
                    }
                    case 2: {
                        String args[] = new String[calendar.get(Calendar.MONTH) + 1];
                        for (int i = calendar.get(Calendar.MONTH) + 1; i >= 1; i--) {

                            String date = res.getStringArray(R.array.months)[Calendar.MONTH - 2];
                            args[i - 1] = date;
                        }
                        Collections.reverse(Arrays.asList(args));
                        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager(),
                                MainActivity.this, calendar.get(Calendar.MONTH) + 1, args));

                        tabLayout.setupWithViewPager(viewPager);
                        break;
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    /**
     * Show preference activity.
     */
    private void openPreferenceActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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



    public String formatMonth(int month, Locale locale) {
        DateFormat formatter = new SimpleDateFormat("MMMM", locale);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month - 1);
        return formatter.format(calendar.getTime());
    }
}
