package com.example.tfs_exchange.history_filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.example.tfs_exchange.ExchangerApp;
import com.example.tfs_exchange.R;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.model.Currency;
import com.example.tfs_exchange.model.Settings;

import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pusya on 30.11.17.
 */

public class HistoryFilterRepository implements HistoryFilterContract.Repository {

    private static final String TAG = "HistoryFilterRepository";

    private DBHelper dbHelper = DBHelper.getInstance();
    private SharedPreferences sharedPreferences;
    private Context context = ExchangerApp.getContext();

    Resources resources = ExchangerApp.getAppResources();

    @Override
    public Observable<List<Currency>> loadCurrencies() {
        return Observable
                .just(dbHelper.getFilteredCurrencies())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void saveSettings(Settings settings) {
        sharedPreferences = context.getSharedPreferences(resources.getString(R.string.preference_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        int periodId = settings.getPeriod_id();
        editor.putInt(resources.getString(R.string.period_id), periodId);
        Log.d(TAG, "saved prefs: period_id " + periodId);

        if (settings.getCurrencies() != null) {
            Set<String> currencies = settings.getCurrencies();
            editor.putStringSet(resources.getString(R.string.currencies), currencies);
            Log.d(TAG, "saved prefs: currencies " + currencies.toString());
        }

        if (settings.getDateFrom() != 0 && settings.getDateTo() != 0) {
            long dateFrom = settings.getDateFrom();
            long dateTo = settings.getDateTo();
            editor.putLong(resources.getString(R.string.date_from), dateFrom);
            editor.putLong(resources.getString(R.string.date_to), dateTo);
            Log.d(TAG, "saved prefs: date_from " + dateFrom + " date_to " + dateTo);
        }
        editor.apply();
    }
}
