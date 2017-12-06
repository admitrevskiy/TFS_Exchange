package com.example.tfs_exchange.exchange;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.tfs_exchange.api.ApiResponse;
import com.example.tfs_exchange.api.FixerApiHelper;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.model.Exchange;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pusya on 30.11.17.
 * Здесь использую чистый SQLite без ORM для понимания происходящего
 */

public class ExchangeRepository implements ExchangeContract.Repository {

    //Тэг
    private static final String TAG = "ExchangeRepository";

     //Работа с базой
    private DBHelper dbHelper = DBHelper.getInstance();
    private ContentValues cv;

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

     //Записываем обмен в базу
    @Override
    public void setExchangeToDB(Exchange exchange) {
        try(SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            cv = new ContentValues();
            cv.put(EXCHANGE_BASE, exchange.getCurrencyFrom());
            cv.put(EXCHANGE_BASE_AMOUNT, exchange.getAmountFrom());
            cv.put(EXCHANGE_SYMBOLS, exchange.getCurrencyTo());
            cv.put(EXCHANGE_SYMBOLS_AMOUNT, exchange.getAmountTo());
            cv.put(EXCHANGE_RATE, exchange.getRate());
            cv.put(EXCHANGE_DATE, exchange.getDate());
            cv.put(EXCHANGE_TIME, exchange.getTime());
            cv.put(EXCHANGE_MILLIS, exchange.getMillis());
            db.insert(TABLE_EXCHANGE_NAME, null, cv);
            Log.d(TAG, "exchange was saved:" + exchange.toString());
        } catch (SQLException e) {
            Log.d(TAG, "something's going wrong with saving to db: " + e.getMessage());
        }
    }

    //Загружаем курс с сервера
    @Override
    public Single<ApiResponse> loadRate(String currencyFrom, String currencyTo) {
        return new FixerApiHelper()
                .createApi()
                .latest(currencyFrom, currencyTo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
