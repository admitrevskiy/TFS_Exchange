package com.example.tfs_exchange.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tfs_exchange.Currency;
import com.example.tfs_exchange.Exchange;
import com.example.tfs_exchange.R;
import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;
import com.example.tfs_exchange.adapter.HistoryRecyclerListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pusya on 10.11.17.
 */

public class HisroryFragment extends Fragment {

    private final static String TAG = "HistoryFragment";

    private List<Exchange> exchanges = new ArrayList<Exchange>();

    private HistoryRecyclerListAdapter adapter;

    @BindView(R.id.history_recycler_view)
    RecyclerView recyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exchanges.add(new Exchange("USD", "RUB", 2.0, 120.0, new Date()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        exchanges.add(new Exchange("EUR", "RUB", 2.0, 140.0, new Date()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View historyFragmentRootView = inflater.inflate(R.layout.history_fragment, container, false);

        ButterKnife.bind(this, historyFragmentRootView);

        adapter = new HistoryRecyclerListAdapter(exchanges);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        Log.d(TAG, " onCreareView" + this.hashCode());

        return historyFragmentRootView;
    }
}
