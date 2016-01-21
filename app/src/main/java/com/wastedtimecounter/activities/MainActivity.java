package com.wastedtimecounter.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

import com.wastedtimecounter.adapters.ApplicationAdapter;
import com.wastedtimecounter.R;
import com.wastedtimecounter.fragments.FragmentPageAdapter;
import com.wastedtimecounter.services.ApplicationsListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
                switch (position) {
                    case 0: {
                        Calendar calendar = Calendar.getInstance();

                        String year = String.valueOf(calendar.get(Calendar.YEAR));
                        String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
                        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

                        String date = day + "." + month + "." + year;
                        String args[] = {date};
                        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager(),
                                MainActivity.this, 1, args));
                        tabLayout.setupWithViewPager(viewPager);
                        break;
                    }
                    case 1: {
                        String args[] = {"Week 1", "Week 2"};
                        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager(),
                                MainActivity.this, 2, args));
                        tabLayout.setupWithViewPager(viewPager);
                        break;
                    }
                    case 2: {
                        String args[] = {"Month 1", "Month 2", "Month 3"};
                        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager(),
                                MainActivity.this, 3, args));
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

}
