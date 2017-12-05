package com.example.tfs_exchange.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tfs_exchange.ExchangerApp;
import com.example.tfs_exchange.model.Currency;
import com.example.tfs_exchange.model.Exchange;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by pusya on 16.10.17.
 * Читать http://sqlite.org/datatype3.html
 * Заметка от 7.11: мигрировать на Room
 */

//Создаем помощник работы с SQLite на чистом SQLite
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper INSTANCE;

    private static final String TAG = "DBHelper";
    private ContentValues cv;
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd '\n' HH:mm:ss");

    //Назначаем имя базы данных и версию
    private static final String DATA_BASE_NAME = "exchangerDB";
    private static final int DATA_BASE_VERSION = 1;

    private static final String TABLE_EXCHANGE_NAME = "exchange_name";
    private static final String EXCHANGE_BASE = "exchange_base";
    private static final String EXCHANGE_SYMBOLS = "exchange_symbols";
    private static final String EXCHANGE_BASE_AMOUNT = "exchange_base_amount";
    private static final String EXCHANGE_SYMBOLS_AMOUNT = "exchange_symbols_amount";
    private static final String EXCHANGE_RATE = "exchange_rate";
    private static final String EXCHANGE_DATE = "exchange_date";
    private static final String EXCHANGE_TIME = "exchange_time";
    private static final String EXCHANGE_MILLIS = "exchange_millis";

    private static final String TABLE_CURRENCY_NAME = "currency_name";
    private static final String CURRENCY_BASE = "currency_base";
    private static final String LAST_USED = "last_used";
    private static final String FAVORITE = "favorite";
    private static final String FILTER = "filter";

    private static final String TABLE_RATES_NAME = "rates_name";


    private static final String CURRENCY_TABLE = "create table " + TABLE_CURRENCY_NAME + " ("
            + CURRENCY_BASE + " string primary key, "
            + LAST_USED + " integer, "
            + FAVORITE + " integer, "
            + FILTER + " integer "
            + ");";

    private static final String EXCHANGE_TABLE = "create table " + TABLE_EXCHANGE_NAME + " ("
            + EXCHANGE_BASE + " string,  "
            + EXCHANGE_BASE_AMOUNT + " real, "
            + EXCHANGE_SYMBOLS + " string, "
            + EXCHANGE_SYMBOLS_AMOUNT + " real, "
            + EXCHANGE_RATE + " real, "
            + EXCHANGE_DATE + " String, "
            + EXCHANGE_TIME + " String, "
            + EXCHANGE_MILLIS + " integer "
            + ");";

    private static final String GET_HISTORY = "SELECT * FROM " + TABLE_EXCHANGE_NAME;

    private static final String GET_ALL_CURRENCIES = "SELECT * FROM " + TABLE_CURRENCY_NAME + " ORDER BY " + LAST_USED + " DESC";

    private String getExchangeHistory(Set<String> currencies) {
        String SQLQuery = "";
        for (String currency: currencies) {
            SQLQuery += EXCHANGE_BASE + " = '" + currency + "' OR " + EXCHANGE_SYMBOLS + " = '" + currency + "' OR ";
        }
        return "SELECT * FROM " + TABLE_EXCHANGE_NAME + " WHERE " + SQLQuery.substring(0, SQLQuery.length() - 3);
    }

    private String getExchangeHistory(long dateFrom, long dateTo) {
        String SQLQuery = "";
        return "SELECT * FROM " + TABLE_EXCHANGE_NAME + " WHERE "
                + EXCHANGE_MILLIS + " BETWEEN '" + dateFrom + "' AND '" + dateTo + "'";
    }

    private String getExchangeHistory(Set<String> currencies, long dateFrom, long dateTo) {
        String SQLQuery = "";
        for (String currency: currencies) {
            SQLQuery += EXCHANGE_BASE + " = '" + currency + "' OR " + EXCHANGE_SYMBOLS + " = '" + currency + "' OR ";
        }
        return "SELECT * FROM " + TABLE_EXCHANGE_NAME + " WHERE ("
                + SQLQuery.substring(0, SQLQuery.length() - 4) + ") AND  "
                + EXCHANGE_MILLIS + " BETWEEN '" + dateFrom + "' AND '" + dateTo + "'";
    }

    private String getExchangeHistory() {
        return "SELECT * FROM " + TABLE_EXCHANGE_NAME;
    }

    private String getFilter(String name) {
        return "SELECT * FROM " + TABLE_CURRENCY_NAME + " WHERE " + CURRENCY_BASE + " = '" + name + "'";
    }

    @Override
    public void onCreate(SQLiteDatabase mySQLiteDB) {
        mySQLiteDB.execSQL(CURRENCY_TABLE);
        mySQLiteDB.execSQL(EXCHANGE_TABLE);
        fillCurrencyData(mySQLiteDB);
    }

    //SINGLETON
    public static synchronized DBHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DBHelper(ExchangerApp.getContext());
        }
        return INSTANCE;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public List<Exchange> loadAllHistory() {
        List<Exchange> exchanges = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase();) {
            try (Cursor cursor = db.query(TABLE_EXCHANGE_NAME, null, null, null, null, null, null)) {
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                    //Находим индексы колонок
                    int baseColId = cursor.getColumnIndex(EXCHANGE_BASE);
                    int symbolsColId = cursor.getColumnIndex(EXCHANGE_SYMBOLS);
                    int amountFromColId = cursor.getColumnIndex(EXCHANGE_BASE_AMOUNT);
                    int amountToColId = cursor.getColumnIndex(EXCHANGE_SYMBOLS_AMOUNT);
                    int rateColId = cursor.getColumnIndex(EXCHANGE_RATE);
                    int dateColId = cursor.getColumnIndex(EXCHANGE_DATE);
                    int timeColId = cursor.getColumnIndex(EXCHANGE_TIME);
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
            }
        }
        return exchanges;
    }

    public List<Currency> loadAll() {
        List<Currency> currencies = new ArrayList<>();
        Currency currency;
        try (SQLiteDatabase db = this.getReadableDatabase();) {
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

    public List<Currency> getFilteredCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        Set<Currency> currenciesSet = new HashSet<>();
        try (SQLiteDatabase db = this.getReadableDatabase();) {
            try (Cursor cursor = db.rawQuery(GET_HISTORY, null)) {
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    do {
                        String nameFrom = cursor.getString(cursor.getColumnIndex(EXCHANGE_BASE));
                        String nameTo = cursor.getString(cursor.getColumnIndex(EXCHANGE_SYMBOLS));
                        currenciesSet.add(new Currency(nameTo));
                        Log.d(TAG, "currency " + nameTo + "was added to Set");
                        currenciesSet.add(new Currency(nameFrom));
                        Log.d(TAG, "currency " + nameFrom + "was added to Set");
                    } while (cursor.moveToNext());
                }
            } catch (SQLException e) {
                Log.d(TAG, e.getMessage());
            }

            for (Currency currency: currenciesSet) {
                try (Cursor cursor = db.rawQuery(getFilter(currency.getName()), null)) {
                    if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                        int filter = cursor.getInt(cursor.getColumnIndex(FILTER));
                        if (filter == 0) {
                            currencies.add(new Currency(currency.getName(), false));
                            Log.d(TAG, "currency " + currency.getName() + "was added to List with isFilter = false");
                        } else {
                            currencies.add(new Currency(currency.getName(), true));
                            Log.d(TAG, "currency " + currency.getName() + "was added to List with isFilter = true");
                        }
                    }
                } catch (SQLException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }
        return currencies;
    }


    public List<Exchange> getSortedExchangeHistory(Set<String> currencies) {
        List<Exchange> exchanges = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(getExchangeHistory(currencies), null)) {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    String currencyFrom = cursor.getString(cursor.getColumnIndex(EXCHANGE_BASE));
                    String currencyTo = cursor.getString(cursor.getColumnIndex(EXCHANGE_SYMBOLS));
                    double amountFrom = cursor.getDouble(cursor.getColumnIndex(EXCHANGE_BASE_AMOUNT));
                    double amountTo = cursor.getDouble(cursor.getColumnIndex(EXCHANGE_SYMBOLS_AMOUNT));
                    String date = cursor.getString(cursor.getColumnIndex(EXCHANGE_DATE));
                    String time = cursor.getString(cursor.getColumnIndex(EXCHANGE_TIME));
                    long millis = cursor.getLong(cursor.getColumnIndex(EXCHANGE_MILLIS));
                    exchanges.add(new Exchange(currencyFrom, currencyTo, amountFrom, amountTo, date, time, millis));
                    Log.d(TAG, currencyFrom + " " + currencyTo + " " + date);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }
        return exchanges;
    }

    public List<Exchange> getSortedExchangeHistory(long dateFrom, long dateTo) {
        List<Exchange> exchanges = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(getExchangeHistory(dateFrom, dateTo), null)) {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    String currencyFrom = cursor.getString(cursor.getColumnIndex(EXCHANGE_BASE));
                    String currencyTo = cursor.getString(cursor.getColumnIndex(EXCHANGE_SYMBOLS));
                    double amountFrom = cursor.getDouble(cursor.getColumnIndex(EXCHANGE_BASE_AMOUNT));
                    double amountTo = cursor.getDouble(cursor.getColumnIndex(EXCHANGE_SYMBOLS_AMOUNT));
                    String date = cursor.getString(cursor.getColumnIndex(EXCHANGE_DATE));
                    String time = cursor.getString(cursor.getColumnIndex(EXCHANGE_TIME));
                    long millis = cursor.getLong(cursor.getColumnIndex(EXCHANGE_MILLIS));
                    exchanges.add(new Exchange(currencyFrom, currencyTo, amountFrom, amountTo, date, time, millis));
                    Log.d(TAG, currencyFrom + " " + currencyTo + " " + date);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }
        return exchanges;
    }

    public List<Exchange> getSortedExchangeHistory(Set<String> currencies, long dateFrom, long dateTo) {
        List<Exchange> exchanges = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(getExchangeHistory(currencies, dateFrom, dateTo), null)) {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    String currencyFrom = cursor.getString(cursor.getColumnIndex(EXCHANGE_BASE));
                    String currencyTo = cursor.getString(cursor.getColumnIndex(EXCHANGE_SYMBOLS));
                    double amountFrom = cursor.getDouble(cursor.getColumnIndex(EXCHANGE_BASE_AMOUNT));
                    double amountTo = cursor.getDouble(cursor.getColumnIndex(EXCHANGE_SYMBOLS_AMOUNT));
                    String date = cursor.getString(cursor.getColumnIndex(EXCHANGE_DATE));
                    String time = cursor.getString(cursor.getColumnIndex(EXCHANGE_TIME));
                    long millis = cursor.getLong(cursor.getColumnIndex(EXCHANGE_MILLIS));
                    exchanges.add(new Exchange(currencyFrom, currencyTo, amountFrom, amountTo, date, time, millis));
                    Log.d(TAG, currencyFrom + " " + currencyTo + " " + millis);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }
        return exchanges;
    }




    //В конструктор передаем контекст, имя базы данных, CursorFactory (не используется, поэтому null), и версию базы данных
    public DBHelper (Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    private void fillCurrencyData(SQLiteDatabase currencydb) {
        currencydb.execSQL("INSERT INTO currency_name VALUES('USD', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('RUB', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('EUR', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('AUD', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('BGN', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('BRL', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('CAD', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('CHF', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('CNY', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('CZK', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('DKK', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('GBP', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('HKD', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('HRK', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('HUF', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('IDR', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('ILS', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('INR', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('JPY', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('KRW', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('MXN', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('MYR', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('NOK', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('NZD', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('PHP', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('PLN', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('RON', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('SEK', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('SGD', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('THB', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('TRY', 1, 0, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('ZAR', 1, 0, 0);");
    }


}
