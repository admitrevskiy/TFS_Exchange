package com.example.tfs_exchange;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * Created by pusya on 29.11.17.
 */

public class ExchangerApp extends Application {
    private static Context context;
    private static Resources resources;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        resources = getResources();
    }

    public static Context getContext() {
        return context;
    }
    public static Resources getAppResources() {return  resources;}
}
