package com.example.tfs_exchange.history;

import com.example.tfs_exchange.fragments.HisroryFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by pusya on 11.12.17.
 */
public class HistoryPresenterTest {
    private HistoryPresenter mPresenter;
    private HistoryContract.View mView;

    @Before
    public void setUp() throws Exception {
        mView = new HisroryFragment();
        mPresenter = new HistoryPresenter(mView);
    }

    @After
    public void tearDown() throws Exception {
        mPresenter = null;
    }

    @Test
    public void makeMessageFirstTest() throws Exception {
        assertTrue(mPresenter.makeMessage().equals("Фильтр не выбран"));
    }

    @Test
    public void makeMessageSecondTest() throws Exception {
        mPresenter.setPeriodId(1);
        assertTrue(mPresenter.makeMessage().equals("Период: неделя"));
    }
}