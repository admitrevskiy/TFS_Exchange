package com.example.tfs_exchange.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.example.tfs_exchange.R;
import com.example.tfs_exchange.exchange.ExchangeContract;
import com.example.tfs_exchange.exchange.ExchangePresenter;
import com.example.tfs_exchange.model.Exchange;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pusya on 01.11.17.
 * Читать: https://github.com/JakeWharton/butterknife
 * Читать: https://github.com/JakeWharton/butterknife/issues/672
 *
 */

public class ExchangeFragment extends Fragment implements ExchangeContract.View {
    private static final String TAG = "ExchangeFragment";
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy '\n' HH:mm:ss");

    private String currencyFrom;
    private String currencyTo;

    private ExchangeContract.Presenter mPresenter;

    private double rate;

    @BindView(R.id.currency_from_name)
    TextView currencyFromName;

    @BindView(R.id.currency_to_name)
    TextView currencyToName;

    @BindView(R.id.currency_from_edit)
    EditText currencyAmountFromEdit;

    @BindView(R.id.currency_to_edit)
    EditText currencyAmountToEdit;

    @BindView(R.id.exchange_button)
    Button exchangeButton;

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

    //Слушатель нажатия на кнопку из ButterKnife
    @OnClick(R.id.exchange_button)
    void onSaveClick() {
        mPresenter.sendExchange();
        Log.d(TAG, " button clicked");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View exchangeFragmentRootView = inflater.inflate(R.layout.exchange_fragment, container, false);
        ButterKnife.bind(this, exchangeFragmentRootView);
        disactivateRate();
        mPresenter = new ExchangePresenter(this);
        mPresenter.getCurrenciesAndRate(getArguments());

        Log.d(TAG, "onCreateView");
        return exchangeFragmentRootView;
    }

    //Устанавливаем в поля значение и курса и единицу для базовой валюты
    @Override
    public void activateRate (double rate) {
        currencyAmountFromEdit.setText("1.00");
        currencyAmountFromEdit.setEnabled(true);
        currencyAmountToEdit.setText(String.valueOf(rate) + " ");
        currencyAmountToEdit.setEnabled(true);
        exchangeButton.setText("ОБМЕНЯТЬ");
        exchangeButton.setEnabled(true);
        setRate(rate);
        Log.d(TAG, "rate activated");
    }

    @Override
    public void disactivateRate() {
        currencyAmountFromEdit.setEnabled(false);
        currencyAmountToEdit.setEnabled(false);
        exchangeButton.setEnabled(false);
        exchangeButton.setText("No connection");
    }

    //Отписка происходит в onDetach
    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.unsubscribeRate();
        Log.d(TAG, "onDetach()");
    }

    @Override
    public void setCurrencies(String currencyFrom, String currencyTo)  {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        currencyFromName.setText(currencyFrom);
        currencyToName.setText(currencyTo);
    }

    @Override
    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public Exchange getExchange() {
        Date dateNow = new Date();
        long millis = dateNow.getTime()/1000;
        String[] dateAndTime = dateFormat.format(dateNow).split("\n");
        double amountFrom = Double.parseDouble(String.valueOf(currencyAmountFromEdit.getText()));
        double amountTo = Double.parseDouble(String.valueOf(currencyAmountToEdit.getText()));
        return new Exchange(currencyFrom, currencyTo, amountFrom, amountTo, rate, dateAndTime[0], dateAndTime[1], millis);
    }
}
