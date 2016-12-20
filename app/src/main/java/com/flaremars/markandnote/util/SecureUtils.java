package com.flaremars.markandnote.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by FlareMars on 2016/6/23.
 * 数据安全工具类
 */
public enum SecureUtils {
    INSTANCE;

    public static final int SDK_INT_M = 23;

    public boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean shouldShowRequestPermissionDialog(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void requestExternalStoragePermissions(Activity activity, int requestCode) {
        requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
    }

    public int getSDKVersionNumber() {
        return Build.VERSION.SDK_INT;
    }
}
