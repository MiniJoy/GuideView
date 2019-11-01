package com.demo.Application;

import com.demo.utils.ScreenUtils;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ScreenUtils.setContext(this);
    }
}
