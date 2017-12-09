package com.example.tfs_exchange.history;

import android.util.Log;

import com.example.tfs_exchange.exchange.ExchangeContract;
import com.example.tfs_exchange.model.Exchange;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by pusya on 30.11.17.
 */

public class HistoryPresenter implements HistoryContract.Presenter {

    private static final String TAG = "HistoryPresenter";

    //MVP
    private HistoryContract.View mView;
    private HistoryContract.Repository mRepository;

    //Конструктор
    public HistoryPresenter(HistoryContract.View mView) {
        this.mView = mView;
        mRepository = new HistoryRepository();
    }

    //Загружаем валюты
    @Override
    public void getHistory() {
        Disposable historySubscription = mRepository.loadHistory()
                .subscribe(this::showHistory, throwable -> {
                    Log.d(TAG, "problems, bro");
                });
    }

    //Открываем фрагмент с фильтрами
    @Override
    public void onFilterButtonClicked() {
        mView.replaceByFilterFragment();
    }

    //Передаем список обменов view
    private void showHistory(List<Exchange> exchanges) {
        mView.setAdapter(exchanges);
    }

}
