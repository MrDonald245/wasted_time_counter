package com.wastedtimecounter.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Realm object.
 * Which save Applications data in db.
 * <p/>
 * Created by Eugene on 1/26/2016.
 */
public class ApplicationInfo extends RealmObject {

    @PrimaryKey
    private String packageName;
    private String appName;

    private long secondsCount;
}
