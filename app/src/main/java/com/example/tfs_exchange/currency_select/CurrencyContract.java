package com.example.tfs_exchange.currency_select;

import com.example.tfs_exchange.model.Currency;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by pusya on 30.11.17.
 */

public interface CurrencyContract {
    interface View {
        void setAdapter(List<Currency> currencies);
        void setCurrencies(List<Currency> currencies);
        void replaceByExchangeFragment(String currencyFrom, String currencyTo);
    }

    interface Presenter {
        void getCurrencies();
        void showCurrencies(List<Currency> currencies);
        //String getCurrencyForExchange(Currency selectedCurrency);
        void onFavoriteChanged(Currency currency);
        //void onTimeChanged(Currency currency);
        void onCurrencyLongClicked(Currency currency);
        void onCurrencyClicked(Currency currency);
    }

    interface Repository {
        Observable<List<Currency>> loadCurrencies();
        void setFaveToDB(Currency currency, int fave);
        void setTimeToDB(Currency currency, long time);
    }
}
