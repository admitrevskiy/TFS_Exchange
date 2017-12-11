package com.example.tfs_exchange.history_filter;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tfs_exchange.ExchangerApp;
import com.example.tfs_exchange.R;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.model.Currency;
import com.example.tfs_exchange.model.Settings;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pusya on 30.11.17.
 */

public class HistoryFilterRepository implements HistoryFilterContract.Repository {

    //Тэг
    private static final String TAG = "HistoryFilterRepository";

    //Синглтон
    private DBHelper dbHelper = DBHelper.getInstance();

    //Строки для работы с БД
    private static final String EXCHANGE_BASE = "exchange_base";
    private static final String EXCHANGE_SYMBOLS = "exchange_symbols";
    private static final String TABLE_CURRENCY_NAME = "currency_name";
    private static final String CURRENCY_BASE = "currency_base";
    private static final String FILTER = "filter";
    private static final String TABLE_EXCHANGE_NAME = "exchange_name";

    private String GET_FILTER =  "SELECT " + CURRENCY_BASE +", " + FILTER + " FROM " + TABLE_CURRENCY_NAME + " WHERE " + CURRENCY_BASE + " IN (SELECT "
            + EXCHANGE_SYMBOLS + " FROM " + TABLE_EXCHANGE_NAME + ") OR " + CURRENCY_BASE + " IN (" + "SELECT "
            + EXCHANGE_BASE + " FROM " + TABLE_EXCHANGE_NAME + ")";

    //Контекст нужен для работы с shared preferences
    //Ресурсы нужны для доступа к строкам из values/strings
    private SharedPreferences sharedPreferences;
    private SharedPreferences timeSharedPreferences;
    private Context context = ExchangerApp.getContext();
    private Resources resources = ExchangerApp.getAppResources();

    private ContentValues cv;

    //Фильтр
    private int periodFilter;
    private long dateFromMillis, dateToMillis;

    //Получаем фильтр для периода времени
    @Override
    public int getPeriodFilter() {
        sharedPreferences = context.getSharedPreferences(resources.getString(R.string.preference_file), Context.MODE_PRIVATE);
        if (sharedPreferences.contains(resources.getString(R.string.period_id))) {
            periodFilter = sharedPreferences.getInt(resources.getString(R.string.period_id), 0);
            Log.d(TAG, "Period ID: " + String.valueOf(periodFilter));
            return periodFilter;
        } else {
            Log.d(TAG, "no period filter");
            return 0;
        }
    }

    //Получаем дату начала и дату конца фильтрации
    @Override
    public long[] getDates() {
        long[] dates = new long[2];
        timeSharedPreferences = context.getSharedPreferences(resources.getString(R.string.preference_time_file), Context.MODE_PRIVATE);
        if (timeSharedPreferences.contains(resources.getString((R.string.saved_date_from)))) {
            dateFromMillis = timeSharedPreferences.getLong(resources.getString(R.string.saved_date_from), 0);
            Log.d(TAG, "DateFrom: " + dateFromMillis);
        }
        if (timeSharedPreferences.contains(resources.getString((R.string.saved_date_to)))) {
            dateToMillis = timeSharedPreferences.getLong(resources.getString(R.string.saved_date_to), 0);
            Log.d(TAG, "DateTo: " + dateToMillis);
        }
        dates[0] = dateFromMillis;
        dates[1] = dateToMillis;
        return dates;
    }

    //Загружаем доступный список валют
    @Override
    public Observable<List<Currency>> loadCurrencies() {
        return Observable
                .just(getExchangedCurrencies())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //Сохраняем настройки, полученные от презентера в shared preferences
    @Override
    public void saveSettings(Settings settings) {
        sharedPreferences = context.getSharedPreferences(resources.getString(R.string.preference_file), Context.MODE_PRIVATE);
        timeSharedPreferences = context.getSharedPreferences(resources.getString(R.string.preference_time_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences.Editor timeEditor = timeSharedPreferences.edit();
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
            timeEditor.clear();
            long dateFrom = settings.getDateFrom();
            long dateTo = settings.getDateTo();
            timeEditor.putLong(resources.getString(R.string.saved_date_from), dateFrom);
            timeEditor.putLong(resources.getString(R.string.saved_date_to), dateTo);
            Log.d(TAG, "saved prefs: date_from " + dateFrom + " date_to " + dateTo);
        }
        editor.apply();
        timeEditor.apply();
    }

    //Сохраняем валюты как необходимые при сортировке
    @Override
    public void setFilterToDB(String currencyName, int filter) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            cv = new ContentValues();
            cv.put(FILTER, filter);
            db.update(TABLE_CURRENCY_NAME, cv, CURRENCY_BASE + " = ?", new String[] {currencyName});
            Log.d(TAG, " " + currencyName + " filter changed" );
        }
    }

    //Список строк для выпадающего списка
    @Override
    public String[] getStrings() {
        return new String[] {resources.getString(R.string.all_time), resources.getString(R.string.week), resources.getString(R.string.month), resources.getString(R.string.custom)};
    }

    //Получаем из БД список валют, с которыми был совершен обмен
    private List<Currency> getExchangedCurrencies() {
        Log.d(TAG, "currencies loading ");
        List<Currency> currencies = new ArrayList<>();
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();) {
            try (Cursor cursor = db.rawQuery(GET_FILTER, null)) {
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    do {
                        Log.d(TAG, cursor.getCount() + " ");
                        String name = cursor.getString(cursor.getColumnIndex(CURRENCY_BASE));
                        int filter = cursor.getInt(cursor.getColumnIndex(FILTER));
                        if (filter == 1) {
                            currencies.add(new Currency(name, true));
                            Log.d(TAG, "currency " + name + "was added to filters with value true");
                        } else {
                            currencies.add(new Currency(name, false));
                            Log.d(TAG, "currency " + name + "was added to filters with value false");
                        }
                        //Log.d(TAG, "currency " + name + "was added to filters with value true");
                    } while (cursor.moveToNext());
                } else {
                    Log.d(TAG, "no cursor for " + GET_FILTER);
                }
            } catch (SQLException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        return currencies;
    }

}
