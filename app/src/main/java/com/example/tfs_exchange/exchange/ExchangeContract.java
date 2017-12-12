package com.example.tfs_exchange.exchange;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;

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
        void setCurrencyAmountFromEdit(String text);
        void setCurrencyAmountToEdit(String text);
    }

    interface Presenter {
        void subscribeRate(String currencyFrom, String currencyTo);
        void onDetach();
        void getCurrenciesAndRate(Bundle bundle);
        void onExchange();
        void onAmountFromEdit(Editable s, android.view.View view);
        void onAmountToEdit(Editable s, android.view.View view);
    }

    interface Repository {
        void setExchangeToDB(Exchange exchange);
        Single<ApiResponse> loadRate(String currencyFrom, String currencyTo);
    }
}
