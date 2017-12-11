package com.example.tfs_exchange.exchange;

import android.os.Bundle;

import com.example.tfs_exchange.api.ApiResponse;
import com.example.tfs_exchange.model.Exchange;

import io.reactivex.Single;

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
        void showDialog(String message);
        double getAmountFrom();
        double getAmountTo();
    }

    interface Presenter {
        void subscribeRate(String currencyFrom, String currencyTo);
        void onDetach();
        void getCurrenciesAndRate(Bundle bundle);
        void onExchange();
    }

    interface Repository {
        void setExchangeToDB(Exchange exchange);
        Single<ApiResponse> loadRate(String currencyFrom, String currencyTo);
    }
}
