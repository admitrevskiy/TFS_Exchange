package com.example.tfs_exchange.analytics;

import com.example.tfs_exchange.model.Currency;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
        void getRates(String currencyName);
        void setFavorite(Currency currenc);
        void onPeriodChanged();

    }

    interface View {
        void setAdapter(List<Currency> currencies);
        void plotGraph(LineGraphSeries<DataPoint> series);
        void refreshGraph();
        void refreshCurrencyList(Currency currencyy, List<Currency> currencies);
        void setFavorite(Currency currency);
        Currency getSelectedCurrency();
        int getDays();

    }

    interface Repository {
        Observable<List<Currency>> loadCurrencies();
        Single<ArrayList<Float>> loadRates(int days, String currencyName);
    }
}
