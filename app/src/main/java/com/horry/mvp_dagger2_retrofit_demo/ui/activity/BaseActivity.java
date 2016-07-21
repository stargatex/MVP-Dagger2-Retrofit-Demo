package com.horry.mvp_dagger2_retrofit_demo.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import com.horry.mvp_dagger2_retrofit_demo.AppApplication;
import com.horry.mvp_dagger2_retrofit_demo.AppComponent;


/**
 * Created by clevo on 2015/6/10.
 */
public abstract  class BaseActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityComponent(AppApplication.get(this).getAppComponent());
    }

    protected abstract  void setupActivityComponent(AppComponent appComponent);
}