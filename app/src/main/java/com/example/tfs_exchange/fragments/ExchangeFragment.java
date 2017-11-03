package com.example.tfs_exchange.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.tfs_exchange.R;

/**
 * Created by pusya on 01.11.17.
 */

public class ExchangeFragment extends Fragment {

    @BindView(R.id.currency_from_name)
    TextView currencyFromName;

    @BindView(R.id.currency_to_name)
    TextView currencyToName;

    @BindView(R.id.currency_from_edit)
    EditText currencyFromEdit;

    @BindView(R.id.currency_to_edit)
    EditText currencyToEdit;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View exchangeFragmentRootView = inflater.inflate(R.layout.exchange_fragment, container, false);
        ButterKnife.bind(this, exchangeFragmentRootView);
        //Получаем Bundle от вызвавшего фрагмента и достаем из него информацию
        Bundle incomingBundle = getArguments();
        try {
            final String currencyFrom = incomingBundle.getStringArray("currencies")[0];
            final String currencyTo = incomingBundle.getStringArray("currencies")[1];
            currencyFromName.setText(currencyFrom);
            currencyToName.setText(currencyTo);
        } catch (NullPointerException e) {

        }

        return exchangeFragmentRootView;
    }
}
