package com.example.tfs_exchange.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.example.tfs_exchange.FixerApiHelper;
import com.example.tfs_exchange.R;

/**
 * Created by pusya on 01.11.17.
 * Читать: https://github.com/JakeWharton/butterknife
 * Читать: https://github.com/JakeWharton/butterknife/issues/672
 *
 */

public class ExchangeFragment extends Fragment {
    private static final String TAG = "ExchangeFragment";

    private double rate;

    private Disposable rateSubscription;

    @BindView(R.id.currency_from_name)
    TextView currencyFromName;

    @BindView(R.id.currency_to_name)
    TextView currencyToName;

    @BindView(R.id.currency_from_edit)
    EditText currencyAmountFromEdit;

    @BindView(R.id.currency_to_edit)
    EditText currencyAmountToEdit;

    //Слушатель изменения в текстовом поле from
    @OnTextChanged(value = R.id.currency_from_edit, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void fromCurrencyAmountChanged(Editable s) {
        if (!s.toString().equals("") && currencyAmountFromEdit.hasFocus()) {
            currencyAmountToEdit.setText(String.valueOf(Double.parseDouble(s.toString()) * rate));
        }
    }

    //Слушатель изменения в текстовом поле to
    @OnTextChanged(value = R.id.currency_to_edit, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void toCurrencyAmountChanged(Editable s) {
        if (!s.toString().equals("") && currencyAmountToEdit.hasFocus()) {
            currencyAmountFromEdit.setText(String.valueOf(Double.parseDouble(s.toString()) / rate));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View exchangeFragmentRootView = inflater.inflate(R.layout.exchange_fragment, container, false);
        ButterKnife.bind(this, exchangeFragmentRootView);
        //Получаем Bundle от вызвавшего фрагмента и достаем из него информацию
        Bundle incomingBundle = getArguments();
        if (incomingBundle!= null) {
            final String currencyFrom = incomingBundle.getStringArray("currencies")[0];
            final String currencyTo = incomingBundle.getStringArray("currencies")[1];

            //Устанавливаем имена валют
            currencyFromName.setText(currencyFrom);
            currencyToName.setText(currencyTo);

            //С помощью RxJava подписываемся на курс
            subscribeRate(currencyFrom, currencyTo);
        }

        Log.d(TAG, "onCreateView");
        return exchangeFragmentRootView;
    }

    //Устанавливаем в поля значение и курса и единицу для базовой валюты
    private void activateRate (double rate) {
        currencyAmountFromEdit.setText("1.00");
        currencyAmountToEdit.setText(String.valueOf(rate) + " ");
        Log.d(TAG, "rate activated");
    }

    //Подписка, используем RetroLambda, RxJava, Retrofit настраиваем в gradle
    private void subscribeRate(String currencyFrom, String currencyTo) {
        rateSubscription = new FixerApiHelper()
                .createApi()
                .latest(currencyFrom, currencyTo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(apiResponse -> {
                    rate = apiResponse.getRates().getRate();
                    activateRate(rate);
                });
        Log.d(TAG, "Subscribe");
    }

    //Отписка
    private void unsubscribeRate() {
        if (rateSubscription != null) rateSubscription.dispose();
        Log.d(TAG, "unsubscribe from rate");
    }

    //Отписка в onDetach
    @Override
    public void onDetach() {
        super.onDetach();
        unsubscribeRate();
        Log.d(TAG, "onDetach()");
    }
}
