package com.example.tfs_exchange.comparators;

import com.example.tfs_exchange.model.Currency;

import java.util.Comparator;

/**
 * Created by pusya on 27.10.17.
 * Сортировка по избранным
 */

public class FavoriteComparator implements Comparator<Currency> {
    @Override
    public int compare(Currency currency1, Currency currency2) {
        if (currency1.isFavorite() && !currency2.isFavorite()) return -1;
        else if (!currency1.isFavorite() && currency2.isFavorite()) return 1;
        else return 0;
    }
}
