package com.example.tfs_exchange.analytics;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.example.tfs_exchange.comparators.FavoriteComparator;
import com.example.tfs_exchange.comparators.LastUsedComparator;
import com.example.tfs_exchange.model.Currency;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by pusya on 01.12.17.
 */

public class AnalyticsPresenter implements AnalyticsContract.Presenter {

    private static final String TAG = "AnalyticsPresenter";
    private List<com.example.tfs_exchange.model.Currency> currencies;
    private int days = 7;
    private AnalyticsContract.View mView;
    private AnalyticsContract.Repository mRepository;
    //Компараторы
    private FavoriteComparator faveComp = new FavoriteComparator();
    private LastUsedComparator lastUsedComp = new LastUsedComparator();
    public AnalyticsPresenter(AnalyticsContract.View mView) {
        this.mRepository = new AnalyticsRepository();;
        this.mView = mView;
    }
    private Currency selectedCurrency;

    @Override
    public void getCurrencies() {
        currencies = new ArrayList<>();
        Disposable currencySubscription = mRepository.loadCurrencies()
                .subscribe(this::showCurrencies, throwable -> {
                    //getCurrencies();
                    Log.d(TAG, "problems, bro");
                });
    }

    @Override
    public void getRates(/**int days**/ String currencyName) {
        Disposable ratesSubscription = mRepository.loadRates(days, currencyName)
                .subscribe(this::showRates, throwable -> {
                    Log.d(TAG, "problems, bro");
                });
    }

    @Override
    public void onPeriodChanged() {
        days = mView.getDays();
        //String currencyName = mView.getSelectedCurrency().getName();
        getRates(selectedCurrency.getName());
        //days = mView.getDays();
    }


    private void showCurrencies(List<com.example.tfs_exchange.model.Currency> currencies) {
        Log.d(TAG, currencies.toString());
        selectedCurrency = currencies.get(0);
        getRates(selectedCurrency.getName());
        sortCurrencies(currencies);
        mView.setAdapter(currencies);
    }

    private void showRates(ArrayList<Float> list) {
        DataPoint[] dataPoints = new DataPoint[list.size()];
        //String[] dates  = new String[days];

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

    //Сортируем избранные валюты вверх по списку - сначала по использованиям, потом по избранности
    private void sortCurrencies(List<Currency> currencies) {
        Collections.sort(currencies, lastUsedComp);
        Collections.sort(currencies, faveComp);
        //mView.setAdapter(currencies);
        //mView.setCurrencies(currencies);
        Log.d(TAG, " sortCurrencies");
        Log.d(TAG, currencies.toString());
    }

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
        mView.refreshCurrencyList(currency, currencies);
        //adapter.notifyDataSetChanged();
    }
}
