package com.example.tfs_exchange.exchange;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tfs_exchange.ExchangerApp;
import com.example.tfs_exchange.api.ApiResponse;
import com.example.tfs_exchange.api.FixerApiHelper;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.model.Exchange;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pusya on 30.11.17.
 */

public class ExchangeRepository implements ExchangeContract.Repository {

    private static final String TAG = "ExchangeRepository";

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private ContentValues cv;

    @Override
    public void setExchangeToDB(Exchange exchange) {
        dbHelper = DBHelper.getInstance();
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
        cv.put("EXCHANGE_BASE", exchange.getCurrencyFrom());
        cv.put("EXCHANGE_BASE_AMOUNT", exchange.getAmountFrom());
        cv.put("EXCHANGE_SYMBOLS", exchange.getCurrencyTo());
        cv.put("EXCHANGE_SYMBOLS_AMOUNT", exchange.getAmountTo());
        cv.put("EXCHANGE_RATE", exchange.getRate());
        cv.put("EXCHANGE_DATE", exchange.getDate());
        cv.put("EXCHANGE_TIME", exchange.getTime());
        cv.put("EXCHANGE_MILLIS", exchange.getMillis());
        db.insert("exchange_name", null, cv);
        db.close();
        Log.d(TAG, "exchange was saved:" + exchange.toString());
    }

    @Override
    public Single<ApiResponse> loadRate(String currencyFrom, String currencyTo) {
        return new FixerApiHelper()
                .createApi()
                .latest(currencyFrom, currencyTo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
