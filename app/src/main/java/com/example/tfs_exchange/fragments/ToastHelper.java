package com.example.tfs_exchange.fragments;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by pusya on 15.10.17.
 */

public class ToastHelper {
    public static void showToast(Activity activity, String text) {
        Toast toast = Toast.makeText(activity, text, Toast.LENGTH_LONG);
        toast.show();
    }
}