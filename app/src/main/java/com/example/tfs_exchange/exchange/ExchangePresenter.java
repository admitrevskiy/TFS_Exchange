package com.example.tfs_exchange.exchange;

import android.os.Bundle;
import android.util.Log;

import com.example.tfs_exchange.api.FixerApiHelper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pusya on 30.11.17.
 */

public class ExchangePresenter implements ExchangeContract.Presenter {

    private static final String TAG = "ExchangePresenter";

    private ExchangeContract.View mView;
    private ExchangeContract.Repository mRepository;

    private Disposable rateSubscription;

    private double rate;

    public ExchangePresenter(ExchangeContract.View mView) {
        this.mView = mView;
        this.mRepository = new ExchangeRepository();
    }

    @Override
    public void subscribeRate(String currencyFrom, String currencyTo) {
        rateSubscription = new FixerApiHelper()
                .createApi()
                .latest(currencyFrom, currencyTo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(apiResponse -> {
                    rate = apiResponse.getRates().getRate();
                    if (rate != 0) {
                        mView.activateRate(rate);
                    } else {
                        Log.d(TAG, " some problems with loading rates");
                    }

                }, throwable -> {
                    Log.d(TAG, " connection problems");
                    mView.disactivateRate();
                });
        Log.d(TAG, "Subscribe");
    }

    @Override
    public void unsubscribeRate() {
        if (rateSubscription != null) rateSubscription.dispose();
        Log.d(TAG, "unsubscribe from rate");
    }

    @Override
    public void getCurrenciesAndRate(Bundle bundle) {
        if (bundle!= null) {
            String currencyFrom = bundle.getStringArray("currencies")[0];
            String currencyTo = bundle.getStringArray("currencies")[1];

            mView.setCurrencies(currencyFrom, currencyTo);

            subscribeRate(currencyFrom, currencyTo);
        } else {
            Log.d(TAG, "problems, bro");
        }
    }

    @Override
    public void sendExchange() {
        mRepository.setExchangeToDB(mView.getExchange());
    }
}
