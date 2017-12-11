package com.example.tfs_exchange.currency_select;

import com.example.tfs_exchange.fragments.CurrencySelectFragment;
import com.example.tfs_exchange.fragments.HisroryFragment;
import com.example.tfs_exchange.history.HistoryPresenter;
import com.example.tfs_exchange.model.Currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
/**
 * Created by pusya on 11.12.17.
 */

public class CurrencySelectPresenterTest {
    CurrencySelectPresenter mPresenter;
    CurrencySelectFragment mView;
    List<Currency> currencies;

    @Before
    public void setUp() throws Exception {
        mView = new CurrencySelectFragment();
        mPresenter = new CurrencySelectPresenter(mView);
    }

    @After
    public void tearDown() throws Exception {
        mPresenter = null;
        mView = null;
        currencies = null;
    }

    @Test
    public void getCurrencyForExchange() throws Exception {
        assertTrue(mPresenter.getCurrencyForExchange(new Currency("USD", 1, false)).equals("RUB"));
    }
}
