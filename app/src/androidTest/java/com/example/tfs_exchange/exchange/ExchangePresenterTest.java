package com.example.tfs_exchange.exchange;

import com.example.tfs_exchange.currency_select.CurrencySelectPresenter;
import com.example.tfs_exchange.fragments.CurrencySelectFragment;
import com.example.tfs_exchange.fragments.ExchangeFragment;
import com.example.tfs_exchange.model.Currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by pusya on 11.12.17.
 */

public class ExchangePresenterTest {
    ExchangePresenter mPresenter;
    ExchangeFragment mView;
    Date dateNow;
    Calendar calendar;


    @Before
    public void setUp() throws Exception {
        mView = new ExchangeFragment();
        mPresenter = new ExchangePresenter(mView);
        calendar = Calendar.getInstance();
        dateNow = new Date();
        calendar.setTime(dateNow);
        calendar.add(Calendar.MINUTE, -4);
    }

    @After
    public void tearDown() throws Exception {
        mPresenter = null;
        mView = null;
        dateNow = null;
        calendar = null;
    }

    @Test
    public void checkTime() throws Exception {
        assertTrue(mPresenter.checkTime(dateNow.getTime(), calendar.getTimeInMillis()));
    }

    @Test
    public void anotherCheckTime() throws Exception {
        calendar.add(Calendar.MINUTE, -2);
        assertTrue(!mPresenter.checkTime(dateNow.getTime(), calendar.getTimeInMillis()));
    }
}
