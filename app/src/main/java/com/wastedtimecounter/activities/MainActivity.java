package com.wastedtimecounter.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.DateFormatSymbols;

import com.wastedtimecounter.adapters.ApplicationAdapter;
import com.wastedtimecounter.R;
import com.wastedtimecounter.fragments.FragmentPageAdapter;
import com.wastedtimecounter.services.ApplicationsListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    String arr[];
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);


        this.arr = new String[]{"Day", "Week", "Month"};
        Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);
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
                        String args[]=new String[calendar.get(Calendar.DAY_OF_MONTH)];
                        for (int i = calendar.get(Calendar.DAY_OF_MONTH); i >=1; i--) {

                            String month = formatMonth(Calendar.MONTH - 1, Locale.getDefault());
                            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH) - i + 1);

                            String date = day + " " + month;
                            args[i-1] = date;
                        }
                        Collections.reverse(Arrays.asList(args));
                            viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager(),
                                    MainActivity.this, calendar.get(Calendar.DAY_OF_MONTH), args));

                        tabLayout.setupWithViewPager(viewPager);
                        break;
                    }
                    case 1: {
                        String args[]=new String[calendar.get(Calendar.WEEK_OF_MONTH)];
                        for (int i = calendar.get(Calendar.WEEK_OF_MONTH); i >=1; i--) {

                            String wom = String.valueOf(calendar.get(Calendar.WEEK_OF_MONTH) - i + 1);

                            String date = wom + " " + getString(R.string.week_of_month);
                            args[i-1] = date;
                        }
                        Collections.reverse(Arrays.asList(args));
                        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager(),
                                MainActivity.this, calendar.get(Calendar.WEEK_OF_MONTH), args));

                        tabLayout.setupWithViewPager(viewPager);
                        break;
                    }
                    case 2: {
                        String args[]=new String[calendar.get(Calendar.MONTH)+1];
                        for (int i = calendar.get(Calendar.MONTH)+1; i >=1; i--) {

                            String date = res.getStringArray(R.array.months)[Calendar.MONTH - 2];
                            args[i-1] = date;
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


        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager(),
                MainActivity.this, 1, arr));
        tabLayout.setupWithViewPager(viewPager);

        // Give the TabLayout the ViewPager


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }

    public String formatMonth(int month, Locale locale) {
        DateFormat formatter = new SimpleDateFormat("MMMM", locale);
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month-1);
        return formatter.format(calendar.getTime());
    }
}
