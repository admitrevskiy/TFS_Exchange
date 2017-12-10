package com.example.tfs_exchange.history;

import android.content.res.Resources;
import android.util.Log;

import com.example.tfs_exchange.ExchangerApp;
import com.example.tfs_exchange.R;
import com.example.tfs_exchange.exchange.ExchangeContract;
import com.example.tfs_exchange.model.Exchange;
import com.example.tfs_exchange.model.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import io.reactivex.disposables.Disposable;

/**
 * Created by pusya on 30.11.17.
 */

public class HistoryPresenter implements HistoryContract.Presenter {

    private static final String TAG = "HistoryPresenter";

    //MVP
    private HistoryContract.View mView;
    private HistoryContract.Repository mRepository;

    //Настройки
    private Settings settings;
    private String currencyMessage;
    private int periodId;
    private long dateFromMillis, dateToMillis;
    private Set<String> currencyFilter;
    private Resources resources = ExchangerApp.getAppResources();

    //Формат даты
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    //Конструктор
    public HistoryPresenter(HistoryContract.View mView) {
        this.mView = mView;
        mRepository = new HistoryRepository();
    }

    //Загружаем валюты
    @Override
    public void getHistory() {
        Disposable historySubscription = mRepository.loadHistory()
                .subscribe(this::showHistory, throwable -> {
                    Log.d(TAG, "problems, bro");
                });
    }

    //Открываем фрагмент с фильтрами
    @Override
    public void onFilterButtonClicked() {
        mView.replaceByFilterFragment();
    }

    //Передаем список обменов view
    private void showHistory(List<Exchange> exchanges) {
        mView.setAdapter(exchanges);
        getAndSetSettings();
    }

    //Запрашиваем сохраненные настройки у репозитория, составляем сообщение о выбранных настройках и передаём view
    private void getAndSetSettings() {
        settings = mRepository.loadSettings();
        periodId = settings.getPeriod_id();
        currencyFilter = settings.getCurrencies();
        dateFromMillis = settings.getDateFrom();
        dateToMillis = settings.getDateTo();
        mView.setFilterText(makeMessage());
    }

    //Составляем сообщение о настройках
    private String makeMessage() {
        if (currencyFilter != null && currencyFilter.size()>0) {
            currencyMessage = currencyFilter.toString().substring(1, currencyFilter.toString().length()-1);
        }
        if (periodId == 0) {
            if (currencyFilter == null || currencyFilter.size() == 0) {
                return resources.getString(R.string.no_filter);
            } else {
                return resources.getString(R.string.period) + ": " + resources.getString(R.string.all_time)+ "\nдля " + currencyMessage;
            }
        } else if (periodId ==1) {
            if (currencyFilter == null || currencyFilter.size() == 0) {
                return resources.getString(R.string.period) + ": " + resources.getString(R.string.week);
            } else {
                return resources.getString(R.string.period) + ": " + resources.getString(R.string.week)+ "\nдля " + currencyMessage;
            }
        } else if (periodId == 2) {
            if (currencyFilter == null || currencyFilter.size() == 0) {
                return resources.getString(R.string.period) + ": " + resources.getString(R.string.month);
            } else {
                return resources.getString(R.string.period) + ": " + resources.getString(R.string.month)+ "\nдля " + currencyMessage;
            }
        } else {
            Date dateFrom = new Date(dateFromMillis*1000);
            Date dateTo = new Date(dateToMillis*1000);
            if (currencyFilter == null || currencyFilter.size() == 0) {
                return "C " + dateFormat.format(dateFrom) + "\nпо " + dateFormat.format(dateTo);
            } else {
                return "С " + dateFormat.format(dateFrom) + "\nпо " + dateFormat.format(dateTo) + "\n для " + currencyMessage;
            }
        }
    }

}
