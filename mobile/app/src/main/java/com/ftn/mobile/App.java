package com.ftn.mobile;

import android.app.Application;

public class App extends Application {

    private static App instance;
    // one safe global application context for usage in long living classes(retrofit, okhttp interceptor)
    //without leaking an activity
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App get() {
        return instance;
    }
}