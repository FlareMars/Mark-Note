package com.flaremars.markandnote.dagger2;

import com.flaremars.markandnote.common.BaseActivity;
import com.flaremars.markandnote.util.ActivityLifecycleHelper;
import com.flaremars.markandnote.util.SharedPrefHelper;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by FlareMars on 2016/11/4.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    OkHttpClient getOKHttpClient();

    Retrofit getRetrofit();

    EventBus getEventBus();

    SharedPrefHelper getSharedPrefHelper();

    ActivityLifecycleHelper getActivityLifecycleHelper();

    void inject(BaseActivity activity);
}
