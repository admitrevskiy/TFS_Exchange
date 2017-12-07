package com.example.tfs_exchange.history_filter;

import android.widget.TextView;

import com.example.tfs_exchange.model.Currency;
import com.example.tfs_exchange.model.Settings;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by pusya on 30.11.17.
 */

public interface HistoryFilterContract {
    interface View {
        void setAdapter(List<Currency> currencies);
        void setCurrencies(List<Currency> currencies);
        int getPeriodId();
        String getDateFrom();
        String getDateTo();
        void popBackStack();
        void callDatePicker(TextView textView, int mYear, int mMonth, int mDay);
        void setTimeSettings(int periodId, String dateFrom, String dateTo, String[] periods);
    }

    interface Presenter {
        void getCurrencies();
        void showCurrencies(List<Currency> currencies);
        void onSaveSettings();
        void onCurrencyClicked(Currency currency);
        void onChangeDate(TextView textView);
        void setSettings();
    }

    interface Repository {
        Observable<List<Currency>> loadCurrencies();
        void saveSettings(Settings settings);
        void setFilterToDB(String currencyName, int filter);
        int getPeriodFilter();
        long[] getDates();
        String[] getStrings();
    }
}
