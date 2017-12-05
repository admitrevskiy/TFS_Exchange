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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.tfs_exchange.R;
import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;
import com.example.tfs_exchange.analytics.AnalyticsContract;
import com.example.tfs_exchange.analytics.AnalyticsPresenter;
import com.example.tfs_exchange.model.Currency;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pusya on 29.11.17.
 */

public class AnalyticsFragment extends Fragment implements AnalyticsContract.View {

    private final static String TAG = "AnalyticsFragment";

    private Currency selectedCurrency;
    private CurrencyRecyclerListAdapter adapter;

    private AnalyticsContract.Presenter mPresenter;

    private List<Currency> currencies = new ArrayList<Currency>();

    private int days = 7;

    @BindView(R.id.analytics_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.graph)
    GraphView graph;

    @BindView(R.id.choose_period)
    RadioGroup choosePeriodGroup;

    @BindView(R.id.week_button)
    RadioButton weekButton;

    @BindView(R.id.two_weeks_button)
    RadioButton twoWeeksButton;

    @BindView(R.id.month_button)
    RadioButton monthButton;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View analyticsFragmentRootView = inflater.inflate(R.layout.analytics_fragment, container, false);
        ButterKnife.bind(this, analyticsFragmentRootView);
        mPresenter = new AnalyticsPresenter(this);
        mPresenter.getCurrencies();
        mPresenter.getRates(days, "RUB");

        choosePeriodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.week_button:
                        Log.d(TAG, "week for " + selectedCurrency.getName());
                        days = 7;
                        mPresenter.getRates(days, selectedCurrency.getName());
                        break;
                    case R.id.two_weeks_button:
                        days = 14;
                        mPresenter.getRates(days, selectedCurrency.getName());
                        Log.d(TAG, "2 weeks for " + selectedCurrency.getName());
                        break;
                    case R.id.month_button:
                        days = 30;
                        mPresenter.getRates(days, selectedCurrency.getName());
                        Log.d(TAG, "month for " + selectedCurrency.getName());
                        break;
                    default:
                        break;
                }
            }

        });

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
                mPresenter.getRates(days, currency.getName());
                Log.d(TAG, currency.getName() + " was clicked");
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
    }

    @Override
    public Currency getSelectedCurrency() {
        return selectedCurrency;
    }

    @Override
    public void setFavorite(Currency currency) {
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

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void plotGraph(ArrayList<Float> list) {
        graph.removeAllSeries();
        DataPoint[] dataPoints = new DataPoint[list.size()];
        String[] dates  = new String[days];

        Calendar calendar = Calendar.getInstance();

        Date today = calendar.getTime();
        for (int i = 0; i < list.size(); i++) {
            dataPoints[i] = new DataPoint(i+1, (double) list.get(i));
            calendar.add(Calendar.DATE, -1);
            today = calendar.getTime();
            Log.d(TAG, "Plotting " + dataPoints.toString());
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);

        graph.addSeries(series);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(days);
        graph.onDataChanged(false, false);


        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
    }

    @Override
    public void refreshGraph(){
        graph.removeAllSeries();
    }

}
