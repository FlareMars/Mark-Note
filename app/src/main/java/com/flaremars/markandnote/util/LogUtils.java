package com.flaremars.markandnote.util;

import android.util.Log;

/**
 * Created by FlareMars on 2016/11/14.
 */
public class LogUtils {

    public static void logE(String tag, String content) {
        Log.e(tag, content);
    }

    public static void logD(String tag, String content) {
        Log.d(tag, content);
    }
}
