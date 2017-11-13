package com.example.tfs_exchange.db;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tfs_exchange.model.Exchange;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pusya on 10.11.17.
 * Загрузка истории обменов из БД
 */

public class AsyncExchangeDBLoader extends AsyncTaskLoader<List<Exchange>> {

    private static final String TABLE_EXCHANGE_NAME = "exchange_name";
    private static final String EXCHANGE_BASE = "exchange_base";
    private static final String EXCHANGE_SYMBOLS = "exchange_symbols";
    private static final String EXCHANGE_BASE_AMOUNT = "exchange_base_amount";
    private static final String EXCHANGE_SYMBOLS_AMOUNT = "exchange_symbols_amount";
    private static final String EXCHANGE_RATE = "exchange_rate";
    private static final String EXCHANGE_DATE = "exchange_date";

    private DBHelper dbHelper;
    private Exchange exchange;
    private List<Exchange> exchanges;
    private SQLiteDatabase db;
    private final static String TAG = "AsyncExchangeLoader";

    public AsyncExchangeDBLoader(Context context) {
        super(context);
        Log.d(TAG, "create AsyncLoader");
    }

    @Override
    public List<Exchange> loadInBackground() {
        exchanges = new ArrayList<Exchange>();
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getReadableDatabase();

        //Создаем курсор
        Cursor cursor = db.query(TABLE_EXCHANGE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            //Находим индексы колонок
            int baseColId = cursor.getColumnIndex(EXCHANGE_BASE);
            int symbolsColId = cursor.getColumnIndex(EXCHANGE_SYMBOLS);
            int amountFromColId = cursor.getColumnIndex(EXCHANGE_BASE_AMOUNT);
            int amountToColId = cursor.getColumnIndex(EXCHANGE_SYMBOLS_AMOUNT);
            int rateColId = cursor.getColumnIndex(EXCHANGE_RATE);
            int dateColId  = cursor.getColumnIndex(EXCHANGE_DATE);

            do {
                String base = cursor.getString(baseColId);
                String symbols = cursor.getString(symbolsColId);
                double amountFrom = cursor.getDouble(amountFromColId);
                double amountTo = cursor.getDouble(amountToColId);
                double rate = cursor.getDouble(rateColId);
                String date = cursor.getString(dateColId);
                exchanges.add(new Exchange(base, symbols, amountFrom, amountTo, rate, date));

            } while (cursor.moveToNext());

        }
        return exchanges;
    }
}
