package com.wastedtimecounter.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wastedtimecounter.R;

import java.util.List;

/**
 * Created by Антон on 18.01.2016.
 */
public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
    private List<ApplicationInfo> applicationInfos = null;
    private Context context;
    private PackageManager packageManager;


    public ApplicationAdapter(Context context, int resource, List<ApplicationInfo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.applicationInfos = objects;
        packageManager = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return ((null != applicationInfos) ? applicationInfos.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != applicationInfos) ? applicationInfos.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_app_item, null);
        }
        ApplicationInfo applicationInfo = applicationInfos.get(position);
        if (applicationInfos != null) {
            TextView name = (TextView) view.findViewById(R.id.app_name);
            TextView packageName = (TextView) view.findViewById(R.id.app_paackage);
           // ImageView icon = (ImageView) view.findViewById(R.id.app_icon);

            name.setText(applicationInfo.loadLabel(packageManager));
            packageName.setText(applicationInfo.packageName);
           // icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
        }
        return view;
    }
}
