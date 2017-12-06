package com.example.tfs_exchange.history_filter;

import android.util.Log;

import com.example.tfs_exchange.model.Currency;
import com.example.tfs_exchange.model.Settings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.disposables.Disposable;

/**
 * Created by pusya on 30.11.17.
 */

public class HistoryFilterPresenter implements HistoryFilterContract.Presenter {

    private static final String TAG = "HistoryFilterPresenter";

    private HistoryFilterContract.View mView;
    private HistoryFilterContract.Repository mRepository;

    private List<Currency> currencies;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public  HistoryFilterPresenter(HistoryFilterContract.View mView) {
        this.mView = mView;
        this.mRepository = new HistoryFilterRepository();
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
    public void showCurrencies(List<Currency> currencies) {
        if (currencies != null) {
            this.currencies = currencies;
            mView.setAdapter(currencies);
            mView.setCurrencies(currencies);
        }
    }

    @Override
    public void onSaveSettings(){
        mRepository.saveSettings(getSettings());
        mView.popBackStack();
    }

    @Override
    public void onCurrencyClicked(Currency currency) {
        Log.d(TAG, currency.getName() + " was clicked");
        //Меняем избранность валюты
        int filter;
        if (currency.isFilter())
        {
            currency.setFilter(false);
            filter = 0;
        } else {
            currency.setFilter(true);
            filter = 1;
        }
        mRepository.setFilterToDB(currency.getName(), filter);
        mView.setAdapter(currencies);
        mView.setCurrencies(currencies);
    }

    private Settings getSettings() {
        Settings settings = new Settings();
        Set<String> currencyFilter = new HashSet<>();
        for (Currency currency: currencies) {
            if (currency.isFilter()) {
                currencyFilter.add(currency.getName());
            }
        }
        if (currencyFilter.size()>0) {
            settings.setCurrencies(currencyFilter);
        }
        int periodId = mView.getPeriodId();
        settings.setPeriod_id(periodId);
        if (periodId == 3) {
            try {
                /** ПОПРАВИТЬ С CALENDAR**/
                long dateFrom = dateFormat.parse(mView.getDateFrom()).getTime()/1000;
                long dateTo = dateFormat.parse(mView.getDateTo()).getTime()/1000;
                settings.setDateFrom(dateFrom);
                settings.setDateTo(dateTo);
            } catch (ParseException e) {
                Log.d(TAG, "there is something wrong with the dates");
            }
        }
        return settings;
    }

}
