package com.horry.mvp_dagger2_retrofit_demo;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.horry.mvp_dagger2_retrofit_demo.data.AppServiceModule;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

/**
 * Created by clevo on 2015/6/9.
 */
public class AppApplication  extends Application{

    private AppComponent appComponent;

    public static AppApplication get(Context context){
        return (AppApplication)context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent=DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .appServiceModule(new AppServiceModule())
                .build();
        enabledStrictMode();
        LeakCanary.install(this);
    }


    public AppComponent getAppComponent() {
        return appComponent;
    }
    private void enabledStrictMode() {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
                    .detectAll() //
                    .penaltyLog() //
                    .penaltyDeath() //
                    .build());
        }
    }
}