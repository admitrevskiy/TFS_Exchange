package com.example.tfs_exchange.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tfs_exchange.R;

/**
 * Created by pusya on 01.11.17.
 */

public class ExchangeFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View exchangeFragmentRootView = inflater.inflate(R.layout.exchange_fragment, container, false);
        return exchangeFragmentRootView;
    }
}
