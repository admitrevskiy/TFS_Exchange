package com.example.tfs_exchange.exchange;

import android.os.Bundle;
import android.util.Log;

import com.example.tfs_exchange.api.FixerApiHelper;

import java.util.Date;

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
    private long time;
    private Disposable rateSubscription;
    private String currencyFrom, currencyTo;
    private double amountFrom;

    private double rate;

    public ExchangePresenter(ExchangeContract.View mView) {
        this.mView = mView;
        this.mRepository = new ExchangeRepository();
    }

    /** перенести в REPOSITORY **/

    @Override
    public void subscribeRate(String currencyFrom, String currencyTo) {
        time = new Date().getTime();
        rateSubscription =
                /**new FixerApiHelper()
                .createApi()
                .latest(currencyFrom, currencyTo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                 **/
                mRepository.loadRate(currencyFrom, currencyTo)
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
            currencyFrom = bundle.getStringArray("currencies")[0];
            currencyTo = bundle.getStringArray("currencies")[1];

            mView.setCurrencies(currencyFrom, currencyTo);

            subscribeRate(currencyFrom, currencyTo);
        } else {
            Log.d(TAG, "problems, bro");
        }
    }


    @Override
    public void sendExchange() {
        long now = new Date().getTime();
        if (now - time < 5000) {
            Log.d(TAG, "time is Ok");
            mRepository.setExchangeToDB(mView.getExchange());
        } else {
            try {
                amountFrom = Double.parseDouble(mView.getAmountFrom());
                time = now;
                Disposable newSubscription =
                        /**new FixerApiHelper()
                        .createApi()
                        .latest(currencyFrom, currencyTo)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                         **/
                        mRepository.loadRate(currencyFrom, currencyTo)
                        .subscribe(apiResponse -> {
                            rate = apiResponse.getRates().getRate();
                            if (rate != 0) {
                                mView.activateRate(rate, amountFrom);
                                mView.showDialog(amountFrom + "  -  " + amountFrom*rate);
                            } else {
                                Log.d(TAG, " some problems with loading new rate");
                            }

                        }, throwable -> {
                            Log.d(TAG, " connection problems");
                            mView.disactivateRate();
                        });
                Log.d(TAG, "new Subscribe");
            } catch (Exception e) {
                Log.d(TAG, "trouble with new subscription");
            }

        }

    }
}
