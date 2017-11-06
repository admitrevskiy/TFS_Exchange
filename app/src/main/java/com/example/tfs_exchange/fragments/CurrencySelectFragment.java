package com.example.tfs_exchange.fragments;



import android.nfc.Tag;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.example.tfs_exchange.comparators.LastUsedComparator;
import com.example.tfs_exchange.comparators.LongClickedComparator;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemLongClick;

/**
 * Created by pusya on 27.10.17.
 */

public class CurrencySelectFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Currency>> {
    public static final int LOADER_ID = 1;

    private final static String CURRENCY_TAG = "currency";
    private final static String TAG = "CurrencySelectFragment";
    private ContentValues cv;
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private FavoriteComparator faveComp;
    private LastUsedComparator lastUsedComp;
    private LongClickedComparator longClickedComp;
    private boolean noItemLongClicked;
    private Currency selectedCurrency;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    //@BindView(R.id.selected_currency)
    //TextView seletcedCurrency;

    private CurrencyRecyclerListAdapter adapter;

    private List<Currency> currencies = new ArrayList<Currency>();
    private final List<Currency> faveCurrencies = new ArrayList<Currency>();

    @Nullable
    @Override
    public void onPause() {
        sortCurrencies();
        noItemLongClicked = true;
        selectedCurrency = null;
        currencies = null;
        currencies = new ArrayList<>();

        super.onPause();
        Log.d(TAG, " onPause");
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View firstFragmentRootView = inflater.inflate(R.layout.currency_select_fragment, container, false);

        ButterKnife.bind(this, firstFragmentRootView);

        faveComp = new FavoriteComparator();
        lastUsedComp = new LastUsedComparator();
        longClickedComp = new LongClickedComparator();

        noItemLongClicked = true;

        adapter = new CurrencyRecyclerListAdapter(currencies, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                setFaveToDB(currency);
                Log.d("Currency item ", " " + currency.getName() + " fave changed");
            }

        }, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                Log.d("Currency item ", " " + currency.getName() + " short clicked");
                setTimeToDB(currency);
                if (noItemLongClicked) {
                    replaceExchangeFragment(currency.getName(), getCurrencyForExchange(currency));
                }
                else replaceExchangeFragment(selectedCurrency.getName(), currency.getName());
            }
        }, new  CurrencyRecyclerListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Currency currency, int id) {
                if (noItemLongClicked) {
                    Log.d("Currency item ", currency.getName() + " long clicked" );
                    currency.setLongClicked(true);
                    Collections.sort(currencies, longClickedComp);
                    currency.setLongClicked(false);
                    adapter.notifyDataSetChanged();
                    noItemLongClicked = false;
                    selectedCurrency = currency;
                    setTimeToDB(currency);
                }
                else {
                    Log.d("Currency item ", currency.getName() + " long clicked, but another currency is already choosed" );
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        Log.d(TAG, " onCreareView" + this.hashCode());
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
        sortCurrencies();
    }

    @Override
    public void onLoaderReset(Loader<List<Currency>> loader) {
        Log.d(TAG, "onLoaderReset for AsyncLoader " + loader.hashCode());
    }

    private void setFaveToDB(Currency currency) {
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
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
        sortCurrencies();
    }

    private void setTimeToDB(Currency currency) {
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
        long lastUse = new Date().getTime();
        int time = (int)lastUse/1000;
        currency.setLastUse(lastUse);
        cv.put("LAST_USED", time);
        db.update("currency_name", cv, "currency_base = ?", new String[] {currency.getName()});
        Log.d(CURRENCY_TAG, " " + currency.getName() + " lastUsed changed" );

    }

    private void sortCurrencies() {
        //Сортируем избранные валюты вверх по списку
        Collections.sort(currencies, lastUsedComp);
        Collections.sort(currencies, faveComp);
        adapter.notifyDataSetChanged();
        Log.d(TAG, " sortCurrencies");
    }

    private void replaceExchangeFragment(String currencyFrom, String currencyTo) {
        ExchangeFragment fragment = new ExchangeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putStringArray("currencies", new String[]{currencyFrom, currencyTo});
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

    private String getCurrencyForExchange(Currency selectedCurrency) {
        for (Currency currency : currencies) {
            if (currency.isFavorite() && !currency.getName().equals(selectedCurrency.getName())) {
                return currency.getName();
            }
            else if (!currency.isFavorite()) {
                break;
            }
        }
        if (selectedCurrency.getName().equals("USD")) {
            return "RUB";
        }
        return "USD";
    }

}
