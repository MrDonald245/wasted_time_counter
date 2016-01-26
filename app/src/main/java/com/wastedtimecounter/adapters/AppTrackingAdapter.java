package com.wastedtimecounter.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.wastedtimecounter.R;
import com.wastedtimecounter.realm.ApplicationRealm;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

import io.realm.RealmResults;

/**
 * Created by Антон on 26.01.2016.
 */
public class AppTrackingAdapter extends BaseAdapter {

    RealmResults<ApplicationRealm> results;
    Activity context;

    public AppTrackingAdapter(RealmResults<ApplicationRealm> results,Activity context)
    {
        this.results=results;
        this.context=context;
    }
    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        convertView=inflater.inflate(R.layout.list_app_item,null);

        TextView textView=(TextView)convertView.findViewById(R.id.app_paackage);
        textView.setText(results.get(position).getAppName());

        TextView progressBar=(TextView)convertView.findViewById(R.id.res_progress);
        Integer secs=(int)(long)results.get(position).getSecondsCount();

        progressBar.setText(secsToTime(secs));

        return convertView;
    }

    private String secsToTime(long secs)
    {
        int hours=(int)secs/3600;
        int remainder=(int)secs-hours*3600;
        int mins=remainder/60;
        remainder=remainder-mins*60;
        int sec=remainder;

        return hours+" hours "+mins+ " mins "+sec+" sec";

    }
}
