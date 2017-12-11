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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.tfs_exchange.R;
import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;
import com.example.tfs_exchange.analytics.AnalyticsContract;
import com.example.tfs_exchange.analytics.AnalyticsPresenter;
import com.example.tfs_exchange.model.Currency;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by pusya on 29.11.17.
 */

public class AnalyticsFragment extends Fragment implements AnalyticsContract.View {

    private final static String TAG = "AnalyticsFragment";

    //ButterKnife
    private Unbinder unbinder;

    //Адаптер
    private CurrencyRecyclerListAdapter adapter;

    //MVP
    private AnalyticsContract.Presenter mPresenter;

    //Период
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

    @BindView(R.id.graph_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.error_text_view)
    TextView errorTextView;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View analyticsFragmentRootView = inflater.inflate(R.layout.analytics_fragment, container, false);
        unbinder = ButterKnife.bind(this, analyticsFragmentRootView);
        mPresenter = new AnalyticsPresenter(this);
        mPresenter.getCurrencies();

        choosePeriodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.week_button:
                        days = 7;
                        mPresenter.onPeriodChanged();
                        break;
                    case R.id.two_weeks_button:
                        days = 14;
                        mPresenter.onPeriodChanged();
                        break;
                    case R.id.month_button:
                        days = 30;
                        mPresenter.onPeriodChanged();
                        break;
                    default:
                        break;
                }
            }

        });

        return analyticsFragmentRootView;

    }

    @Override
    public void setAdapter(List<Currency> currencies) {
        adapter = new CurrencyRecyclerListAdapter(currencies, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                mPresenter.setFavorite(currency);
                mPresenter.getRates();
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
    public int getDays() {
        return days;
    }

    @Override
    public void showProgress() {
        recyclerView.setVisibility(View.GONE);
        choosePeriodGroup.setVisibility(View.GONE);
        graph.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        errorTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        choosePeriodGroup.setVisibility(View.VISIBLE);
        graph.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void handleError() {
        errorTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        choosePeriodGroup.setVisibility(View.VISIBLE);
        graph.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void refreshCurrencyList(List<Currency> currencies) {
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void plotGraph(LineGraphSeries<DataPoint> series) {
        graph.removeAllSeries();
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

    @Override
    public void onDetach() {
        super.onDetach();
        unbinder.unbind();
    }
}
