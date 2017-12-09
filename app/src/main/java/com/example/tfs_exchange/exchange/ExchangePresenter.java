package com.example.tfs_exchange.exchange;

import android.os.Bundle;
import android.util.Log;

import com.example.tfs_exchange.api.FixerApiHelper;
import com.example.tfs_exchange.model.Exchange;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pusya on 30.11.17.
 */

public class ExchangePresenter implements ExchangeContract.Presenter {

    private static final String TAG = "ExchangePresenter";

    //MVP
    private ExchangeContract.View mView;
    private ExchangeContract.Repository mRepository;

    //Примитивы и списки
    private long time;
    private String currencyFrom, currencyTo;
    private double amountFrom;
    private double rate;

    //Rx
    private Disposable rateSubscription;

    //Формат даты
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy '\n' HH:mm:ss");

    //Конструктор
    public ExchangePresenter(ExchangeContract.View mView) {
        this.mView = mView;
        this.mRepository = new ExchangeRepository();
    }

    //Получаем курс и передаем его view на отрисовку
    @Override
    public void subscribeRate(String currencyFrom, String currencyTo) {
        time = new Date().getTime();
        rateSubscription =
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

    //Отписка
    @Override
    public void unsubscribeRate() {
        if (rateSubscription != null) rateSubscription.dispose();
        Log.d(TAG, "unsubscribe from rate");
    }

    //Достаём из бандла имена валют от предыдущего фрагмента и получаем курс
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

    //Нажата кнопка "Обменять"
    //Проверяем, сколько прошло времени
    //Если меньше 5 мин - пишем в базу
    //Если больше - получаем обновленный курс и предлагаем записатьа
    @Override
    public void onExchange() {
        long now = new Date().getTime();
        if (now - time < 5*60*1000) {
            Log.d(TAG, "time is Ok");
            Date time = new Date();
            long millis = time.getTime()/1000;
            String[] dateAndTime = dateFormat.format(time).split("\n");
            Exchange exchange = new Exchange(currencyFrom, currencyTo, mView.getAmountFrom(), mView.getAmountTo());
            exchange.setDate(dateAndTime[0]);
            exchange.setTime(dateAndTime[1]);
            exchange.setMillis(time.getTime()/1000);
            mRepository.setExchangeToDB(exchange);
        } else {
            try {
                amountFrom = mView.getAmountFrom();
                time = now;
                rateSubscription =
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
