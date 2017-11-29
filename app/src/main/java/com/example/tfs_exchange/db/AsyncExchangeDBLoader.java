package com.example.tfs_exchange.db;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tfs_exchange.model.Exchange;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
    private static final String EXCHANGE_TIME = "exchange_time";
    private static final String EXCHANGE_MILLIS = "exchange_millis";
    private boolean isSortedDate, isSortedCurrencies;
    private String curencyFrom, currencyTo, dateFrom, dateTo;
    private Set<String> currencies;
    private int periodId;
    private long dateFromMillis, dateToMillis;

    private DBHelper dbHelper;
    private Exchange exchange;
    private List<Exchange> exchanges;
    private SQLiteDatabase db;
    private final static String TAG = "AsyncExchangeLoader";

    public AsyncExchangeDBLoader(Context context) {
        super(context);
        Log.d(TAG, "create AsyncLoader");
        isSortedDate = false;
        isSortedCurrencies = false;
    }

    public AsyncExchangeDBLoader(Context context, long dateFrom, long dateTo) {
        super(context);
        this.isSortedDate = true;
        this.isSortedCurrencies = false;
        this.dateFromMillis = dateFrom;
        this.dateToMillis = dateTo;
        Log.d(TAG, "create AsyncLoader with date");
    }

    public AsyncExchangeDBLoader(Context context, Set<String> currencies){
        super(context);
        this.isSortedDate = false;
        this.isSortedCurrencies = true;
        this.currencies = currencies;
        Log.d(TAG, "create sorted AsyncLoader with currencies");
    }

    public AsyncExchangeDBLoader(Context context, Set<String> currencies, long dateFromMillis, long dateToMillis){
        super(context);
        this.isSortedDate = true;
        this.isSortedCurrencies = true;
        this.currencies = currencies;
        this.dateFromMillis = dateFromMillis;
        this.dateToMillis = dateToMillis;

        Log.d(TAG, "create sorted AsyncLoader with dates and currencies");
    }

    @Override
    public List<Exchange> loadInBackground() {
        exchanges = new ArrayList<Exchange>();
        dbHelper = new DBHelper(getContext());
        if (!isSortedDate && !isSortedCurrencies) {
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
                int timeColId  = cursor.getColumnIndex(EXCHANGE_TIME);
                int millisColId = cursor.getColumnIndex(EXCHANGE_MILLIS);

                do {
                    String base = cursor.getString(baseColId);
                    String symbols = cursor.getString(symbolsColId);
                    double amountFrom = cursor.getDouble(amountFromColId);
                    double amountTo = cursor.getDouble(amountToColId);
                    double rate = cursor.getDouble(rateColId);
                    String date = cursor.getString(dateColId);
                    String time = cursor.getString(timeColId);
                    long millis = cursor.getLong(millisColId);
                    Log.d(TAG, time);
                    exchanges.add(new Exchange(base, symbols, amountFrom, amountTo, rate, date, time, millis));

                } while (cursor.moveToNext());

            }
            Log.d(TAG, exchanges.toString());
            return exchanges;
        }
        else if (isSortedDate && !isSortedCurrencies) {
            Log.d(TAG, "DBHelper call with date");
            return dbHelper.getSortedExchangeHistory(dateFromMillis, dateToMillis);
        }
        else if (!isSortedDate && isSortedCurrencies) {
            Log.d(TAG, "DBHelper call with currencies");
            return dbHelper.getSortedExchangeHistory(currencies);
        }
        else {
            Log.d(TAG, "DBHelper call with date and currencies");
            return dbHelper.getSortedExchangeHistory(currencies, dateFromMillis, dateToMillis);
        }


    }
}
