package com.example.tfs_exchange.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.tfs_exchange.db.AsyncExchangeDBLoader;
import com.example.tfs_exchange.history.HistoryContract;
import com.example.tfs_exchange.history.HistoryPresenter;
import com.example.tfs_exchange.model.Exchange;
import com.example.tfs_exchange.R;
import com.example.tfs_exchange.adapter.HistoryRecyclerListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by pusya on 10.11.17.
 * История обменов, допилить фильтры
 */

public class HisroryFragment extends Fragment implements HistoryContract.View {

    private final static String TAG = "HistoryFragment";
    private HistoryContract.Presenter mPresenter;
    private Unbinder unbinder;

    private List<Exchange> exchanges = new ArrayList<Exchange>();
    private HistoryRecyclerListAdapter adapter;

    @BindView(R.id.history_recycler_view)
    RecyclerView recyclerView;


    @Override
    public void onPause() {
        super.onPause();
        exchanges = new ArrayList<>();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View historyFragmentRootView = inflater.inflate(R.layout.history_fragment, container, false);

        unbinder = ButterKnife.bind(this, historyFragmentRootView);

        mPresenter = new HistoryPresenter(this);
        mPresenter.subscribeHistory();

        Log.d(TAG, " onCreateView" + this.hashCode());

        return historyFragmentRootView;
    }

    @Override
    public void onDetach(){
        unbinder.unbind();
        super.onDetach();
    }

    @Override
    public void setAdapter(List<Exchange> exchanges) {
        adapter = new HistoryRecyclerListAdapter(exchanges);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
    }
}
