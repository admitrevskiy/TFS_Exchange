package com.example.tfs_exchange.Main;

import android.os.Bundle;
import android.util.Log;

import com.example.tfs_exchange.fragments.AnalyticsFragment;
import com.example.tfs_exchange.fragments.CurrencySelectFragment;
import com.example.tfs_exchange.fragments.HisroryFragment;

/**
 * Created by pusya on 11.12.17.
 */

public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = "MainPresenter";

    private MainContract.View mView;


    CurrencySelectFragment currencySelectFragment;
    HisroryFragment historyFragment;
    AnalyticsFragment analyticsFragment;

    public MainPresenter(MainContract.View mView) {
        this.mView = mView;
        currencySelectFragment = new CurrencySelectFragment();
        historyFragment = new HisroryFragment();
        analyticsFragment = new AnalyticsFragment();
    }


    @Override
    public void onExchangeClicked() {
        mView.replaceFragment(currencySelectFragment);
    }

    @Override
    public void onHistoryClicked() {
        mView.replaceFragment(historyFragment);
    }

    @Override
    public void onAnalyticsClicked() {
        mView.replaceFragment(analyticsFragment);
    }

    @Override
    public void initFirstFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mView.replaceFragment(currencySelectFragment);
        } else {
            Log.d(TAG, "restoreState");
        }
    }
}
