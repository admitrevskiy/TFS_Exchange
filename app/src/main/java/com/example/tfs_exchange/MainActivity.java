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




        populateRecords(currencies);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        CurrencyRecyclerListAdapter adapter = new CurrencyRecyclerListAdapter(currencies);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
    }



    private void populateRecords(List<Currency> currencies){

        dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("currency_name", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int baseColId = cursor.getColumnIndex("currency_base");
            int lastUsedColId = cursor.getColumnIndex("last_used");
            int favoriteColId = cursor.getColumnIndex("favorite");

            do {
                Currency currency = new Currency();
                currency.setName(cursor.getString(baseColId));
                currency.setLastUse(cursor.getInt(lastUsedColId));
                int favorite = cursor.getInt(favoriteColId);
                if (favorite == 0)
                {
                    currency.setFavorite(false);
                } else {
                    currency.setFavorite(true);
                }
                currencies.add(currency);
            } while (cursor.moveToNext());
        }

        db.close();


        /**
        Currency currency1 = new Currency("USD", 400, false);
        currencies.add(currency1);

        Currency currency2 = new Currency("RUB", 400, false);
        currencies.add(currency2);

        Currency currency3 = new Currency("KRN", 400, false);
        currencies.add(currency3);
         **/

    }
}

