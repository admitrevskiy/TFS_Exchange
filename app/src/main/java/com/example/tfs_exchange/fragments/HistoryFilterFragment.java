package com.example.tfs_exchange.fragments;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tfs_exchange.R;
import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;
import com.example.tfs_exchange.db.AsyncCurrencyDBLoader;
import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.model.Currency;

import java.util.ArrayList;
import java.util.Calendar;
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

public class HistoryFilterFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Currency>>{

    private static final String TAG = "HistoryFilterFragment";

    private final String[] periods = {"все время", "неделя", "месяц", "выбрать даты"};
    private int period;
    private final int LOADER_ID = 3;
    private DBHelper dbHelper;
    private ContentValues cv;
    SQLiteDatabase db;
    SharedPreferences settings;
    private CurrencyRecyclerListAdapter adapter;
    private List<Currency> currencies = new ArrayList<Currency>();
    private Set<Currency> filterCurrencies = new HashSet<>();
    private Set<String> savedCurrencies;
    private int mYear, mMonth, mDay;
    private ToastHelper toastHelper;

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

    @OnClick (R.id.save_filter)
    public void setSettings() {
        saveFilter();
    }


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
        /** Загрузка валют из БД происходит асинхронно **/
        getLoaderManager().initLoader(LOADER_ID, null, this);
        Loader<Object> loader = getLoaderManager().getLoader(LOADER_ID);
        loader.forceLoad();
        /** ------------------------------------------**/
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View historyFilterFragmentRootView = inflater.inflate(R.layout.history_filter_fragment, container, false);

        ButterKnife.bind(this, historyFilterFragmentRootView);
        disableDate();
        Log.d(TAG, currencies.toString());

        toastHelper = new ToastHelper();
        settings = this.getActivity().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);


        //RecyclerView
        adapter = new CurrencyRecyclerListAdapter(currencies, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                setFaveToDB(currency);
                //Log.d("Currency item ", " " + currency.getName() + " fave changed");
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

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


        saveFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFilterCurrencies();
                if (filterCurrencies.size() > 0)
                {
                    saveFilter();
                    Log.d(TAG, "fab is clicked \n" + getFilterCurrencies().toString());
                }

            }
        });

        Log.d(TAG, " onCreateView" + this.hashCode());
        return historyFilterFragmentRootView;
    }

    @Override
    public Loader<List<Currency>> onCreateLoader(int id, Bundle args) {
        Loader<List<Currency>> loader = null;
        if (id == LOADER_ID) {
            loader = new AsyncCurrencyDBLoader(getContext(), true);
            Log.d(TAG, "onCreateLoader: " + loader.hashCode());
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Currency>> loader, List<Currency> data) {
        Log.d(TAG, data.toString());
        for (Currency currency : data) {
            currencies.add(currency);
        }
        Log.d(TAG, "onLoadFinished: " + loader.hashCode());
        //sortCurrencies();
    }

    @Override
    public void onLoaderReset(Loader<List<Currency>> loader) {
        Log.d(TAG, "onLoaderReset for AsyncLoader " + loader.hashCode());
    }

    //Записываем в БД изменение избранности валюты
    private void setFaveToDB(Currency currency) {
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
        int changeFilter;
        if (currency.isFilter())
        {
            currency.setFilter(false);
            changeFilter = 0;
        } else {
            currency.setFilter(true);
            changeFilter = 1;
        }
        cv.put("FILTER", changeFilter);
        db.update("currency_name", cv, "currency_base = ?", new String[] {currency.getName()});
        Log.d(TAG, " " + currency.getName() + " filter changed" );
        //sortCurrencies();
        db.close();
        adapter.notifyDataSetChanged();
    }

    private Set<String> getFilterCurrencies() {
        savedCurrencies = new HashSet<>();
        if (currencies.size() > 0) {
            for (Currency currency : currencies) {
                if (currency.isFilter()) {
                    filterCurrencies.add(currency);
                }
            }
        }

        //Чтобы избежать ConcurrentModificationException приходится явно использовать итератор
        for (Iterator<Currency> iterator = filterCurrencies.iterator(); iterator.hasNext();) {
            Currency currency = iterator.next();
            if (!currency.isFilter()) {
                iterator.remove();
            }
        }

        for (Iterator<Currency> iterator = filterCurrencies.iterator(); iterator.hasNext();) {
            Currency currency = iterator.next();
            savedCurrencies.add(currency.getName());
        }
        return savedCurrencies;
    }

    protected void saveFilter() {
        if (periodSpinner.getSelectedItemPosition() == 3 && (dateFromEdit.getText().equals(getString(R.string.date_from)) || dateToEdit.getText().equals(getString(R.string.date_to)))) {
            toastHelper.showToast(getActivity(), getString(R.string.insert_date_message));
        } else if (periodSpinner.getSelectedItemPosition() == 3 && dateFromEdit.getText().toString().compareTo(dateToEdit.getText().toString()) > 0) {
            toastHelper.showToast(getActivity(), getString(R.string.wrong_date_message));
        } else {
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.putInt(getString(R.string.period_id), periodSpinner.getSelectedItemPosition());
            if (savedCurrencies.size() > 0) {
                editor.putStringSet(getString(R.string.currencies), savedCurrencies);
            }
            if (periodSpinner.getSelectedItemPosition() == 3) {
                editor.putString(getString(R.string.saved_date_from), dateFromEdit.getText().toString());
                editor.putString(getString(R.string.saved_date_to), dateToEdit.getText().toString());
            }

            editor.apply();
            Log.d(TAG, "SharedPrefs was saved " + periodSpinner.getSelectedItemPosition() + " " + savedCurrencies.toString());
        }
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
}
