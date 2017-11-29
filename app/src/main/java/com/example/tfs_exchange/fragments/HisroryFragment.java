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

/**
 * Created by pusya on 10.11.17.
 * История обменов, допилить фильтры
 */

public class HisroryFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Exchange>> {

    private static final int LOADER_ID = 2;
    private final static String TAG = "HistoryFragment";
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private int periodFilter;
    private Set<String> currencyFilter;
    private String dateFromFilter, dateToFilter;
    private long dateFromMillis, dateToMillis;

    private SharedPreferences sharedPrefs;

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
        sharedPrefs = this.getActivity().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
        if (sharedPrefs.contains(getString(R.string.period_id))) {
            periodFilter = sharedPrefs.getInt(getString(R.string.period_id), 0);
            Log.d(TAG, "Period ID: " + String.valueOf(periodFilter));
        }
        if (sharedPrefs.contains(getString(R.string.currencies))) {
            currencyFilter = sharedPrefs.getStringSet(getString(R.string.currencies), new HashSet<>());
        }
        if (sharedPrefs.contains(getString((R.string.saved_date_from)))) {
            dateFromFilter = sharedPrefs.getString(getString(R.string.saved_date_from), "");
            Log.d(TAG, "DateFrom: " + dateFromFilter);
        }
        if (sharedPrefs.contains(getString((R.string.saved_date_to)))) {
            dateToFilter = sharedPrefs.getString(getString(R.string.saved_date_to), "");
            Log.d(TAG, "DateTo: " + dateToFilter);
        }

        Log.d(TAG, "Period ID: " + String.valueOf(periodFilter));
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
        HashSet<String> set = new HashSet<>();
        if (id == LOADER_ID) {


            if (periodFilter == 0 && (currencyFilter == null || currencyFilter.size() == 0)) {
                loader = new AsyncExchangeDBLoader(getContext());
                Log.d(TAG, "Loader 0");
            } else if (periodFilter == 0 && currencyFilter.size()>0) {
                loader = new AsyncExchangeDBLoader(getContext(), currencyFilter);
                Log.d(TAG, "Loader 0 with currencies");
            } else if (periodFilter == 3) {
                if (currencyFilter == null || currencyFilter.size() == 0 && (dateFromFilter != null && dateToFilter != null)) {
                    try {
                        Date dateFrom = dateFormat.parse(dateFromFilter);
                        Date dateTo = dateFormat.parse(dateToFilter);
                        dateFromMillis = dateFrom.getTime()/1000;
                        dateToMillis = dateTo.getTime()/1000;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    loader = new AsyncExchangeDBLoader(getContext(), dateFromMillis, dateToMillis);
                    Log.d(TAG, "Loader 3 " + dateFromFilter + " " + dateToFilter);
                } else {
                    try {
                        Date dateFrom = dateFormat.parse(dateFromFilter);
                        Date dateTo = dateFormat.parse(dateToFilter);
                        dateFromMillis = dateFrom.getTime()/1000;
                        dateToMillis = dateTo.getTime()/1000;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    loader = new AsyncExchangeDBLoader(getContext(), currencyFilter, dateFromMillis, dateToMillis);
                    Log.d(TAG, "Loader 3 with currencies " + dateFromMillis + " " + dateToMillis);
                }
            } else if (periodFilter == 1) {
                Date todayDate = new Date();
                dateToMillis = todayDate.getTime()/1000;
                dateFromMillis = dateToMillis - 60*60*24*7;
                if (currencyFilter == null || currencyFilter.size() == 0) {
                    loader = new AsyncExchangeDBLoader(getContext(), dateFromMillis, dateToMillis);
                    Log.d(TAG, "Loader 1");
                } else {
                    loader = new AsyncExchangeDBLoader(getContext(), currencyFilter, dateFromMillis, dateToMillis);
                    Log.d(TAG, "Loader 1 with currencies from " + dateFromMillis + " to " + dateToMillis);
                }
            } else if (periodFilter == 2){
                Date todayDate = new Date();
                dateToMillis = todayDate.getTime()/1000;
                dateFromMillis =  dateToMillis - 60*60*24*30;
                if (currencyFilter == null || currencyFilter.size() == 0) {
                    loader = new AsyncExchangeDBLoader(getContext(), dateFromMillis, dateToMillis);
                    Log.d(TAG, "Loader 2");
                } else {
                    loader = new AsyncExchangeDBLoader(getContext(), currencyFilter, dateFromMillis, dateToMillis);
                    Log.d(TAG, "Loader 2 with currencies from " + dateFromMillis + " to " + dateToMillis);
                }
            }


            //loader = new AsyncExchangeDBLoader(getContext(), true, currencyFilter, "25.11.2017", "25.11.2017");
            //loader = new AsyncExchangeDBLoader(getContext(), true, "23.11.2017", "25.11.2017");
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
