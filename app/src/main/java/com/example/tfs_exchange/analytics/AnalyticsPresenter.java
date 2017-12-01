package com.example.tfs_exchange.analytics;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import com.example.tfs_exchange.model.Currency;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by pusya on 01.12.17.
 */

public class AnalyticsPresenter implements AnalyticsContract.Presenter {

    private static final String TAG = "AnalyticsPresenter";
    private List<com.example.tfs_exchange.model.Currency> currencies;

    private AnalyticsContract.View mView;
    private AnalyticsContract.Repository mRepository;

    public AnalyticsPresenter(AnalyticsContract.View mView) {
        this.mRepository = new AnalyticsRepository();;
        this.mView = mView;
    }

    @Override
    public void getCurrencies() {
        currencies = new ArrayList<>();
        Disposable currencySubscription = mRepository.loadCurrencies()
                .subscribe(this::showCurrencies, throwable -> {
                    Log.d(TAG, "problems, bro");
                });
    }

    @Override
    public void getRates() {
        Disposable ratesSubscription = mRepository.loadRates(5, "RUB")
                .subscribe(this::showRates, throwable -> {
                    Log.d(TAG, "problems, bro");
                });
    }

    private void showCurrencies(List<com.example.tfs_exchange.model.Currency> currencies) {
        mView.setAdapter(currencies);
    }

    private void showRates(ArrayList<Float> list) {
        mView.plotGraph(list);
    }
}
