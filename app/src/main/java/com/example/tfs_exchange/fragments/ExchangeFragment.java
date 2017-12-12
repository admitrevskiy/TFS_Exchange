package com.example.tfs_exchange.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

import com.example.tfs_exchange.R;
import com.example.tfs_exchange.exchange.ExchangeContract;
import com.example.tfs_exchange.exchange.ExchangePresenter;

/**
 * Created by pusya on 01.11.17.
 * Читать: https://github.com/JakeWharton/butterknife
 * Читать: https://github.com/JakeWharton/butterknife/issues/672
 *
 */

public class ExchangeFragment extends Fragment implements ExchangeContract.View {
    private static final String TAG = "ExchangeFragment";

    private FragmentManager fragmentManager;

    private Unbinder unbinder;

    private static ExchangeContract.Presenter mPresenter;

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

    @BindView(R.id.exchange_progress_bar)
    ProgressBar progressBar;

    //Слушатель изменения в текстовом поле from
    @OnTextChanged(value = R.id.currency_from_edit, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void fromCurrencyAmountChanged(Editable s) {
        mPresenter.onAmountFromEdit(s, currencyAmountFromEdit);
    }

    //Слушатель изменения в текстовом поле to
    @OnTextChanged(value = R.id.currency_to_edit, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void toCurrencyAmountChanged(Editable s) {
        mPresenter.onAmountToEdit(s, currencyAmountToEdit);
    }

    //Слушатель нажатия на кнопку из ButterKnife
    @OnClick(R.id.exchange_button)
    void onSaveClick() {
        mPresenter.onExchange();
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
        Log.d(TAG, " button clicked");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View exchangeFragmentRootView = inflater.inflate(R.layout.exchange_fragment, container, false);
        unbinder = ButterKnife.bind(this, exchangeFragmentRootView);
        disactivateRate();
        mPresenter = new ExchangePresenter(this);
        mPresenter.getCurrenciesAndRate(getArguments());
        fragmentManager = getFragmentManager();
        Log.d(TAG, "onCreateView");
        return exchangeFragmentRootView;
    }

    //Устанавливаем в поля значение и курса и единицу для базовой валюты
    @Override
    public void activateRate (double rate) {
        progressBar.setVisibility(View.GONE);
        currencyAmountFromEdit.setText("1.0000");
        currencyAmountFromEdit.setEnabled(true);
        currencyAmountToEdit.setText(String.format("%.4f", rate));
        currencyAmountToEdit.setEnabled(true);
        exchangeButton.setText("ОБМЕНЯТЬ");
        exchangeButton.setEnabled(true);
        setRate(rate);
        Log.d(TAG, "rate activated");
    }

    @Override
    public void activateRate(double rate, double amountFrom) {
        progressBar.setVisibility(View.GONE);
        currencyAmountFromEdit.setText(String.valueOf(amountFrom));
        currencyAmountFromEdit.setEnabled(true);
        currencyAmountToEdit.setText(String.format("%.4f", amountFrom*rate));
        currencyAmountToEdit.setEnabled(true);
        exchangeButton.setText("ОБМЕНЯТЬ");
        exchangeButton.setEnabled(true);
        setRate(rate);
        Log.d(TAG, "rate activated");
    }

    @Override
    public void disactivateRate() {
        progressBar.setVisibility(View.VISIBLE);
        currencyAmountFromEdit.setEnabled(false);
        currencyAmountToEdit.setEnabled(false);
        exchangeButton.setEnabled(false);
        exchangeButton.setText("No connection");
    }

    //Отписка происходит в onDetach
    @Override
    public void onDetach() {
        mPresenter.onDetach();
        unbinder.unbind();
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }

    @Override
    public void setCurrencies(String currencyFrom, String currencyTo)  {
        currencyFromName.setText(currencyFrom);
        currencyToName.setText(currencyTo);
    }

    @Override
    public double getAmountFrom() {
        return Double.parseDouble(currencyAmountFromEdit.getText().toString());
    }

    @Override
    public double getAmountTo () {
        return Double.parseDouble(currencyAmountToEdit.getText().toString());
    }

    @Override
    public void setRate(double rate) {
        this.rate = rate;
    }

    public static class ExchangeDialogFragment extends DialogFragment {

        private static final String TAG = "ExchangeDialog";

        String message = "";

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)  {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(getString(R.string.dialog_message));
            builder.setMessage(message);
            builder.setPositiveButton(getString(R.string.ok_message), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPresenter.onExchange();
                    Log.d(TAG, "OK");
                }
            });

            builder.setNegativeButton(getString(R.string.abort_message), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, "not Ok");
                }
            });
            builder.setCancelable(true);

            Log.d(TAG, "created");
            return builder.create();
        }

        private void setMessage(String message) {
            this.message = message;
        }
    }

    @Override
    public void showDialog(String message) {
        FragmentManager manager = getFragmentManager();
        ExchangeDialogFragment myDialogFragment = new ExchangeDialogFragment();
        myDialogFragment.setMessage(message);
        myDialogFragment.show(manager, ExchangeDialogFragment.TAG);
    }

    @Override
    public void setCurrencyAmountFromEdit(String text) {
        currencyAmountFromEdit.setText(text);
    }

    @Override
    public void setCurrencyAmountToEdit(String text) {
        currencyAmountToEdit.setText(text);
    }

    @Override
    public void onPause() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(TAG);
        super.onPause();
    }



}
