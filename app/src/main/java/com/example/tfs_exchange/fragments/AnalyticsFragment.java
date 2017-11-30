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
import android.widget.EditText;

import com.example.tfs_exchange.R;
import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;
import com.example.tfs_exchange.api.FixerApi;
import com.example.tfs_exchange.api.FixerApiHelper;
import com.example.tfs_exchange.comparators.FavoriteComparator;
import com.example.tfs_exchange.comparators.LastUsedComparator;
import com.example.tfs_exchange.db.AsyncCurrencyDBLoader;
import com.example.tfs_exchange.model.Currency;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.Flowable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

/**
 * Created by pusya on 29.11.17.
 */

public class AnalyticsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Currency>> {

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private final static String TAG = "AnalyticsFragment";
    private static final int LOADER_ID = 3;

    private FavoriteComparator faveComp;
    private LastUsedComparator lastUsedComp;

    private Currency selectedCurrency;
    private CurrencyRecyclerListAdapter adapter;

    private FixerApi api;
    private Disposable rateSubscription;

    private List<Currency> currencies = new ArrayList<Currency>();

    private int days = 7;

    @BindView(R.id.analytics_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.graph)
    GraphView graph;

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
        final View analyticsFragmentRootView = inflater.inflate(R.layout.analytics_fragment, container, false);
        faveComp = new FavoriteComparator();
        lastUsedComp = new LastUsedComparator();
        ButterKnife.bind(this, analyticsFragmentRootView);
        api = new FixerApiHelper().createApi();
        adapter = new CurrencyRecyclerListAdapter(currencies, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                setFavorite(currency);
                subscribeRates(days, currency.getName());
                Log.d(TAG, currency.getName() + " was clicked");
                Log.d(TAG, generateDates(days).toString());
                //Log.d("Currency item ", " " + currency.getName() + " fave changed");
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 100),
                new DataPoint(1, 105),
                new DataPoint(2, 97)
        });
        subscribeRates(days, "RUB");

//        Log.d(TAG, String.valueOf(currencies.get(0).isSelected()));
        return analyticsFragmentRootView;

    }

    private void setFavorite(Currency currency) {
       for (Currency curr: currencies) {
           if (!curr.isFilter()) {

           } else {
               curr.setFilter(false);
           }
       }
        currency.setFilter(true);
        selectedCurrency = currency;
        Log.d(TAG, "selected currency: " + selectedCurrency.getName());
        adapter.notifyDataSetChanged();
    }

    private String[] generateDates(int days) {
        String[] dates  = new String[days];

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        for (int i = days-1; i >= 0; i--) {
            dates[i] = format.format(today);
            calendar.add(Calendar.DATE, -1);
            today = calendar.getTime();
            Log.d(TAG, dates[i]);
        }

        return dates;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
//        Log.d(TAG, String.valueOf(currencies.get(0).isSelected()));
    }

    void plotGraph(ArrayList<Float> list) {
        DataPoint[] dataPoints = new DataPoint[list.size()];
        String[] dates  = new String[days];

        Calendar calendar = Calendar.getInstance();

        Date today = calendar.getTime();
        for (int i = 0; i < list.size(); i++) {
            dataPoints[i] = new DataPoint(i, (double) list.get(i));
            calendar.add(Calendar.DATE, -1);
            today = calendar.getTime();
            Log.d(TAG, "Plotting " + dataPoints.toString());
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);


        // set date label formatter
        //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        //graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
        //graph.getViewport().setMinX(1);
        //graph.getViewport().setMaxX(list.size());
        //graph.getViewport().setMinY(list.get(0));
        //graph.getViewport().setMaxY(list.get(list.size()-1));

        graph.addSeries(series);

// set manual x bounds to have nice steps


        /**
        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d3.getTime());
         **/
        //graph.getViewport().setXAxisBoundsManual(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
        //graph.getGridLabelRenderer().setHumanRounding(false);
    }

    @Override
    public Loader<List<Currency>> onCreateLoader(int id, Bundle args) {
        Loader<List<Currency>> loader = null;
        if (id == LOADER_ID) {
            loader = new AsyncCurrencyDBLoader(getContext());
            Log.d(TAG, "onCreateLoader: " + loader.hashCode());
        }
        return loader;
    }

    private void subscribeRates(int days, String currencyName) {
        graph.removeAllSeries();
        rateSubscription = Flowable.fromArray(generateDates(days))
                .subscribeOn(Schedulers.io())
                .flatMapSingle(date -> (api.getRateByDate(date, currencyName, "EUR")))
                .reduce(new ArrayList<Float>(), (list, rate) -> {
                    list.add((float)rate.getRates().getRate());
                    Log.d(TAG, list.toString());
                    return list;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::plotGraph, throwable ->{
                    Log.d(TAG, "connection problems");
                });
    }

    @Override
    public void onLoadFinished(Loader<List<Currency>> loader, List<Currency> data) {
        for (Currency currency : data) {
            currency.setFilter(false);
            currencies.add(currency);
        }
        currencies.get(0).setFilter(true);
        selectedCurrency = currencies.get(0);
        Log.d(TAG, "onLoadFinished: " + loader.hashCode());
        sortCurrencies();
    }

    @Override
    public void onLoaderReset(Loader<List<Currency>> loader) {
        Log.d(TAG, "onLoaderReset for AsyncLoader " + loader.hashCode());
    }

    private void sortCurrencies() {
        Collections.sort(currencies, lastUsedComp);
        Collections.sort(currencies, faveComp);
        adapter.notifyDataSetChanged();
        Log.d(TAG, " sortCurrencies");
    }
}
