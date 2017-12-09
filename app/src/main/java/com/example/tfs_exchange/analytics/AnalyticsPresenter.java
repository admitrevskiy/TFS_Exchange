package com.example.tfs_exchange.analytics;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.tfs_exchange.model.Currency;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import io.reactivex.disposables.Disposable;

/**
 * Created by pusya on 01.12.17.
 */

public class AnalyticsPresenter implements AnalyticsContract.Presenter {

    private static final String TAG = "AnalyticsPresenter";

    //Валюты
    private List<com.example.tfs_exchange.model.Currency> currencies;
    private Currency selectedCurrency;


    private int days = 7;

    //MVP
    private AnalyticsContract.View mView;
    private AnalyticsContract.Repository mRepository;


    //Конструктор
    public AnalyticsPresenter(AnalyticsContract.View mView) {
        this.mRepository = new AnalyticsRepository();;
        this.mView = mView;
    }

    //Загружаем валюты из БД и передаём view на отрисовку
    @Override
    public void getCurrencies() {
        currencies = new ArrayList<>();
        Disposable currencySubscription = mRepository.loadCurrencies()
                .subscribe(this::showCurrencies, throwable -> {
                    //getCurrencies();
                    Log.d(TAG, "problems with loading currencies bro");
                });
    }

    //Загружаем историю курсов с сервера и передаём view на отрисовку
    @Override
    public void getRates() {
        Disposable ratesSubscription = mRepository.loadRates(days, selectedCurrency.getName())
                .subscribe(this::showRates, throwable -> {
                    Log.d(TAG, "problems with loading rates bro");
                });
    }

    //Изменился период на view
    @Override
    public void onPeriodChanged() {
        days = mView.getDays();
        Log.d(TAG, days + " days for " + selectedCurrency.getName());
        getRates();
    }

    //Передаём view список валют на отрисовку
    private void showCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
        Log.d(TAG, currencies.toString());
        selectedCurrency = currencies.get(0);
        selectedCurrency.setFilter(true);
        Log.d(TAG, "selected one: " + selectedCurrency.getName());
        getRates();
        mView.setAdapter(currencies);
    }

     //Полученный список курсов переводим в LineGraphSeries (com.jjoe64.graphview.series.LineGraphSeries) и отдаём view на отрисовку
    private void showRates(ArrayList<Float> list) {
        DataPoint[] dataPoints = new DataPoint[list.size()];
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < list.size(); i++) {
            dataPoints[i] = new DataPoint(i+1, (double) list.get(i));
            calendar.add(Calendar.DATE, -1);
            Log.d(TAG, "Plotting " + dataPoints.toString());
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        mView.refreshGraph();
        mView.plotGraph(series);
    }

    //Выбираем избранную валюту для получения данных с сервера
    @Override
    public void setFavorite(Currency currency) {
        for (Currency curr: currencies) {
            if (!curr.isFilter()) {

            } else {
                curr.setFilter(false);
            }
        }
        currency.setFilter(true);
        selectedCurrency = currency;
        Log.d(TAG, "selected currency: " + selectedCurrency.getName());
        mView.refreshCurrencyList(currencies);

    }
}
