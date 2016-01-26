package com.wastedtimecounter.adapters;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.wastedtimecounter.R;
import com.wastedtimecounter.realm.ApplicationRealm;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Антон on 26.01.2016.
 */
public class CustomDialogAdapter extends BaseAdapter {
    List<ApplicationInfo> packageInfoList;
    Activity context;
    PackageManager packageManager;
    boolean[] itemChecked;
    Realm realm = Realm.getDefaultInstance();


    public CustomDialogAdapter(Activity context, List<ApplicationInfo> packageList,
                               PackageManager packageManager) {
        super();
        this.context = context;
        this.packageInfoList = packageList;
        this.packageManager = packageManager;
        itemChecked = new boolean[packageList.size()];
    }

    private class ViewHolder {
        TextView apkName;
        CheckBox ck1;
    }

    public int getCount() {
        return packageInfoList.size();
    }

    public Object getItem(int position) {
        return packageInfoList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_dialog_item, null);
            holder = new ViewHolder();

            holder.apkName = (TextView) convertView
                    .findViewById(R.id.dlgText);
            holder.ck1 = (CheckBox) convertView
                    .findViewById(R.id.dlgBox);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        final ApplicationInfo packageInfo = (ApplicationInfo) getItem(position);

        Drawable appIcon = packageManager
                .getApplicationIcon(packageInfo);
        String appName = packageManager.getApplicationLabel(
                packageInfo).toString();
        appIcon.setBounds(0, 0, 40, 40);
        holder.apkName.setCompoundDrawables(appIcon, null, null, null);
        holder.apkName.setCompoundDrawablePadding(15);
        holder.apkName.setText(appName);
        holder.ck1.setChecked(false);

        itemChecked[position] = isChecked(position);

        if (itemChecked[position])
            holder.ck1.setChecked(true);
        else
            holder.ck1.setChecked(false);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemChecked[position]) {
                    holder.ck1.setChecked(false);
                    itemChecked[position] = false;

                    removeChange(position);
                } else {
                    holder.ck1.setChecked(true);
                    itemChecked[position] = true;
                    Toast.makeText(parent.getContext(), packageInfo.processName, Toast.LENGTH_SHORT).show();

                    saveChange(position);
                }
            }
        };

        holder.ck1.setOnClickListener(listener);
        holder.apkName.setOnClickListener(listener);

        return convertView;

    }


    /**
     * When the custom dialog load this method initialize checkbox.
     *
     * @param position current position.
     * @return boolean true if value exist in db.
     */
    private boolean isChecked(int position) {
        realm.beginTransaction();

        RealmResults<ApplicationRealm> query = realm
                .where(ApplicationRealm.class)
                .equalTo("packageName", packageInfoList.get(position).packageName)
                .findAll();

        realm.commitTransaction();

        return query.size() != 0;
    }


    /**
     * Remove current change in realm db.
     *
     * @param position current position.
     */
    private void removeChange(int position) {
        realm.beginTransaction();

        RealmResults<ApplicationRealm> query = realm
                .where(ApplicationRealm.class)
                .equalTo("packageName", packageInfoList.get(position).packageName)
                .findAll();

        query.clear();

        realm.commitTransaction();
    }


    /**
     * Save current change in realm db.
     *
     * @param position current position.
     */
    private void saveChange(int position) {
        realm.beginTransaction();

        ApplicationRealm appRealm = realm.createObject(ApplicationRealm.class);
        appRealm.setAppName((String) packageManager.getApplicationLabel(packageInfoList.get(position)));
        appRealm.setPackageName(packageInfoList.get(position).packageName);


        realm.commitTransaction();

    }
}
