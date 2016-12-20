package com.flaremars.markandnote.dagger2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.flaremars.markandnote.event.MyEventBusIndex;
import com.flaremars.markandnote.util.ActivityLifecycleHelper;
import com.flaremars.markandnote.util.SharedPrefHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by FlareMars on 2016/11/4.
 */
@Module
public class AppModule {

    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
//                .addInterceptor(httpLoggingInterceptor)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okhttpClient) {
        return new Retrofit.Builder()
                .client(okhttpClient)
                .baseUrl("http://192.168.1.5:5000")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    public SharedPrefHelper provideSharedPrefHelper(SharedPreferences sharedPreferences) {
        return new SharedPrefHelper(sharedPreferences);
    }

    @Provides
    @Singleton
    public ActivityLifecycleHelper provideActivityLifecycleHelper() {
        return new ActivityLifecycleHelper();
    }

    @Provides
    @Singleton
    public EventBus providerEventBus() {
        return EventBus.builder().addIndex(new MyEventBusIndex()).build();
    }

    @Provides
    public Context provideContext() {
        return context;
    }
}
