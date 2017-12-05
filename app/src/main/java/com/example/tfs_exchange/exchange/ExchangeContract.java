package com.example.tfs_exchange.exchange;

import android.os.Bundle;

import com.example.tfs_exchange.model.Exchange;

/**
 * Created by pusya on 30.11.17.
 */

public interface ExchangeContract {
    interface View {
        void activateRate(double rate);
        void activateRate(double rate, double amountFrom);
        void disactivateRate();
        void setCurrencies(String currencyFrom, String currencyTo);
        void setRate(double rate);
        Exchange getExchange();
        void showDialog(String message);
        String getAmountFrom();
        String getAmountTo();
    }

    interface Presenter {
        void subscribeRate(String currencyFrom, String currencyTo);
        void unsubscribeRate();
        void getCurrenciesAndRate(Bundle bundle);
        void sendExchange();
    }

    interface Repository {
        void setExchangeToDB(Exchange exchange);
    }
}
