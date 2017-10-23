package com.example.tfs_exchange;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;
import com.example.tfs_exchange.DBHelper;
import com.example.tfs_exchange.Currency;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Currency> currencies = new ArrayList<Currency>();

        populateCurrencies(currencies);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        CurrencyRecyclerListAdapter adapter = new CurrencyRecyclerListAdapter(currencies);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
    }

    //Добавляем валюты в список из БД
    private void populateCurrencies(List<Currency> currencies){

        //подключаем Data Base Helper, получаем из него БД для чтения
        dbHelper = new DBHelper(getApplicationContext());
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
                    currency.setFavorite(false);
                } else {
                    currency.setFavorite(true);
                }

                //Добавляем валюту в List с валютами
                currencies.add(currency);
            } while (cursor.moveToNext());
        }


        //Закрываем БД
        db.close();
    }
}

