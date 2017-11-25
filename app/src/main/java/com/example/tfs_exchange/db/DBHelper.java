package com.example.tfs_exchange.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    private static final String TABLE_CURRENCY_NAME = "currency_name";
    private static final String CURRENCY_BASE = "currency_base";
    private static final String LAST_USED = "last_used";
    private static final String FAVORITE = "favorite";
    private static final String FILTER = "filter";

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
            + EXCHANGE_TIME + " String "
            + ");";

    private static final String GET_HISTORY = "SELECT * FROM " + TABLE_EXCHANGE_NAME;

    private String getExchangeHistory(Set<String> currencies) {
        String SQLQuery = "";
        for (String currency: currencies) {
            SQLQuery += EXCHANGE_BASE + " = '" + currency + "' OR " + EXCHANGE_SYMBOLS + " = '" + currency + "' OR ";
        }
        return "SELECT * FROM " + TABLE_EXCHANGE_NAME + " WHERE " + SQLQuery.substring(0, SQLQuery.length() - 3);
    }

    private String getExchangeHistory(String dateFrom, String dateTo) {
        String SQLQuery = "";
        return "SELECT * FROM " + TABLE_EXCHANGE_NAME + " WHERE "
                + EXCHANGE_DATE + " BETWEEN '" + dateFrom + "' AND '" + dateTo + "'";
    }

    private String getExchangeHistory(Set<String> currencies, String dateFrom, String dateTo) {
        String SQLQuery = "";
        for (String currency: currencies) {
            SQLQuery += EXCHANGE_BASE + " = '" + currency + "' OR " + EXCHANGE_SYMBOLS + " = '" + currency + "' OR ";
        }
        return "SELECT * FROM " + TABLE_EXCHANGE_NAME + " WHERE "
                + SQLQuery.substring(0, SQLQuery.length() - 3) + " AND "
                + EXCHANGE_DATE + " BETWEEN '" + dateFrom + "' AND '" + dateTo + "'";
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

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //Запись обмена в БД
    public void setExchangeToDB(String currencyFrom, String currencyTo, double amountFrom, double amountTo, double rate) {
        cv = new ContentValues();
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Date date = new Date();
            cv.put("EXCHANGE_BASE", currencyFrom);
            cv.put("EXCHANGE_BASE_AMOUNT", amountFrom);
            cv.put("EXCHANGE_SYMBOLS", currencyTo);
            cv.put("EXCHANGE_SYMBOLS_AMOUNT", amountTo);
            cv.put("EXCHANGE_RATE", rate);
            cv.put("EXCHANGE_DATE", dateFormat.format(date));
            db.insert("exchange_name", null, cv);
        }
    }

    //Запись избранных в БД
    public void setFaveToDB(Currency currency) {
        cv = new ContentValues();
        int changeFavorite;
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            if (currency.isFavorite())
            {
                currency.setFavorite(false);
                changeFavorite = 0;
            } else {
                currency.setFavorite(true);
                changeFavorite = 1;
            }
            cv.put(FAVORITE, changeFavorite);
            db.update("currency_name", cv, "currency_base = ?", new String[] {currency.getName()});
            Log.d(TAG, " " + currency.getName() + " favorite changed" );
            //sortCurrencies();
        }
    }

    //Записываем в БД время последнего использования
    public void setTimeToDB(Currency currency) {
        cv = new ContentValues();
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            long lastUse = new Date().getTime();
            int time = (int)lastUse/1000;
            currency.setLastUse(lastUse);
            cv.put("LAST_USED", time);
            db.update("currency_name", cv, "currency_base = ?", new String[] {currency.getName()});
            Log.d(TAG, " " + currency.getName() + " lastUsed changed" );
        }
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
                    exchanges.add(new Exchange(currencyFrom, currencyTo, amountFrom, amountTo, date, time));
                    Log.d(TAG, currencyFrom + " " + currencyTo + " " + date);
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
