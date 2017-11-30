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

    private HistoryContract.View mView;
    private HistoryContract.Repository mRepostory;

    private List<Exchange> exchanges;

    public HistoryPresenter(HistoryContract.View mView) {
        this.mView = mView;
        mRepostory = new HistoryRepository();
    }

    @Override
    public void subscribeHistory() {
        exchanges = new ArrayList<>();
        Disposable historySubscription = mRepostory.loadHistory()
                .subscribe(this::showHistory, throwable -> {
                    Log.d(TAG, "problems, bro");
                });
    }

    private void showHistory(List<Exchange> exchanges) {
        mView.setAdapter(exchanges);
    }

}
