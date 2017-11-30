package com.example.tfs_exchange.currency_select;

import android.util.Log;

import com.example.tfs_exchange.model.Currency;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by pusya on 30.11.17.
 */

public class CurrencySelectPresenter implements CurrencyContract.Presenter {

    private static final String TAG = "CurrencySelectPresenter";
    private final CurrencyContract.Repository mRepository;
    private final CurrencyContract.View mView;

    private List<Currency> currencies;


    public CurrencySelectPresenter(CurrencyContract.View mView) {
        this.mView = mView;
        mRepository = new CurrencyRepository();
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
            mView.setAdapter(currencies);
            mView.setCurrencies(currencies);
        }
    }

    @Override
    public void setFaveToDb(Currency currency) {
        mRepository.setFaveToDB(currency);
    }

    @Override
    public void setTime(Currency currency) {
        mRepository.setTimeToDB(currency);
    }

    @Override
    public String getCurrencyForExchange(Currency selectedCurrency) {
        for (Currency currency : currencies) {
            if (currency.isFavorite() && !currency.getName().equals(selectedCurrency.getName())) {
                return currency.getName();
            }
            else if (!currency.isFavorite()) {
                break;
            }
        }
        if (selectedCurrency.getName().equals("USD")) {
            return "RUB";
        }
        return "USD";
    }
}
