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
import com.example.tfs_exchange.analytics.AnalyticsContract;
import com.example.tfs_exchange.analytics.AnalyticsPresenter;
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

public class AnalyticsFragment extends Fragment implements AnalyticsContract.View {

    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private final static String TAG = "AnalyticsFragment";
    private static final int LOADER_ID = 3;

    private FavoriteComparator faveComp;
    private LastUsedComparator lastUsedComp;

    private Currency selectedCurrency;
    private CurrencyRecyclerListAdapter adapter;

    private AnalyticsContract.Presenter mPresenter;

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
       mPresenter = new AnalyticsPresenter(this);
       mPresenter.getCurrencies();
        mPresenter.getRates();

        //subscribeRates(days, "RUB");

//        Log.d(TAG, String.valueOf(currencies.get(0).isSelected()));
        return analyticsFragmentRootView;

    }

    private void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    @Override
    public void setAdapter(List<Currency> currencies) {
        setCurrencies(currencies);
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

    @Override
    public void plotGraph(ArrayList<Float> list) {
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

        graph.addSeries(series);
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

}
