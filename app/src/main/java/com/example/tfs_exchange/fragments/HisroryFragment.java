package com.example.tfs_exchange.fragments;

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
import com.example.tfs_exchange.model.Exchange;
import com.example.tfs_exchange.R;
import com.example.tfs_exchange.adapter.HistoryRecyclerListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pusya on 10.11.17.
 * История обменов, допилить фильтры
 */

public class HisroryFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Exchange>> {

    private static final int LOADER_ID = 2;

    private final static String TAG = "HistoryFragment";

    private List<Exchange> exchanges = new ArrayList<Exchange>();

    private HistoryRecyclerListAdapter adapter;

    @BindView(R.id.history_recycler_view)
    RecyclerView recyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        /** Загрузка обменов из БД происходит асинхронно **/
        getLoaderManager().initLoader(LOADER_ID, null, this);
        Loader<Object> loader = getLoaderManager().getLoader(LOADER_ID);
        loader.forceLoad();
        /** ------------------------------------------**/

        super.onCreate(savedInstanceState);
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

    @Override
    public Loader<List<Exchange>> onCreateLoader(int id, Bundle args) {
        Loader<List<Exchange>> loader = null;
        if (id == LOADER_ID) {
            loader = new AsyncExchangeDBLoader(getContext(), true, "RUB", "USD");
            //loader = new AsyncExchangeDBLoader(getContext());
            Log.d(TAG, "onCreateLoader: " + loader.hashCode());
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Exchange>> loader, List<Exchange> data) {
        for (Exchange exchange : data) {
            exchanges.add(exchange);
        }
        Log.d(TAG, "onLoadFinished: " + loader.hashCode());
    }

    @Override
    public void onLoaderReset(Loader<List<Exchange>> loader) {
        Log.d(TAG, "onLoaderReset for AsyncLoader " + loader.hashCode());
    }
}
