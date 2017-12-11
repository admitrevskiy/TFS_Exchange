package com.example.tfs_exchange.analytics;

import com.example.tfs_exchange.model.Currency;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by pusya on 01.12.17.
 */

public interface AnalyticsContract {
    interface Presenter {
        void getCurrencies();
        void getRates();
        void setFavorite(Currency currenc);
        void onPeriodChanged();
        void onDetach();

    }

    interface View {
        void setAdapter(List<Currency> currencies);
        void plotGraph(LineGraphSeries<DataPoint> series, String label);
        void refreshGraph();
        void refreshCurrencyList(List<Currency> currencies);
        int getDays();
        void showProgress();
        void hideProgress();
        void handleError();

    }

    interface Repository {
        Observable<List<Currency>> loadCurrencies();
        Single<ArrayList<Float>> loadRates(int days, String currencyName);
        void refreshApi();
    }
}
