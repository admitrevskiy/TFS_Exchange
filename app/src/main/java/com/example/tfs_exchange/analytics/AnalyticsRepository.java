package com.example.tfs_exchange.analytics;

import android.util.Log;

import com.example.tfs_exchange.api.FixerApi;
import com.example.tfs_exchange.api.FixerApiHelper;
import com.example.tfs_exchange.comparators.FavoriteComparator;
import com.example.tfs_exchange.comparators.LastUsedComparator;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.model.Currency;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pusya on 01.12.17.
 */

public class AnalyticsRepository implements AnalyticsContract.Repository {

    private static final String TAG = "AnalyticsRepository";

    private LastUsedComparator lastUsedComp = new LastUsedComparator();
    private FavoriteComparator faveComp = new FavoriteComparator();

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private DBHelper dbHelper = DBHelper.getInstance();

    @Override
    public Observable<List<Currency>> loadCurrencies() {
        return Observable
                .just(dbHelper.loadAll())
                .subscribeOn(Schedulers.io())
                .map(dates -> {
                    sortCurrencies(dates);
                    return dates;
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<ArrayList<Float>> loadRates(int days, String currencyName) {
        FixerApi api = new FixerApiHelper().createApi();
        return Flowable.fromArray(generateDates(days))
                .subscribeOn(Schedulers.io())
                .flatMapSingle(date -> (api.getRateByDate(date, currencyName, "EUR")))
                .reduce(new ArrayList<Float>(), (list, rate) -> {
                    list.add((float)rate.getRates().getRate());
                    Log.d(TAG, list.toString());
                    return list;
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void sortCurrencies(List<Currency> currencies) {
        Collections.sort(currencies, lastUsedComp);
        Collections.sort(currencies, faveComp);
        //adapter.notifyDataSetChanged();
        Log.d(TAG, " sortCurrencies");
    }


    private String[] generateDates(int days) {
        String[] dates  = new String[days];

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        for (int i = days-1; i >= 0; i--) {
            dates[i] = format.format(today);
            calendar.add(Calendar.DATE, -1);
            today = calendar.getTime();
            Log.d(TAG, dates[i]);
        }

        return dates;
    }
}