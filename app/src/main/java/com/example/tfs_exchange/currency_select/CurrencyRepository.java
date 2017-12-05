package com.example.tfs_exchange.currency_select;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tfs_exchange.comparators.FavoriteComparator;
import com.example.tfs_exchange.comparators.LastUsedComparator;
import com.example.tfs_exchange.comparators.LongClickedComparator;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.model.Currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pusya on 30.11.17.
 */

public class CurrencyRepository implements CurrencyContract.Repository {

    private static final String TAG = "CurrencyRepository";
    private ContentValues cv;
    private List<Currency> currencies = new ArrayList<Currency>();

    private static final String TABLE_CURRENCY_NAME = "currency_name";
    private static final String CURRENCY_BASE = "currency_base";
    private static final String LAST_USED = "last_used";

    private DBHelper dbHelper = DBHelper.getInstance();
    private static final String FAVORITE = "favorite";
    private FavoriteComparator faveComp = new FavoriteComparator();
    private LastUsedComparator lastUsedComp = new LastUsedComparator();
    //private LongClickedComparator longClickedComp = new LongClickedComparator();
    private static final String GET_ALL_CURRENCIES = "SELECT * FROM " + TABLE_CURRENCY_NAME + " ORDER BY " + LAST_USED + " DESC";

    @Override
    public Observable<List<Currency>> loadCurrencies() {

        Log.d(TAG, "load currencies");
        return Observable
                .just(loadAll())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void setFaveToDB (Currency currency, int fave) {
        cv = new ContentValues();
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            cv.put(FAVORITE, fave);
            db.update(TABLE_CURRENCY_NAME, cv, CURRENCY_BASE + " = ?", new String[]{currency.getName()});
            Log.d(TAG, " " + currency.getName() + " favorite changed");
            //dbHelper.setFaveToDB(currency);
        }
    }

    @Override
    public void setTimeToDB(Currency currency, long time) {
        cv = new ContentValues();
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            //long lastUse = new Date().getTime();
            //int time = (int)lastUse/1000;
            //currency.setLastUse(lastUse);
            cv.put(LAST_USED, time);
            db.update(TABLE_CURRENCY_NAME, cv, CURRENCY_BASE + " = ?", new String[] {currency.getName()});
            Log.d(TAG, " " + currency.getName() + " lastUsed changed " + time );
        }
        //dbHelper.setTimeToDB(currency);
    }

    private List<Currency> loadAll() {
        List<Currency> currencies = new ArrayList<>();
        Currency currency;
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();) {
            try (Cursor cursor = db.rawQuery(GET_ALL_CURRENCIES, null);) {
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    //Находим индексы колонок
                    int baseColId = cursor.getColumnIndex("currency_base");
                    int lastUsedColId = cursor.getColumnIndex("last_used");
                    int favoriteColId = cursor.getColumnIndex("favorite");

                    do {
                        //Создаем объект типа Currency и присваиваем ему значения из БД
                        currency = new Currency();
                        currency.setName(cursor.getString(baseColId));
                        currency.setLastUse(cursor.getInt(lastUsedColId));
                        int favorite = cursor.getInt(favoriteColId);

                        //В SQLite нет типа boolean, поэтому НЕ избранные валюты имеют в колонке favorite 0, а избранные 1
                        if (favorite == 0) {
                            currencies.add(currency);
                            currency.setFavorite(false);
                            Log.d(TAG, " " + currency.getName() + " was added, last use: " + currency.getLastUse());
                        } else {
                            currencies.add(0, currency);
                            currency.setFavorite(true);
                        }

                        //Добавляем валюту в List с валютами

                    } while (cursor.moveToNext());
                }
                //sortCurrencies();
            }
        }
        return currencies;
    }
}