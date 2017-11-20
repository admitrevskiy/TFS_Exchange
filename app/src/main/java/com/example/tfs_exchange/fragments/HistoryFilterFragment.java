package com.example.tfs_exchange.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tfs_exchange.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pusya on 20.11.17.
 */

public class HistoryFilterFragment extends Fragment {

    private static final String TAG = "HistoryFilterFragment";

    private final String[] periods = {"все время", "неделя", "месяц", "выбрать даты"};

    private int period;

    @BindView(R.id.filter_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.period_spinner)
    Spinner periodSpinner;

    @BindView(R.id.date_from_spinner)
    Spinner dateFromSpinner;

    @BindView(R.id.date_to_spinner)
    Spinner dateToSpinner;

    private void disableDate() {
        dateFromSpinner.setEnabled(false);
        dateToSpinner.setEnabled(false);
        dateFromSpinner.setVisibility(View.GONE);
        dateToSpinner.setVisibility(View.GONE);
    }

    private void enableDate() {
        dateFromSpinner.setEnabled(true);
        dateToSpinner.setEnabled(true);
        dateFromSpinner.setVisibility(View.VISIBLE);
        dateToSpinner.setVisibility(View.VISIBLE);
        Log.d(TAG, "enableDate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View historyFilterFragmentRootView = inflater.inflate(R.layout.history_filter_fragment, container, false);

        ButterKnife.bind(this, historyFilterFragmentRootView);
        disableDate();
        ArrayAdapter<String> selectPeriodAdaper = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, periods);
        selectPeriodAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(selectPeriodAdaper);

        /** Переписать на butterKnife!**/
        AdapterView.OnItemSelectedListener periodSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)parent.getItemAtPosition(position);
                switch (item) {
                    case "все время":
                        period = 0;
                        Log.d(TAG, "period " + period);
                        disableDate();
                        break;
                    case "неделя":
                        period = 1;
                        Log.d(TAG, "period " + period);
                        disableDate();
                        break;
                    case "месяц":
                        period = 2;
                        Log.d(TAG, "period " + period);
                        disableDate();
                        break;
                    case "выбрать даты":
                        period = 3;
                        Log.d(TAG, "period " + period);
                        enableDate();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        periodSpinner.setOnItemSelectedListener(periodSelectedListener);

        return historyFilterFragmentRootView;
    }
}
