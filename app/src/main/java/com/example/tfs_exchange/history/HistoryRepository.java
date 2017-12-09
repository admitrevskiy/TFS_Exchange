package com.example.tfs_exchange.history;

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
import com.example.tfs_exchange.model.Exchange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pusya on 30.11.17.
 * Здесь использую чистый SQLite без ORM для понимания происходящего
 */

public class HistoryRepository implements HistoryContract.Repository {

    //Тэг
    private final static String TAG = "HistoryRepository";

    //Шаблоны для сообщений
    private final static String dateMessage = "date period: ";
    private final static String currencyMessage = "with currencies: ";

    //Фильтр
    private int periodFilter;
    private Set<String> currencyFilter;
    private long dateFromMillis, dateToMillis;

    //Ресурсы нужны для загрузки shared preferences
    private SharedPreferences sharedPrefs;
    private Resources resources = ExchangerApp.getAppResources();

    //Синглтон
    private DBHelper dbHelper = DBHelper.getInstance();

    //Строки для работы с базой
    private static final String TABLE_EXCHANGE_NAME = "exchange_name";
    private static final String EXCHANGE_BASE = "exchange_base";
    private static final String EXCHANGE_SYMBOLS = "exchange_symbols";
    private static final String EXCHANGE_BASE_AMOUNT = "exchange_base_amount";
    private static final String EXCHANGE_SYMBOLS_AMOUNT = "exchange_symbols_amount";
    private static final String EXCHANGE_RATE = "exchange_rate";
    private static final String EXCHANGE_DATE = "exchange_date";
    private static final String EXCHANGE_TIME = "exchange_time";
    private static final String EXCHANGE_MILLIS = "exchange_millis";
    private static final String GET_ALL_HISTORY = "SELECT * FROM " + TABLE_EXCHANGE_NAME + " ORDER BY " + EXCHANGE_MILLIS + " DESC";


    //Получаем shared preferences, чтобы понять, что загружать из базы
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
            dateFromMillis = sharedPrefs.getLong(resources.getString(R.string.saved_date_from), 0);
            Log.d(TAG, "DateFrom: " + dateFromMillis);
        }
        if (sharedPrefs.contains(resources.getString((R.string.saved_date_to)))) {
            dateToMillis = sharedPrefs.getLong(resources.getString(R.string.saved_date_to), 0);
            Log.d(TAG, "DateTo: " + dateToMillis);
        }

        Log.d(TAG, "Period ID: " + String.valueOf(periodFilter));
    }

    //Загружаем из базы в соответствии с полученными shared preferences
    @Override
    public Observable<List<Exchange>> loadHistory() {

        //Даты нужны для загрузки истории за неделю или за месяц
        Date now = new Date (System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        //Получаем shared preferences
        getSharedPreferences();

        if (periodFilter == 0) {
            Log.d(TAG, dateMessage + "not selected");
            if (currencyFilter == null || currencyFilter.size() == 0 && (dateFromMillis != 0 && dateToMillis != 0)) {

                return Observable
                        .just(loadAllHistory())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            } else {
                Log.d(TAG, currencyMessage + currencyFilter.toString());
                return Observable
                        .just(loadSortedExchangeHistory(currencyFilter))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        } else if (periodFilter == 3) {
            Log.d(TAG, dateMessage + "custom: dates:  " + dateFromMillis + " " + dateToMillis);
            if (currencyFilter == null || currencyFilter.size() == 0 && (dateFromMillis != 0 && dateToMillis != 0)) {
                return Observable
                        .just(loadSortedExchangeHistory(dateFromMillis, dateToMillis))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            } else {
                Log.d(TAG, currencyMessage + currencyFilter.toString());
                return Observable
                        .just(loadSortedExchangeHistory(currencyFilter, dateFromMillis, dateToMillis))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        } else if (periodFilter == 1) {
            calendar.add(Calendar.DATE, -7);
            Log.d(TAG, dateMessage + "week");
            if (currencyFilter == null || currencyFilter.size() == 0) {
                return Observable
                        .just(loadSortedExchangeHistory(calendar.getTimeInMillis()/1000, System.currentTimeMillis()/1000))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            } else {
                Log.d(TAG, currencyMessage + currencyFilter.toString());
                return Observable
                        .just(loadSortedExchangeHistory(currencyFilter, calendar.getTimeInMillis()/1000, System.currentTimeMillis()/1000))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        } else {
            calendar.add(Calendar.MONTH, -1);
            Log.d(TAG, dateMessage + "month");
            if (currencyFilter == null || currencyFilter.size() == 0) {
                return Observable
                        .just(loadSortedExchangeHistory(calendar.getTimeInMillis()/1000, System.currentTimeMillis()/1000))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            } else {
                Log.d(TAG, currencyMessage + currencyFilter.toString());
                return Observable
                        .just(loadSortedExchangeHistory(currencyFilter, calendar.getTimeInMillis()/1000, System.currentTimeMillis()/1000))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }
    }

    //Строим SQL команду для получения из базы с фильтрацией по валютам
    private String getExchangeHistory(Set<String> currencies) {
        String SQLQuery = "";
        for (String currency: currencies) {
            SQLQuery += EXCHANGE_BASE + " = '" + currency + "' OR " + EXCHANGE_SYMBOLS + " = '" + currency + "' OR ";
        }
        return "SELECT * FROM " + TABLE_EXCHANGE_NAME + " WHERE " + SQLQuery.substring(0, SQLQuery.length() - 3);
    }

    //Строим SQL команду для получения из базы с фильтрацией по датам
    private String getExchangeHistory(long dateFrom, long dateTo) {
        String SQLQuery = "";
        return "SELECT * FROM " + TABLE_EXCHANGE_NAME + " WHERE "
                + EXCHANGE_MILLIS + " BETWEEN '" + dateFrom + "' AND '" + dateTo + "'";
    }

    //Строим SQL команду для получения из базы с фильтрацией по валютам и датам
    private String getExchangeHistory(Set<String> currencies, long dateFrom, long dateTo) {
        String SQLQuery = "";
        for (String currency: currencies) {
            SQLQuery += EXCHANGE_BASE + " = '" + currency + "' OR " + EXCHANGE_SYMBOLS + " = '" + currency + "' OR ";
        }
        return "SELECT * FROM " + TABLE_EXCHANGE_NAME + " WHERE ("
                + SQLQuery.substring(0, SQLQuery.length() - 4) + ") AND  "
                + EXCHANGE_MILLIS + " BETWEEN '" + dateFrom + "' AND '" + dateTo + "'";
    }

    //Загрузка всей истории
    private List<Exchange> loadAllHistory() {
        List<Exchange> exchanges = new ArrayList<>();
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();) {
            try (Cursor cursor = db.rawQuery(GET_ALL_HISTORY, null)) {
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    do {
                        cursorWork(cursor, exchanges);
                    } while (cursor.moveToNext());
                }
            }
        }
        return exchanges;
    }

    //Загрузка истории с сортировкой по валютам
    private List<Exchange> loadSortedExchangeHistory(Set<String> currencies) {
        List<Exchange> exchanges = new ArrayList<>();
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery(getExchangeHistory(currencies), null)) {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    cursorWork(cursor, exchanges);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }
        return exchanges;
    }

    //Загрузка истории с сортировкой по датам
    private List<Exchange> loadSortedExchangeHistory(long dateFrom, long dateTo) {
        List<Exchange> exchanges = new ArrayList<>();
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery(getExchangeHistory(dateFrom, dateTo), null)) {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    cursorWork(cursor, exchanges);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }
        return exchanges;
    }

    //Загрузка истории с сортировкой по валютам и датам
    private List<Exchange> loadSortedExchangeHistory(Set<String> currencies, long dateFrom, long dateTo) {
        List<Exchange> exchanges = new ArrayList<>();
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery(getExchangeHistory(currencies, dateFrom, dateTo), null)) {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    cursorWork(cursor, exchanges);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }
        return exchanges;
    }

    //Вынес работу с курсором в отдельный метод, чтобы не писать много раз
    private void cursorWork(Cursor cursor, List<Exchange> exchanges) {
        String currencyFrom = cursor.getString(cursor.getColumnIndex(EXCHANGE_BASE));
        String currencyTo = cursor.getString(cursor.getColumnIndex(EXCHANGE_SYMBOLS));
        double amountFrom = cursor.getDouble(cursor.getColumnIndex(EXCHANGE_BASE_AMOUNT));
        double amountTo = cursor.getDouble(cursor.getColumnIndex(EXCHANGE_SYMBOLS_AMOUNT));
        String date = cursor.getString(cursor.getColumnIndex(EXCHANGE_DATE));
        String time = cursor.getString(cursor.getColumnIndex(EXCHANGE_TIME));
        long millis = cursor.getLong(cursor.getColumnIndex(EXCHANGE_MILLIS));
        exchanges.add(new Exchange(currencyFrom, currencyTo, amountFrom, amountTo, date, time, millis));
        Log.d(TAG, currencyFrom + " " + currencyTo + " " + date);
    }

}
