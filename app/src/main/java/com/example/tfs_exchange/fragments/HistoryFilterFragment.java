package com.example.tfs_exchange.fragments;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tfs_exchange.R;
import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.history_filter.HistoryFilterContract;
import com.example.tfs_exchange.history_filter.HistoryFilterPresenter;
import com.example.tfs_exchange.model.Currency;
import com.example.tfs_exchange.model.Settings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pusya on 20.11.17.
 */

public class HistoryFilterFragment extends Fragment implements HistoryFilterContract.View {

    private static final String TAG = "HistoryFilterFragment";

    private final String[] periods = {"все время", "неделя", "месяц", "выбрать"};

    private CurrencyRecyclerListAdapter adapter;
    private List<Currency> currencies = new ArrayList<Currency>();
    private int mYear, mMonth, mDay;

    private HistoryFilterContract.Presenter mPresenter;

    @BindView(R.id.filter_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.period_spinner)
    Spinner periodSpinner;

    @BindView(R.id.date_from_edit)
    TextView dateFromEdit;

    @BindView(R.id.date_to_edit)
    TextView dateToEdit;

    @BindView(R.id.save_filter)
    android.support.design.widget.FloatingActionButton saveFilter;

    //@OnClick (R.id.save_filter)
    //public void setSettings() {
        //saveFilter();
    //}


    private void disableDate() {
        dateFromEdit.setEnabled(false);
        dateToEdit.setEnabled(false);
        dateFromEdit.setVisibility(View.GONE);
        dateToEdit.setVisibility(View.GONE);
    }

    private void enableDate() {
        dateFromEdit.setEnabled(true);
        dateToEdit.setEnabled(true);
        dateFromEdit.setVisibility(View.VISIBLE);
        dateToEdit.setVisibility(View.VISIBLE);
        Log.d(TAG, "enableDate");
    }

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View historyFilterFragmentRootView = inflater.inflate(R.layout.history_filter_fragment, container, false);

        ButterKnife.bind(this, historyFilterFragmentRootView);
        disableDate();
        Log.d(TAG, currencies.toString());

        mPresenter = new HistoryFilterPresenter(this);



        mPresenter.getCurrencies();
        //setAdapter(currencies);
        setSpinner();


        saveFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onSaveSettings();
            }
        });

        Log.d(TAG, " onCreateView" + this.hashCode());
        return historyFilterFragmentRootView;
    }

    @Override
    public void setAdapter(List<Currency> currencies) {
        adapter = new CurrencyRecyclerListAdapter(currencies, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                mPresenter.onCurrencyClicked(currency);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
    }

    @Override
    public void setCurrencies(List<Currency> currencies) {
        //this.currencies = currencies;
        adapter.notifyDataSetChanged();
    }

    private void setSpinner() {
        //Spinner для выбора периода
        ArrayAdapter<String> selectPeriodAdaper = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, periods);
        selectPeriodAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(selectPeriodAdaper);

        dateFromEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDatePicker(dateFromEdit);
                Log.d(TAG, "date from");
            }
        });

        dateToEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDatePicker(dateToEdit);
                Log.d(TAG, "date to");
            }
        });

        /** Переписать на butterKnife!**/
        AdapterView.OnItemSelectedListener periodSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)parent.getItemAtPosition(position);
                switch (item) {
                    case "все время":
                        disableDate();
                        break;
                    case "неделя":
                        disableDate();
                        break;
                    case "месяц":
                        disableDate();
                        break;
                    case "выбрать":
                        enableDate();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        periodSpinner.setOnItemSelectedListener(periodSelectedListener);

    }

    private void callDatePicker(TextView textView) {
        // получаем текущую дату
        final Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        // инициализируем диалог выбора даты текущими значениями
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    String editTextDateParam = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                    textView.setText(editTextDateParam);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public int getPeriodId() {
        return periodSpinner.getSelectedItemPosition();
    }

    @Override
    public String getDateFrom() {
        return dateFromEdit.getText().toString();
    }

    @Override
    public String getDateTo() {
       return dateToEdit.getText().toString();
    }

    @Override
    public void popBackStack() {
        getFragmentManager().popBackStack();
    }
}

