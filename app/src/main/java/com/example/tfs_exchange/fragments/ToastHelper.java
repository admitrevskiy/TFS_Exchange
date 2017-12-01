package com.example.tfs_exchange.fragments;

import android.widget.Toast;

import com.example.tfs_exchange.ExchangerApp;

/**
 * Created by pusya on 15.10.17.
 */

public class ToastHelper {
    public static void showToast(String text) {
        Toast toast = Toast.makeText(ExchangerApp.getContext(), text, Toast.LENGTH_LONG);
        toast.show();
    }
}