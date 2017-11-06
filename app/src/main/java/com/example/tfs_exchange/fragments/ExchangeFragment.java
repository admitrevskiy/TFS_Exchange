package com.example.tfs_exchange.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import com.example.tfs_exchange.R;

/**
 * Created by pusya on 01.11.17.
 * Читать: https://github.com/JakeWharton/butterknife
 * Читать: https://github.com/JakeWharton/butterknife/issues/672
 *
 */

public class ExchangeFragment extends Fragment {
    private static final String TAG = "ExchangeFragment";

    @BindView(R.id.currency_from_name)
    TextView currencyFromName;

    @BindView(R.id.currency_to_name)
    TextView currencyToName;

    @BindView(R.id.currency_from_edit)
    EditText currencyAmountFromEdit;

    @BindView(R.id.currency_to_edit)
    EditText currencyAmountToEdit;

    @OnTextChanged(value = R.id.currency_from_edit, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void fromCurrencyAmountChanged(Editable s) {
        if (!s.toString().equals("") && currencyAmountFromEdit.hasFocus()) {
            currencyAmountToEdit.setText(String.valueOf(Double.parseDouble(s.toString()) * 2));
        }
    }

    @OnTextChanged(value = R.id.currency_to_edit, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void toCurrencyAmountChanged(Editable s) {
        if (!s.toString().equals("") && currencyAmountToEdit.hasFocus()) {
            currencyAmountFromEdit.setText(String.valueOf(Double.parseDouble(s.toString()) / 2));
        }
    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View exchangeFragmentRootView = inflater.inflate(R.layout.exchange_fragment, container, false);
        ButterKnife.bind(this, exchangeFragmentRootView);
        //Получаем Bundle от вызвавшего фрагмента и достаем из него информацию
        Bundle incomingBundle = getArguments();
        if (incomingBundle!= null) {
            final String currencyFrom = incomingBundle.getStringArray("currencies")[0];
            final String currencyTo = incomingBundle.getStringArray("currencies")[1];
            currencyFromName.setText(currencyFrom);
            currencyToName.setText(currencyTo);
        }

        return exchangeFragmentRootView;
    }
}
