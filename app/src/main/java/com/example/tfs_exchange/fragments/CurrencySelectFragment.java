package com.example.tfs_exchange.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.tfs_exchange.currency_select.CurrencyContract;
import com.example.tfs_exchange.currency_select.CurrencySelectPresenter;
import com.example.tfs_exchange.model.Currency;
import com.example.tfs_exchange.R;
import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by pusya on 27.10.17.
 * Заметка от 9.11: анонимные внутренние классы переписать на лямбдах
 */

public class CurrencySelectFragment extends Fragment implements CurrencyContract.View {

    private final static String TAG = "CurrencySelectFragment";

    //MVP
    private CurrencyContract.Presenter mPresenter;

    //Менеджер фрагментов
    private FragmentManager fragmentManager;

    private Unbinder unbinder;

    private CurrencyRecyclerListAdapter adapter;

    @BindView(R.id.currency_recycler_view)
    RecyclerView recyclerView;


    @Nullable
    @Override
    public void onPause() {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(TAG);
        super.onPause();
        Log.d(TAG, " onPause");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View firstFragmentRootView = inflater.inflate(R.layout.currency_select_fragment, container, false);
        unbinder = ButterKnife.bind(this, firstFragmentRootView);

        fragmentManager = getFragmentManager();

        mPresenter = new CurrencySelectPresenter(this);
        mPresenter.getCurrencies();

        Log.d(TAG, " onCreateView" + this.hashCode());

        return firstFragmentRootView;
    }

    @Override
    public void setAdapter(List<Currency> currencies) {
        adapter = new CurrencyRecyclerListAdapter(currencies, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                Log.d(TAG, "Currency item " + currency.getName() + " fave changed");

                //Сообщаем презентеру, что у валюты нажата звездочка
                mPresenter.onFavoriteChanged(currency);
            }

        }, new CurrencyRecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Currency currency) {
                Log.d("Currency item ", " " + currency.getName() + " short clicked");

                //Сообщаем презентеру, что валюта нажата
                mPresenter.onCurrencyClicked(currency);
            }
        }, new  CurrencyRecyclerListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Currency currency, int id) {
                Log.d("Currency item ", " " + currency.getName() + " long clicked");

                //Сообщаем презентеру, что валюта LongClicked
                mPresenter.onCurrencyLongClicked(currency);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void setCurrencies(List<Currency> currencies) {
        adapter.notifyDataSetChanged();
        Log.d(TAG, currencies.toString());
    }


    @Override
    public void replaceByExchangeFragment(String currencyFrom, String currencyTo) {
        ExchangeFragment fragment = new ExchangeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putStringArray("currencies", new String[]{currencyFrom, currencyTo});
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

    @Nullable
    @Override
    public void onDetach() {
        mPresenter.onDetach();
        unbinder.unbind();
        super.onDetach();
    }

}
