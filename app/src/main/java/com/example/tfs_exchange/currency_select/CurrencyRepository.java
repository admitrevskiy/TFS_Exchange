package com.example.tfs_exchange.currency_select;

import android.util.Log;

import com.example.tfs_exchange.comparators.FavoriteComparator;
import com.example.tfs_exchange.comparators.LastUsedComparator;
import com.example.tfs_exchange.comparators.LongClickedComparator;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.model.Currency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by pusya on 30.11.17.
 */

public class CurrencyRepository implements CurrencyContract.Repository {

    private static final String TAG = "CurrencyRepository";
    private static final int LOADER_ID = 1;

    private List<Currency> currencies = new ArrayList<Currency>();

    private DBHelper dbHelper = DBHelper.getInstance();

    private FavoriteComparator faveComp = new FavoriteComparator();
    private LastUsedComparator lastUsedComp = new LastUsedComparator();
    //private LongClickedComparator longClickedComp = new LongClickedComparator();


    @Override
    public Observable<List<Currency>> loadCurrencies() {

        return Observable
                .just(dbHelper.loadAll())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void setFaveToDB (Currency currency) {
        dbHelper.setFaveToDB(currency);
    }

    @Override
    public void setTimeToDB(Currency currency) {
        dbHelper.setTimeToDB(currency);
    }

    //Сортируем избранные валюты вверх по списку - сначала по использованиям, потом по избранности
    private void sortCurrencies() {
        Collections.sort(currencies, lastUsedComp);
        Collections.sort(currencies, faveComp);
        //adapter.notifyDataSetChanged();
        Log.d(TAG, " sortCurrencies");
    }



}
