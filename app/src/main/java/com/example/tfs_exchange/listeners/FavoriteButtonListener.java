package com.example.tfs_exchange.listeners;

import android.view.View;

import com.example.tfs_exchange.Currency;
import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;

/**
 * Created by pusya on 26.10.17.
 */

public class FavoriteButtonListener implements View.OnClickListener {
    private Currency currency;

    public FavoriteButtonListener (Currency currency) {
        this.currency = currency;
    }
    @Override
    public void onClick(View view) {

    }

    public void setCurrency (Currency currency) {
        this.currency = currency;
    }

    public Currency getCurrency() {
        return this.currency;
    }
}
