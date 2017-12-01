package com.example.tfs_exchange.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.example.tfs_exchange.ExchangerApp;
import com.example.tfs_exchange.R;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.model.Exchange;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pusya on 30.11.17.
 */

public class HistoryRepository implements HistoryContract.Repository {

    private final static String TAG = "HistoryRepository";
    private final static String dateMessage = "date period: ";
    private final static String currencyMesage = "with currencies: ";
    private int periodFilter;
    private Set<String> currencyFilter;
    private String dateFromFilter, dateToFilter;
    private long dateFromMillis, dateToMillis;
    private Resources resources = ExchangerApp.getAppResources();
    private DBHelper dbHelper = DBHelper.getInstance();

    private SharedPreferences sharedPrefs;

    private void getSharedPreferences() {
        sharedPrefs = ExchangerApp.getContext().getSharedPreferences(resources.getString(R.string.preference_file), Context.MODE_PRIVATE);
        if (sharedPrefs.contains(resources.getString(R.string.period_id))) {
            periodFilter = sharedPrefs.getInt(resources.getString(R.string.period_id), 0);
            Log.d(TAG, "Period ID: " + String.valueOf(periodFilter));
        }
        if (sharedPrefs.contains(resources.getString(R.string.currencies))) {
            currencyFilter = sharedPrefs.getStringSet(resources.getString(R.string.currencies), new HashSet<>());
            Log.d(TAG, currencyFilter.toString());
        }
        if (sharedPrefs.contains(resources.getString((R.string.saved_date_from)))) {
            //dateFromFilter = sharedPrefs.getString(resources.getString(R.string.saved_date_from), "");
            dateFromMillis = sharedPrefs.getLong(resources.getString(R.string.saved_date_from), 0);
            Log.d(TAG, "DateFrom: " + dateFromFilter);
        }
        if (sharedPrefs.contains(resources.getString((R.string.saved_date_to)))) {
            //dateToFilter = sharedPrefs.getString(resources.getString(R.string.saved_date_to), "");
            dateToMillis = sharedPrefs.getLong(resources.getString(R.string.saved_date_to), 0);
            Log.d(TAG, "DateTo: " + dateToFilter);
        }

        Log.d(TAG, "Period ID: " + String.valueOf(periodFilter));
    }

    @Override
    public Observable<List<Exchange>> loadHistory() {
        getSharedPreferences();
        if (periodFilter == 0) {
            Log.d(TAG, dateMessage + "not selected");
            if (currencyFilter == null || currencyFilter.size() == 0 && (dateFromFilter != null && dateToFilter != null)) {

                return Observable
                        .just(dbHelper.loadAllHistory())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            } else {
                Log.d(TAG, currencyMesage + currencyFilter.toString());
                return Observable
                        .just(dbHelper.getSortedExchangeHistory(currencyFilter))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        } else if (periodFilter == 3) {
            Log.d(TAG, dateMessage + "custom");
            if (currencyFilter == null || currencyFilter.size() == 0 && (dateFromFilter != null && dateToFilter != null)) {
                Log.d(TAG, "dates:  " + dateFromMillis + " " + dateToMillis);
                return Observable
                        .just(dbHelper.getSortedExchangeHistory(dateFromMillis, dateToMillis))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            } else {
                Log.d(TAG, currencyMesage + currencyFilter.toString());
                return Observable
                        .just(dbHelper.getSortedExchangeHistory(currencyFilter, dateFromMillis, dateToMillis))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        } else if (periodFilter == 1) {
            Log.d(TAG, dateMessage + "week");
            if (currencyFilter == null || currencyFilter.size() == 0) {
                return Observable
                        .just(dbHelper.getSortedExchangeHistory(dateFromMillis, dateToMillis))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            } else {
                Log.d(TAG, currencyMesage + currencyFilter.toString());
                return Observable
                        .just(dbHelper.getSortedExchangeHistory(currencyFilter, dateFromMillis, dateToMillis))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        } else {
            Log.d(TAG, dateMessage + "month");
            if (currencyFilter == null || currencyFilter.size() == 0) {
                return Observable
                        .just(dbHelper.getSortedExchangeHistory(dateFromMillis, dateToMillis))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            } else {
                Log.d(TAG, currencyMesage + currencyFilter.toString());
                return Observable
                        .just(dbHelper.getSortedExchangeHistory(currencyFilter, dateFromMillis, dateToMillis))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }
    }


}
