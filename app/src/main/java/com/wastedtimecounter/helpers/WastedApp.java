package com.wastedtimecounter.helpers;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Антон on 20.01.2016.
 */
public class WastedApp extends Application {
    private ArrayList<String> packages = new ArrayList<>();
    private final ArrayList<HashMap<String, Object>> processList = new ArrayList<>();


    /**
     * Called when the application is starting.
     * Set default realm configuration.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // Create realm configuration
        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext())
                .name("application.realm")
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public ArrayList<HashMap<String, Object>> getProcessList() {
        return processList;
    }

    public ArrayList<String> getPackages() {
        return packages;
    }
}
