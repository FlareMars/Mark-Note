package com.flaremars.markandnote.common;

import com.flaremars.markandnote.dagger2.AppComponent;
import com.flaremars.markandnote.dagger2.AppModule;
import com.flaremars.markandnote.dagger2.DaggerAppComponent;
import com.flaremars.markandnote.util.Check;

import org.litepal.LitePalApplication;

import cn.bmob.v3.Bmob;

public class App extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Bmob.initialize(this, "719fc6c14fd5dea55bbc22697bd78814");

        if (!Check.isApkInDebug(this)) {
            CrashHandler.getInstance().init(this);
        }

        AppComponent appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        ComponentHolder.setAppComponent(appComponent);
        registerActivityLifecycleCallbacks(appComponent.getActivityLifecycleHelper());
    }

}
