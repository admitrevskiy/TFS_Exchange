package com.example.tfs_exchange.fragments;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tfs_exchange.R;
import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;
import com.example.tfs_exchange.db.AsyncCurrencyDBLoader;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.model.Currency;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pusya on 20.11.17.
 */

public class HistoryFilterFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Currency>>{

    private static final String TAG = "HistoryFilterFragment";

    private final String[] periods = {"все время", "неделя", "месяц", "выбрать даты"};
    private int period;
    private final int LOADER_ID = 3;
    private DBHelper dbHelper;
    private ContentValues cv;
    SQLiteDatabase db;

    private CurrencyRecyclerListAdapter adapter;
    private List<Currency> currencies = new ArrayList<Currency>();

    @BindView(R.id.filter_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.period_spinner)
    Spinner periodSpinner;

    @BindView(R.id.date_from_spinner)
    Spinner dateFromSpinner;

    @BindView(R.id.date_to_spinner)
    Spinner dateToSpinner;

    private void disableDate() {
        dateFromSpinner.setEnabled(false);
        dateToSpinner.setEnabled(false);
        dateFromSpinner.setVisibility(View.GONE);
        dateToSpinner.setVisibility(View.GONE);
    }

    private void enableDate() {
        dateFromSpinner.setEnabled(true);
        dateToSpinner.setEnabled(true);
        dateFromSpinner.setVisibility(View.VISIBLE);
        dateToSpinner.setVisibility(View.VISIBLE);
        Log.d(TAG, "enableDate");
    }

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        /** Загрузка валют из БД происходит асинхронно **/
        getLoaderManager().initLoader(LOADER_ID, null, this);
        Loader<Object> loader = getLoaderManager().getLoader(LOADER_ID);
        loader.forceLoad();
        /** ------------------------------------------**/
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View historyFilterFragmentRootView = inflater.inflate(R.layout.history_filter_fragment, container, false);

        ButterKnife.bind(this, historyFilterFragmentRootView);
        disableDate();
        Log.d(TAG, currencies.toString());
        //addCurrencies();

        //RecyclerView
        adapter = new CurrencyRecyclerListAdapter(currencies, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                setFaveToDB(currency);
                //Log.d("Currency item ", " " + currency.getName() + " fave changed");
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        //Spinner для выбора периода
        ArrayAdapter<String> selectPeriodAdaper = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, periods);
        selectPeriodAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(selectPeriodAdaper);
        /** Переписать на butterKnife!**/
        AdapterView.OnItemSelectedListener periodSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)parent.getItemAtPosition(position);
                switch (item) {
                    case "все время":
                        period = 0;
                        Log.d(TAG, "period " + period);
                        disableDate();
                        break;
                    case "неделя":
                        period = 1;
                        Log.d(TAG, "period " + period);
                        disableDate();
                        break;
                    case "месяц":
                        period = 2;
                        Log.d(TAG, "period " + period);
                        disableDate();
                        break;
                    case "выбрать даты":
                        period = 3;
                        Log.d(TAG, "period " + period);
                        enableDate();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        periodSpinner.setOnItemSelectedListener(periodSelectedListener);
        Log.d(TAG, " onCreateView" + this.hashCode());
        return historyFilterFragmentRootView;
    }

    public void addCurrencies() {
        currencies.add(new Currency("USD", 0, false));
        currencies.add(new Currency("RUB", 0, true));
    }

    @Override
    public Loader<List<Currency>> onCreateLoader(int id, Bundle args) {
        Loader<List<Currency>> loader = null;
        if (id == LOADER_ID) {
            loader = new AsyncCurrencyDBLoader(getContext(), true);
            Log.d(TAG, "onCreateLoader: " + loader.hashCode());
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Currency>> loader, List<Currency> data) {
        Log.d(TAG, data.toString());
        for (Currency currency : data) {
            currencies.add(currency);
        }
        Log.d(TAG, "onLoadFinished: " + loader.hashCode());
        //sortCurrencies();
    }

    @Override
    public void onLoaderReset(Loader<List<Currency>> loader) {
        Log.d(TAG, "onLoaderReset for AsyncLoader " + loader.hashCode());
    }

    //Записываем в БД изменение избранности валюты
    private void setFaveToDB(Currency currency) {
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
        int changeFilter;
        if (currency.isFilter())
        {
            currency.setFilter(false);
            changeFilter = 0;
        } else {
            currency.setFilter(true);
            changeFilter = 1;
        }
        cv.put("FILTER", changeFilter);
        db.update("currency_name", cv, "currency_base = ?", new String[] {currency.getName()});
        Log.d(TAG, " " + currency.getName() + " filter changed" );
        //sortCurrencies();
        db.close();
        adapter.notifyDataSetChanged();
    }
}
