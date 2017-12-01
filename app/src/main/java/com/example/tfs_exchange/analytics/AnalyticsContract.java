package com.example.tfs_exchange.analytics;

import com.example.tfs_exchange.model.Currency;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by pusya on 01.12.17.
 */

public interface AnalyticsContract {
    interface Presenter {
        void getCurrencies();
        void getRates();
    }

    interface View {
        void setAdapter(List<Currency> currencies);
        void plotGraph(ArrayList<Float> list);

    }

    interface Repository {
        Observable<List<Currency>> loadCurrencies();
        Single<ArrayList<Float>> loadRates(int days, String currencyName);
    }
}
