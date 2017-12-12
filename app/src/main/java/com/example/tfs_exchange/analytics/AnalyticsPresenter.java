package com.example.tfs_exchange.analytics;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.tfs_exchange.fragments.ToastHelper;
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

    //Количество дней по-умолчанию
    private int days = 7;

    //MVP
    private AnalyticsContract.View mView;
    private AnalyticsContract.Repository mRepository;

    //Rx
    private Disposable ratesSubscription;
    private Disposable currencySubscription;

    //Имя выбранной валюты
    String selectedCurrencyName;

    private ToastHelper toaster = ToastHelper.getInstance();

    //Конструктор
    public AnalyticsPresenter(AnalyticsContract.View mView) {
        this.mRepository = new AnalyticsRepository();;
        this.mView = mView;
    }

    //Загружаем валюты из БД и передаём view на отрисовку
    @Override
    public void getCurrencies() {
        selectedCurrencyName = mRepository.getSelected();
        Log.d(TAG, "loading currencies");
        currencies = new ArrayList<>();
        currencySubscription = mRepository.loadCurrencies()
                .subscribe(this::showCurrencies, throwable -> {
                    //getCurrencies();
                    Log.d(TAG, "problems with loading currencies bro");
                });
    }

    //Загружаем историю курсов с сервера и передаём view на отрисовку
    @Override
    public void getRates() {
        Log.d(TAG, "getRates");
        mView.showProgress();
        Log.d(TAG, "showProgress; days: " + days);
        try {
            ratesSubscription = mRepository.loadRates(days, selectedCurrency.getName())
                    .subscribe(this::showRates, throwable -> {
                        Log.d(TAG, throwable.getMessage());
                        mRepository.refreshApi();
                        mView.handleError();
                        Log.d(TAG, "problems with loading rates bro");
                    });
        } catch (NullPointerException e) {
            Log.d(TAG, e.getMessage());
            //showCurrencies(currencies);
        }
    }

    //Изменился период на view
    @Override
    public void onPeriodChanged() {
        Log.d(TAG, "onPeriodChanged()");
        days = mView.getDays();
        Log.d(TAG, "now days are: " + days);
        try {
            //selectedCurrency = currencies.get(position);
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, e.getMessage());
        }
        if (selectedCurrency != null) {
            Log.d(TAG, days + " days for " + selectedCurrency.getName());
        }

        getRates();
    }

    //Передаём view список валют на отрисовку
    private void showCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
        Log.d(TAG, currencies.toString());
        if (selectedCurrencyName == null) {
            selectedCurrency = currencies.get(0);
        } else {
            for (Currency currency: currencies) {
                if (currency.getName().equals(selectedCurrencyName)) {
                    selectedCurrency = currency;
                }
            }
        }
        selectedCurrency.setFilter(true);
        Log.d(TAG, "selected one: " + selectedCurrency.getName());
        getRates();
        mView.setAdapter(currencies);

        /**
        Log.d(TAG, "showCurrencies()");
        this.currencies = currencies;
        Log.d(TAG, currencies.toString());
        try {
            if (currencies != null) {
                if (selectedCurrencyName != null) {
                    Log.d(TAG, "selectedCurrency successfully loaded!");
                    for (Currency currency : currencies) {
                        if (currency.getName().equals(selectedCurrencyName)) {
                            selectedCurrency = currency;
                            selectedCurrency.setFilter(true);
                        }
                    }
                } else {
                    selectedCurrency = currencies.get(0);
                    selectedCurrency.setFilter(true);
                }
                getRates();
                mView.setAdapter(currencies);
            }
            } catch(IndexOutOfBoundsException e){
                Log.d(TAG, e.getMessage());
            }
         **/
    }


     //Полученный список курсов переводим в LineGraphSeries (com.jjoe64.graphview.series.LineGraphSeries) и отдаём view на отрисовку
    private void showRates(ArrayList<Float> list) {
        Log.d(TAG, "showRates()");
        DataPoint[] dataPoints = new DataPoint[list.size()];
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < list.size(); i++) {
            dataPoints[i] = new DataPoint(i+1, (double) list.get(i));
            calendar.add(Calendar.DATE, -1);
            Log.d(TAG, "Plotting " + dataPoints.toString());
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        mView.refreshGraph();
        mView.plotGraph(series, selectedCurrency.getName() + "/EUR");
        mView.hideProgress();
    }

    //Выбираем избранную валюту для получения данных с сервера
    @Override
    public void setFavorite(Currency currency) {
        Log.d(TAG, "setFavorite()");
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
        mRepository.setSelected(currency.getName());
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach()");
        if (ratesSubscription != null) {
            ratesSubscription.dispose();
        }
        if (currencySubscription != null) {
            currencySubscription.dispose();
        }
    }
}
