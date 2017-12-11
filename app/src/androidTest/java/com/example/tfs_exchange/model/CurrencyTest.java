package com.example.tfs_exchange.model;

import com.example.tfs_exchange.comparators.FavoriteComparator;
import com.example.tfs_exchange.comparators.LastUsedComparator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by pusya on 11.12.17.
 */

public class CurrencyTest {

    Currency currency1;
    Currency currency2;
    Currency currency3;
    FavoriteComparator faveComp;
    LastUsedComparator lastUsedComp;
    List<Currency> currencyFaveList;
    List<Currency> currencyTimeList;


    @Before
    public void setUp() throws Exception {
        currency1 = new Currency("USD", 0, true);
        currency2 = new Currency("USD", 1, false);
        currency3 = new Currency("EUR", 5, false);
        faveComp = new FavoriteComparator();
        lastUsedComp = new LastUsedComparator();
        currencyFaveList = new ArrayList<>();
        currencyTimeList = new ArrayList<>();
        currencyFaveList.add(currency1);
        currencyFaveList.add(currency3);
        currencyTimeList.add(currency2);
        currencyTimeList.add(currency3);


    }

    @After
    public void tearDown() throws Exception {
        currency1 = null;
        currency2 = null;
        faveComp = null;
        lastUsedComp = null;
        currencyFaveList = null;
        currencyTimeList = null;
        currency3 = null;
    }

    @Test
    public void hashTest() throws Exception {
        assertTrue(currency1.hashCode() == currency2.hashCode());
    }

    @Test
    public void equalsTest() throws Exception {
        assertTrue(!currency1.equals(currency2));
    }

    @Test
    public void faveSortTest() throws Exception {
        Collections.sort(currencyFaveList, faveComp);
        assertTrue(currencyFaveList.get(0).getName().equals("USD"));
    }

    @Test
    public void timeSortTest() throws Exception {
        Collections.sort(currencyTimeList, lastUsedComp);
        assertTrue(currencyTimeList.get(0).getName().equals("EUR"));
    }

    @Test
    public void hashNtEqualsTest() {
        assertTrue(currency3.hashCode() != currency2.hashCode());
    }


}
