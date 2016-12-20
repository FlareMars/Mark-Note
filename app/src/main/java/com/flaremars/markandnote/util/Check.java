package com.flaremars.markandnote.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * Created by FlareMars on 2016/11/5.
 */
public class Check {

    public static boolean isNull(Object object) {
        if (object == null) {
            return true;
        }
        return false;
    }

    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
