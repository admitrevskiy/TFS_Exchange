package com.example.tfs_exchange;

import java.util.Comparator;

/**
 * Created by pusya on 27.10.17.
 */

public class FavoriteComparator implements Comparator<Currency> {
    @Override
    public int compare(Currency currency1, Currency currency2) {
        if (currency1.isFavorite() && !currency2.isFavorite()) return -1;
        else if (!currency1.isFavorite() && !currency2.isFavorite()) return 1;
        else return 0;
    }
}
