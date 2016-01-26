package com.wastedtimecounter.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.wastedtimecounter.R;
import com.wastedtimecounter.activities.MainActivity;
import com.wastedtimecounter.adapters.AppTrackingAdapter;
import com.wastedtimecounter.helpers.WastedApp;
import com.wastedtimecounter.realm.ApplicationRealm;
import com.wastedtimecounter.services.ApplicationsListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.wastedtimecounter.helpers.ProcessList.COLUMN_PROCESS_NAME;
import static com.wastedtimecounter.helpers.ProcessList.COLUMN_PROCESS_PROP;

/**
 * Created by student on 21.01.2016.
 */
public class PageFragment extends Fragment implements ApplicationsListener.ServiceCallback {
    public static final String ARG_PAGE = "ARG_PAGE";

    ArrayList<HashMap<String, Object>> List;
    ApplicationsListener baListener;
    AppTrackingAdapter adapter;
    ListView listView;
    private int mPage;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        listView = (ListView) view.findViewById(R.id.trackingView);
        if(mPage== Calendar.getInstance().get(Calendar.DAY_OF_MONTH)-1) {
            createAdapter();
            getActivity().bindService(new Intent(getActivity(), ApplicationsListener.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }
        return view;
    }
    private void createAdapter() {
        List = ((WastedApp) getActivity().getApplication()).getProcessList();
        Realm realm=Realm.getDefaultInstance();
        RealmResults<ApplicationRealm> res=realm.where(ApplicationRealm.class).findAll();

        adapter = new AppTrackingAdapter(res,getActivity());
        listView.setAdapter(adapter);
    }

    @Override
    public void sendResult(int resultCode, Bundle b) {
        adapter.notifyDataSetChanged();

    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ApplicationsListener.LocalBinder binder = (ApplicationsListener.LocalBinder) service;
            baListener = binder.getService();
            baListener.setCallback(PageFragment.this);
            baListener.start();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            baListener = null;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (baListener != null)
            baListener.setCallback(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (baListener != null)
            baListener.setCallback(null);
    }

}
