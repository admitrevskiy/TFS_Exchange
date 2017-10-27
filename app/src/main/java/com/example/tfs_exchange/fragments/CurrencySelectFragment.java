package com.example.tfs_exchange.fragments;



import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.tfs_exchange.AsyncDBLoader;
import com.example.tfs_exchange.Currency;
import com.example.tfs_exchange.DBHelper;
import com.example.tfs_exchange.R;
import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;
import com.example.tfs_exchange.comparators.FavoriteComparator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pusya on 27.10.17.
 */

public class CurrencySelectFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Currency>> {
    public static final int LOADER_ID = 1;

    private final static String CURRENCY_TAG = "currency";

    private final static String TAG = "MainActivity";

    private DBHelper dbHelper;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    CurrencyRecyclerListAdapter adapter;

    final List<Currency> currencies = new ArrayList<Currency>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View firstFragmentRootView = inflater.inflate(R.layout.currency_select_fragment, container, false);

        ButterKnife.bind(this, firstFragmentRootView);

        //final List<Currency> currencies = new ArrayList<Currency>();

        /** Загрузка валют из БД происходит асинхронно! **/
        getLoaderManager().initLoader(LOADER_ID, null, this);
        //Loader<List<Currency>> loader;
        Loader<Object> loader = getLoaderManager().getLoader(LOADER_ID);
        loader.forceLoad();

        //populateCurrencies(currencies);

        //final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        adapter = new CurrencyRecyclerListAdapter(currencies, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                dbHelper = new DBHelper(getContext());
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

        }, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                Log.d("Currency item ", " " + currency.getName() + " short clicked" );
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        return firstFragmentRootView;
    }


    @Override
    public Loader<List<Currency>> onCreateLoader(int id, Bundle args) {
        Loader<List<Currency>> loader = null;
        if (id == LOADER_ID) {
            loader = new AsyncDBLoader(getContext());
            Log.d(TAG, "onCreateLoader: " + loader.hashCode());
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Currency>> loader, List<Currency> data) {
        for (Currency currency : data) {
            currencies.add(currency);
        }

        Log.d(TAG, "onLoadFinished: " + loader.hashCode());
    }

    @Override
    public void onLoaderReset(Loader<List<Currency>> loader) {
        Log.d(TAG, "onLoaderReset for AsyncLoader " + loader.hashCode());
    }
}
