package com.example.tfs_exchange.currency_select;

import android.util.Log;

import com.example.tfs_exchange.comparators.FavoriteComparator;
import com.example.tfs_exchange.comparators.LastUsedComparator;
import com.example.tfs_exchange.comparators.LongClickedComparator;
import com.example.tfs_exchange.model.Currency;

import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by pusya on 30.11.17.
 */

public class CurrencySelectPresenter implements CurrencyContract.Presenter {


    private static final String TAG = "CurrencySelectPresenter";

    //MVP
    private final CurrencyContract.Repository mRepository;
    private final CurrencyContract.View mView;

    //Список валют
    private List<Currency> currencies;

    //LongClicked валюта
    private Currency selectedCurrency;

    //Флаг
    private boolean noItemLongClicked;

    //Компараторы
    private FavoriteComparator faveComp = new FavoriteComparator();
    private LastUsedComparator lastUsedComp = new LastUsedComparator();
    private LongClickedComparator longClickedComp = new LongClickedComparator();

    public CurrencySelectPresenter(CurrencyContract.View mView) {
        this.mView = mView;
        mRepository = new CurrencyRepository();
    }

    //Загружаем валюты и отдамем их view
    @Override
    public void getCurrencies() {
        noItemLongClicked = true;
        selectedCurrency = null;
        //currencies = new ArrayList<>();
        if (currencies == null) {
            Disposable currencySubscription = mRepository.loadCurrencies()
                    .subscribe(this::showCurrencies, throwable -> {
                        Log.d(TAG, "problems, bro");
                    });
            Log.d(TAG, "loading from repository");
        } else {
            mView.setCurrencies(currencies);
            Log.d(TAG, "show actual currencies");
        }
    }

    //Передаем валюты view
    @Override
    public void showCurrencies(List<Currency> currencies) {
        if (currencies != null) {
            this.currencies = currencies;
            //sortCurrencies();
            mView.setAdapter(currencies);
            mView.setCurrencies(currencies);
        }
    }

    //Нажата звездочка
    @Override
    public void onFavoriteChanged(Currency currency) {
        //Меняем избранность валюты
        int fave;
        if (currency.isFavorite())
        {
            currency.setFavorite(false);
            fave = 0;
        } else {
            currency.setFavorite(true);
            fave = 1;
        }

        //Говорим репозиторию, что изменение избранности нужно записать в БД
        mRepository.setFaveToDB(currency, fave);

        //Сортируем валюты
        sortCurrencies();
    }


    @Override
    public void onCurrencyLongClicked(Currency currency) {
        Log.d("Currency item ", currency.getName() + " long clicked" );
        currency.setLongClicked(true);
        Collections.sort(currencies, longClickedComp);
        currency.setLongClicked(false);
        showCurrencies(currencies);
        noItemLongClicked = false;
        selectedCurrency = currency;
        //onTimeChanged(currency);
    }

    @Override
    public void onCurrencyClicked(Currency currency) {
        if (selectedCurrency != null) {
            Log.d(TAG, "selected currency: " + selectedCurrency.getName());
        }
        //onTimeChanged(currency);
        //Если ни одна валюта не LongClicked, выбираем вторую валюту по логике из ТЗ
        //Если уже выбрана LongClicked валюта, текущая валюта являтеся второй
        //Запускаем Exchange фрагмент
        if (noItemLongClicked) {

            mView.replaceByExchangeFragment(currency.getName(), getCurrencyForExchange(currency));
        }
        else {

            mView.replaceByExchangeFragment(selectedCurrency.getName(), currency.getName());
        }
    }

    //Получаем вторую валюту для обмена по ТЗ
    protected String getCurrencyForExchange(Currency selectedCurrency) {
        if (currencies != null) {
            for (Currency currency : currencies) {
                if (currency.isFavorite() && !currency.getName().equals(selectedCurrency.getName())) {
                    return currency.getName();
                } else if (!currency.isFavorite()) {
                    break;
                }
            }
        }
        if (selectedCurrency.getName().equals("USD")) {
            return "RUB";
        }
        return "USD";
    }

    //Сортируем избранные валюты вверх по списку - сначала по использованиям, потом по избранности
    private void sortCurrencies() {
        Collections.sort(currencies, lastUsedComp);
        Collections.sort(currencies, faveComp);
        //mView.setAdapter(currencies);
        mView.setCurrencies(currencies);
        Log.d(TAG, " sortCurrencies");
        Log.d(TAG, currencies.toString());
    }
}
