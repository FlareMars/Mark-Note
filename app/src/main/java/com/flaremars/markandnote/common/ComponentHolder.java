package com.flaremars.markandnote.common;


import com.flaremars.markandnote.dagger2.AppComponent;

/**
 * Created by FlareMars on 2016/11/4.
 */
public class ComponentHolder {

    private static AppComponent appComponent;

    public static void setAppComponent(AppComponent appComponent) {
        ComponentHolder.appComponent = appComponent;
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
