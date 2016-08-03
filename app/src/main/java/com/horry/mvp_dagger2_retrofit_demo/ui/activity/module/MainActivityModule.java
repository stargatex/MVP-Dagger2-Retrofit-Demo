package com.horry.mvp_dagger2_retrofit_demo.ui.activity.module;


import com.horry.mvp_dagger2_retrofit_demo.ui.activity.ActivityScope;
import com.horry.mvp_dagger2_retrofit_demo.ui.activity.MainActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by clevo on 2015/6/10.
 */
@Module
public class MainActivityModule extends BaseModule<MainActivity> {

    public MainActivityModule(MainActivity viewer) {
        super(viewer);
    }

    @Provides
    @ActivityScope
    MainActivity providerView() {
        return viewer;
    }

}
