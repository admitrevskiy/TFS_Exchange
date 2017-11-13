package com.example.tfs_exchange.comparators;

import com.example.tfs_exchange.model.Currency;

import java.util.Comparator;

/**
 * Created by pusya on 03.11.17.
 */

public class LongClickedComparator implements Comparator<Currency> {
    @Override
    public int compare(Currency currency1, Currency currency2) {
        if (!currency1.isLongClicked() && currency2.isLongClicked()) return 1;
        else if (currency1.isLongClicked() && !currency2.isLongClicked()) return -1;
        else return 0;
    }
}
