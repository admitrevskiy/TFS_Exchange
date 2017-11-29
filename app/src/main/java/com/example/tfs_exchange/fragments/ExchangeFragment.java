package com.example.tfs_exchange.fragments;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.example.tfs_exchange.db.DBHelper;
import com.example.tfs_exchange.api.FixerApiHelper;
import com.example.tfs_exchange.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pusya on 01.11.17.
 * Читать: https://github.com/JakeWharton/butterknife
 * Читать: https://github.com/JakeWharton/butterknife/issues/672
 *
 */

public class ExchangeFragment extends Fragment {
    private static final String TAG = "ExchangeFragment";
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy '\n' HH:mm:ss");

    private String currencyFrom;
    private String currencyTo;

    private ContentValues cv;
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private double amountFrom;
    private double amountTo;
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
        amountFrom = Double.parseDouble(String.valueOf(currencyAmountFromEdit.getText()));
        amountTo = Double.parseDouble(String.valueOf(currencyAmountToEdit.getText()));
        setExchangeToDB();
//        dbHelper.setExchangeToDB(currencyFrom, currencyTo, amountFrom, amountTo, rate);
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
        //Получаем Bundle от вызвавшего фрагмента и достаем из него информацию
        Bundle incomingBundle = getArguments();
        if (incomingBundle!= null) {
            currencyFrom = incomingBundle.getStringArray("currencies")[0];
            currencyTo = incomingBundle.getStringArray("currencies")[1];

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
        currencyAmountFromEdit.setEnabled(true);
        currencyAmountToEdit.setText(String.valueOf(rate) + " ");
        currencyAmountToEdit.setEnabled(true);
        exchangeButton.setText("ОБМЕНЯТЬ");
        exchangeButton.setEnabled(true);
        Log.d(TAG, "rate activated");
    }

    private void disactivateRate() {
        currencyAmountFromEdit.setEnabled(false);
        currencyAmountToEdit.setEnabled(false);
        exchangeButton.setEnabled(false);
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
                    if (rate != 0) {
                        activateRate(rate);

                    } else {
                        Log.d(TAG, " some problems with loading rates");
                    }

                }, throwable -> {
                    Log.d(TAG, " connection problems");
                    exchangeButton.setText("No connection");
                });
        Log.d(TAG, "Subscribe");
    }

    //Отписка
    private void unsubscribeRate() {
        if (rateSubscription != null) rateSubscription.dispose();
        Log.d(TAG, "unsubscribe from rate");
    }

    //Отписка происходит в onDetach
    @Override
    public void onDetach() {
        super.onDetach();
        unsubscribeRate();
        Log.d(TAG, "onDetach()");
    }

    /** перенести в DBHelper **/
    private void setExchangeToDB() {
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
        Date dateNow = new Date();
        long millis = dateNow.getTime()/1000;
        String[] dateAndTime = dateFormat.format(dateNow).split("\n");
        cv.put("EXCHANGE_BASE", currencyFrom);
        cv.put("EXCHANGE_BASE_AMOUNT", Double.parseDouble(String.valueOf(currencyAmountFromEdit.getText())));
        cv.put("EXCHANGE_SYMBOLS", currencyTo);
        cv.put("EXCHANGE_SYMBOLS_AMOUNT", Double.parseDouble(String.valueOf(currencyAmountToEdit.getText())));
        cv.put("EXCHANGE_RATE", rate);
        cv.put("EXCHANGE_DATE", dateAndTime[0]);
        cv.put("EXCHANGE_TIME", dateAndTime[1]);
        cv.put("EXCHANGE_MILLIS", millis);
        db.insert("exchange_name", null, cv);
        db.close();
    }
}
