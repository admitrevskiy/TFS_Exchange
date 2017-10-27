package com.example.tfs_exchange;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.tfs_exchange.Currency;
import com.example.tfs_exchange.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pusya on 23.10.17.
 */

public class AsyncDBLoader extends AsyncTaskLoader<List<Currency>> {


    private DBHelper dbHelper;

    public static final String TAG = "AsyncLoader";

    public AsyncDBLoader(Context context) {
        super(context);
        Log.d(TAG, hashCode() + " create AsyncLoader");
    }

    @Override
    public List<Currency> loadInBackground() {
         List<Currency> currencies = new ArrayList<Currency>();

        //подключаем Data Base Helper, получаем из него БД для чтения
        dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Создаем курсор
        Cursor cursor = db.query("currency_name", null, null, null, null, null, null);

        //Если БД не пустая
        if (cursor.moveToFirst()) {
            //Находим индексы колонок
            int baseColId = cursor.getColumnIndex("currency_base");
            int lastUsedColId = cursor.getColumnIndex("last_used");
            int favoriteColId = cursor.getColumnIndex("favorite");

            do {
                //Создаем объект типа Currency и присваиваем ему значения из БД

                //String name = cursor.getString(baseColId);

                Currency currency = new Currency();
                currency.setName(cursor.getString(baseColId));
                currency.setLastUse(cursor.getInt(lastUsedColId));
                int favorite = cursor.getInt(favoriteColId);

                //В SQLite нет типа boolean, поэтому НЕ избранные валюты имеют в колонке favorite 0, а избранные 1
                if (favorite == 0)
                {
                    currencies.add(currency);
                    currency.setFavorite(false);
                } else {
                    currencies.add(0, currency);
                    currency.setFavorite(true);
                }

                //Добавляем валюту в List с валютами

            } while (cursor.moveToNext());
        }


        //Закрываем БД
        db.close();

        return currencies;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d(TAG, hashCode() + " onStartLoading");
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        Log.d(TAG, hashCode() + " onStopLoading");
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
        Log.d(TAG, hashCode() + " onAbandon");
    }

    @Override
    protected void onReset() {
        super.onReset();
        Log.d(TAG, hashCode() + " onReset");
    }


}