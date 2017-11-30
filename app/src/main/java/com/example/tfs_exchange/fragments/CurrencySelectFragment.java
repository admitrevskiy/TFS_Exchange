package com.example.tfs_exchange.fragments;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.tfs_exchange.currency_select.CurrencyContract;
import com.example.tfs_exchange.currency_select.CurrencySelectPresenter;
import com.example.tfs_exchange.model.Currency;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.R;
import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;
import com.example.tfs_exchange.comparators.FavoriteComparator;
import com.example.tfs_exchange.comparators.LastUsedComparator;
import com.example.tfs_exchange.comparators.LongClickedComparator;

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
 * Заметка от 9.11: анонимные внутренние классы переписать на лямбдах
 */

public class CurrencySelectFragment extends Fragment implements CurrencyContract.View {
    private static final int LOADER_ID = 1;

    private final static String TAG = "CurrencySelectFragment";
    private DBHelper dbHelper;
    private FavoriteComparator faveComp;
    private LastUsedComparator lastUsedComp;
    private LongClickedComparator longClickedComp;
    private boolean noItemLongClicked;
    private Currency selectedCurrency;
    private CurrencyContract.Presenter mPresenter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private CurrencyRecyclerListAdapter adapter;

    private List<Currency> currencies = new ArrayList<Currency>();
    //private final List<Currency> faveCurrencies = new ArrayList<Currency>();

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

    /**
    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Загрузка валют из БД происходит асинхронно
        getLoaderManager().initLoader(LOADER_ID, null, this);
        Loader<Object> loader = getLoaderManager().getLoader(LOADER_ID);
        loader.forceLoad();

        super.onCreate(savedInstanceState);
    }
     **/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View firstFragmentRootView = inflater.inflate(R.layout.currency_select_fragment, container, false);

        ButterKnife.bind(this, firstFragmentRootView);

        dbHelper = DBHelper.getInstance();
        faveComp = new FavoriteComparator();
        lastUsedComp = new LastUsedComparator();
        longClickedComp = new LongClickedComparator();

        noItemLongClicked = true;
        mPresenter = new CurrencySelectPresenter(this);
        mPresenter.getCurrencies();

        //setAdapter(currencies);

        Log.d(TAG, " onCreateView" + this.hashCode());
        return firstFragmentRootView;
    }

    @Override
    public void setAdapter(List<Currency> currencies) {
        adapter = new CurrencyRecyclerListAdapter(currencies, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                mPresenter.setFaveToDb(currency);
                sortCurrencies();
                Log.d(TAG, "Currency item " + currency.getName() + " fave changed");
            }

        }, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                Log.d("Currency item ", " " + currency.getName() + " short clicked");
                mPresenter.setTime(currency);
                sortCurrencies();
                //dbHelper.setTimeToDB(currency);
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
                    mPresenter.setTime(currency);
                    //dbHelper.setTimeToDB(currency);
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
    }

    @Override
    public void onStop() {
        super.onStop();
        dbHelper.close();
    }

    @Override
    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
        sortCurrencies();
    }


    //Сортируем избранные валюты вверх по списку - сначала по использованиям, потом по избранности
    private void sortCurrencies() {
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

    //Выбор валюты после LongClick
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

    @Nullable
    @Override
    public void onDetach() {
        super.onDetach();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    /**
    @Override
    public Loader<List<Currency>> onCreateLoader(int id, Bundle args) {
        Loader<List<Currency>> loader = null;
        if (id == LOADER_ID) {
            loader = new AsyncCurrencyDBLoader(getContext());
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
    **/

}
