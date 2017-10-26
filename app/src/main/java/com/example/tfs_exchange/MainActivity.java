package com.example.tfs_exchange;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;
import com.example.tfs_exchange.comparators.FavoriteComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final static String CURRENCY_TAG = "currency";

    private DBHelper dbHelper;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    CurrencyRecyclerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final List<Currency> currencies = new ArrayList<Currency>();

        populateCurrencies(currencies);

        //final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        adapter = new CurrencyRecyclerListAdapter(currencies, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                dbHelper = new DBHelper(getBaseContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                int changeFavorite;
                if (currency.isFavorite())
                {
                    currency.setFavorite(false);
                    changeFavorite = 0;
                } else {
                    currency.setFavorite(true);
                    changeFavorite = 1;
                }
                cv.put("FAVORITE", changeFavorite);
                db.update("currency_name", cv, "currency_base = ?", new String[] {currency.getName()});
                Log.d(CURRENCY_TAG, " " + currency.getName() + " favorite changed" );

                //Сортируем избранные валюты вверх по списку
                Collections.sort(currencies, new FavoriteComparator());
                //Обновляем RecycleView, меняем "пустую" звездочку на "избранную
                adapter.notifyDataSetChanged();
            }

        });


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
    }
}

