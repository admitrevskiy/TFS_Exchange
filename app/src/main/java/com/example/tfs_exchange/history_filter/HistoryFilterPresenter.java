package com.example.tfs_exchange.history_filter;

import android.util.Log;

import com.example.tfs_exchange.model.Currency;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by pusya on 30.11.17.
 */

public class HistoryFilterPresenter implements HistoryFilterContract.Presenter {

    private static final String TAG = "HistoryFilterPresenter";

    private HistoryFilterContract.View mView;
    private HistoryFilterContract.Repository mRepository;

    public  HistoryFilterPresenter(HistoryFilterContract.View mView) {
        this.mView = mView;
        this.mRepository = new HistoryFilterRepository();
    }

    private List<Currency> currencies;

    @Override
    public void getCurrencies() {
        currencies = new ArrayList<>();
        Disposable currencySubscription = mRepository.loadCurrencies()
                .subscribe(this::showCurrencies, throwable -> {
                    Log.d(TAG, "problems, bro");
                });
    }

    @Override
    public void showCurrencies(List<Currency> currencies) {
        if (currencies != null) {
            mView.setAdapter(currencies);
            mView.setCurrencies(currencies);
        }
    }

    @Override
    public void getAndSaveSettings(){
        mRepository.saveSettings(mView.getSettings());
    }

}
