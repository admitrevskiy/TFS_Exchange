package com.example.tfs_exchange.analytics;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    //Компараторы
    private LastUsedComparator lastUsedComp = new LastUsedComparator();
    private FavoriteComparator faveComp = new FavoriteComparator();

    //Формат даты
    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    //Работа с БД
    private DBHelper dbHelper = DBHelper.getInstance();
    private static final String TABLE_CURRENCY_NAME = "currency_name";
    private static final String CURRENCY_BASE = "currency_base";
    private static final String LAST_USED = "last_used";
    private static final String FAVORITE = "favorite";
    private static final String GET_ALL_CURRENCIES = "SELECT * FROM " + TABLE_CURRENCY_NAME + " ORDER BY " + LAST_USED + " DESC, " + FAVORITE;

    FixerApi api = new FixerApiHelper().createApi();

    //Загружаем валюты из БД и сразу сортируем
    @Override
    public Observable<List<Currency>> loadCurrencies() {
        return Observable
                .just(loadAll())
                .subscribeOn(Schedulers.io())
                .map(currencies -> {
                    sortCurrencies(currencies);
                    return currencies;
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    //Загружаем курсы с сервера
    @Override
    public Single<ArrayList<Float>> loadRates(int days, String currencyName) {

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

    @Override
    public void refreshApi() {
        if (api != null) {
            api = null;
            api = new FixerApiHelper().createApi();
        } else {
            api = new FixerApiHelper().createApi();
        }
    }

    //Сортировка перед передачей Presenter'у
    private void sortCurrencies(List<Currency> currencies) {
        Collections.sort(currencies, lastUsedComp);
        Collections.sort(currencies, faveComp);
        //adapter.notifyDataSetChanged();
        Log.d(TAG, " sortCurrencies");
    }

    //Генерируем даты для запросов на сервер
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

    //Загружаем валюты из базы
    public List<Currency> loadAll() {
        List<Currency> currencies = new ArrayList<>();
        Currency currency;
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();) {
            try (Cursor cursor = db.rawQuery(GET_ALL_CURRENCIES, null);) {
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    do {
                        //Создаем объект типа Currency и присваиваем ему значения из БД
                        currency = new Currency();
                        currency.setName(cursor.getString(cursor.getColumnIndex(CURRENCY_BASE)));
                        currency.setLastUse(cursor.getInt(cursor.getColumnIndex(LAST_USED)));
                        int favorite = cursor.getInt(cursor.getColumnIndex(FAVORITE));
                        //В SQLite нет типа boolean, поэтому НЕ избранные валюты имеют в колонке favorite 0, а избранные 1
                        if (favorite == 0) {
                            currencies.add(currency);
                            currency.setFavorite(false);
                            Log.d(TAG, " " + currency.getName() + " was added, last use: " + currency.getLastUse());
                        } else {
                            currencies.add(0, currency);
                            currency.setFavorite(true);
                        }
                    } while (cursor.moveToNext());
                }
            }
        }
        Log.d(TAG, "For " + GET_ALL_CURRENCIES + ": " + currencies.toString());
        return currencies;
    }
}
