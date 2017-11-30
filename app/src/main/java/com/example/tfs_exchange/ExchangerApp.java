package com.example.tfs_exchange;

import android.app.Application;
import android.content.Context;

import com.example.tfs_exchange.db.DBHelper;

/**
 * Created by pusya on 29.11.17.
 */

public class ExchangerApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //DBHelper.getInstance();
    }

    public static Context getContext() {
        return context;
    }
}
