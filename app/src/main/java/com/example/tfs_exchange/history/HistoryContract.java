package com.example.tfs_exchange.history;

import com.example.tfs_exchange.model.Exchange;

import java.util.List;

import io.reactivex.Observable;


/**
 * Created by pusya on 30.11.17.
 */

public interface HistoryContract {
    interface  Repository {
        Observable<List<Exchange>> loadHistory();
    }

    interface Presenter {
        void getHistory();
        void onFilterButtonClicked();
    }

    interface View {
        void setAdapter(List<Exchange> exchanges);
        void replaceByFilterFragment();
    }
}
