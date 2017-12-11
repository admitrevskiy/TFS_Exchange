package com.example.tfs_exchange.currency_select;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.model.Currency;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pusya on 30.11.17.
 * Здесь использую чистый SQLite без ORM для понимания происходящего
 */

public class CurrencyRepository implements CurrencyContract.Repository {

    //Тэг
    private static final String TAG = "CurrencyRepository";

    //Синглтон
    private DBHelper dbHelper = DBHelper.getInstance();

    //Content values
    private ContentValues cv;

    //Строки для работы с базой
    private static final String TABLE_CURRENCY_NAME = "currency_name";
    private static final String CURRENCY_BASE = "currency_base";
    private static final String LAST_USED = "last_used";
    private static final String FAVORITE = "favorite";

    //SQL команда для загрузки всех валют из базы
    private static final String GET_ALL_CURRENCIES = "SELECT * FROM " + TABLE_CURRENCY_NAME + " ORDER BY " + LAST_USED + " DESC, " + FAVORITE;

    @Override
    public Observable<List<Currency>> loadCurrencies() {

        Log.d(TAG, "load currencies");
        return Observable
                .just(loadAll())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //Записываем избранность в БД
    @Override
    public void setFaveToDB (Currency currency, int fave) {
        cv = new ContentValues();
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            cv.put(FAVORITE, fave);
            db.update(TABLE_CURRENCY_NAME, cv, CURRENCY_BASE + " = ?", new String[]{currency.getName()});
            Log.d(TAG, " " + currency.getName() + " favorite changed");
        }
    }

    //Загрузка всех валют из БД
    private List<Currency> loadAll() {
        List<Currency> currencies = new ArrayList<>();
        Currency currency;
        int faves = 0;
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();) {
            try (Cursor cursor = db.rawQuery(GET_ALL_CURRENCIES, null);) {
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    //Находим индексы колонок
                    int baseColId = cursor.getColumnIndex(CURRENCY_BASE);
                    int lastUsedColId = cursor.getColumnIndex(LAST_USED);
                    int favoriteColId = cursor.getColumnIndex(FAVORITE);

                    do {
                        //Создаем объект типа Currency и присваиваем ему значения из БД
                        currency = new Currency();
                        currency.setName(cursor.getString(baseColId));
                        currency.setLastUse(cursor.getInt(lastUsedColId));
                        int favorite = cursor.getInt(favoriteColId);

                        //В SQLite нет типа boolean, поэтому НЕ избранные валюты имеют в колонке favorite 0, а избранные 1
                        //Добавляем валюту в List с валютами
                        if (favorite == 0) {
                            currencies.add(currency);
                            currency.setFavorite(false);
                            Log.d(TAG, " " + currency.getName() + " was added, last use: " + currency.getLastUse());
                        } else {
                            currencies.add(faves,currency);
                            currency.setFavorite(true);
                            faves++;
                        }
                    } while (cursor.moveToNext());
                }
            }
        }
        Log.d(TAG, "For " + GET_ALL_CURRENCIES + ": " + currencies.toString());
        return currencies;
    }
}
