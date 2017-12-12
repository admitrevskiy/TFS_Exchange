package com.example.tfs_exchange.exchange;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;

import com.example.tfs_exchange.model.Exchange;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.disposables.Disposable;


/**
 * Created by pusya on 30.11.17.
 */

public class ExchangePresenter implements ExchangeContract.Presenter {

    private static final String TAG = "ExchangePresenter";

    //MVP
    private ExchangeContract.View mView;
    private ExchangeContract.Repository mRepository;

    //Примитивы b флаги
    private long time, checkTime;
    private String currencyFrom, currencyTo;
    private double amountFrom;
    private double rate;
    private boolean refresh;


    //Rx
    private Disposable rateSubscription;

    //Формат даты
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy '\n' HH:mm:ss");

    //Конструктор
    public ExchangePresenter(ExchangeContract.View mView) {
        this.mView = mView;
        this.mRepository = new ExchangeRepository();
        checkTime = 5*60*1000;
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
                        if (!refresh) {
                            mView.activateRate(rate);
                            refresh = true;
                        } else {
                            Log.d(TAG, "Rate was refreshed");
                            time = new Date().getTime();
                        }
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
    public void onDetach() {
        if (rateSubscription != null) rateSubscription.dispose();
        Log.d(TAG, "unsubscribe from rate");
    }

    //Достаём из бандла имена валют от предыдущего фрагмента и получаем курс
    @Override
    public void getCurrenciesAndRate(Bundle bundle) {
        if (bundle!= null) {
            try {
                currencyFrom = bundle.getStringArray("currencies")[0];
                currencyTo = bundle.getStringArray("currencies")[1];
                mView.setCurrencies(currencyFrom, currencyTo);
                subscribeRate(currencyFrom, currencyTo);
            } catch (NullPointerException e) {
                Log.d(TAG, "problems with bundle: " + e.getMessage());
            }
        } else {
            Log.d(TAG, "problems with bundle");
        }
    }

    //Нажата кнопка "Обменять"
    //Проверяем, сколько прошло времени
    //Если меньше 5 мин - пишем в базу
    //Если больше - получаем обновленный курс и предлагаем записатьа
    @Override
    public void onExchange() {
        long now = new Date().getTime();
        if (checkTime(now, time)) {
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

    protected boolean checkTime(long now, long time) {
        if (now - time < checkTime) {
            Log.d(TAG, "time is Ok");
            return true;
        } else {
            Log.d(TAG, "time is not Ok, refresh rate");
            return false;
        }
    }

    @Override
    public void onAmountFromEdit(Editable s, View view) {
        if (!s.toString().equals("") && view.hasFocus()) {
            if (!checkTime(new Date().getTime(), time)) {
                subscribeRate(currencyFrom, currencyTo);
            }
            mView.setCurrencyAmountToEdit(String.format( "%.4f", (Double.parseDouble(s.toString()) * rate)));
        }

    }

    @Override
    public void onAmountToEdit(Editable s, View view) {
        if (!s.toString().equals("") && view.hasFocus()) {
            if (!checkTime(new Date().getTime(), time)) {
                subscribeRate(currencyFrom, currencyTo);
            }
            mView.setCurrencyAmountFromEdit(String.format( "%.4f", (Double.parseDouble(s.toString()) / rate)));
        }
    }
}
