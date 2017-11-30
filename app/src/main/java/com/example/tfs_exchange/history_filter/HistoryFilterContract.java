package com.example.tfs_exchange.history_filter;

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
        Settings getSettings();
    }

    interface Presenter {
        void getCurrencies();
        void showCurrencies(List<Currency> currencies);
        void getAndSaveSettings();
    }

    interface Repository {
        Observable<List<Currency>> loadCurrencies();
        void saveSettings(Settings settings);
    }
}
