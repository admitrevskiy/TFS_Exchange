package com.example.tfs_exchange.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pusya on 16.10.17.
 * Читать http://sqlite.org/datatype3.html
 * Заметка от 7.11: мигрировать на Room
 */

//Создаем помощник работы с SQLite на чистом SQLite
public class DBHelper extends SQLiteOpenHelper {

    private ContentValues cv;
    private SQLiteDatabase db;
    private DBHelper dbHelper;

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


    private static final String TABLE_CURRENCY_NAME = "currency_name";
    private static final String CURRENCY_BASE = "currency_base";
    private static final String LAST_USED = "last_used";
    private static final String FAVORITE = "favorite";

    private static final String CURRENCY_TABLE = "create table " + TABLE_CURRENCY_NAME + " ("
            + CURRENCY_BASE + " string primary key, "
            + LAST_USED + " integer, "
            + FAVORITE + " integer "
            + ");";

    private static final String EXCHANGE_TABLE = "create table " + TABLE_EXCHANGE_NAME + " ("
            + EXCHANGE_BASE + " string primary key, "
            + EXCHANGE_BASE_AMOUNT + " real, "
            + EXCHANGE_SYMBOLS + " string, "
            + EXCHANGE_SYMBOLS_AMOUNT + " real, "
            + EXCHANGE_RATE + " real, "
            + EXCHANGE_DATE + " String "
            + ");";

    @Override
    public void onCreate(SQLiteDatabase mySQLiteDB) {
        mySQLiteDB.execSQL(CURRENCY_TABLE);
        mySQLiteDB.execSQL(EXCHANGE_TABLE);
        fillCurrencyData(mySQLiteDB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //В конструктор передаем контекст, имя базы данных, CursorFactory (не используется, поэтому null), и версию базы данных
    public DBHelper (Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    private void fillCurrencyData(SQLiteDatabase currencydb) {
        currencydb.execSQL("INSERT INTO currency_name VALUES('USD', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('RUB', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('EUR', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('AUD', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('BGN', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('BRL', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('CAD', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('CHF', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('CNY', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('CZK', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('DKK', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('GBP', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('HKD', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('HRK', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('HUF', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('IDR', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('ILS', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('INR', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('JPY', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('KRW', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('MXN', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('MYR', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('NOK', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('NZD', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('PHP', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('PLN', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('RON', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('SEK', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('SGD', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('THB', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('TRY', 1, 0);");
        currencydb.execSQL("INSERT INTO currency_name VALUES('ZAR', 1, 0);");
    }


}
