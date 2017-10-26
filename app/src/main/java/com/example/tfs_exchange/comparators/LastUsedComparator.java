package com.example.tfs_exchange.comparators;

import com.example.tfs_exchange.Currency;

import java.util.Comparator;

/**
 * Created by pusya on 27.10.17.
 */

public class LastUsedComparator implements Comparator<Currency> {
    @Override
    public int compare(Currency currency1, Currency currency2) {
        if (currency1.getLastUse() < currency2.getLastUse()) return -1;
        else if (currency1.getLastUse() > currency2.getLastUse()) return 1;
        else return 0;
    }
}
