package com.example.tfs_exchange.Main;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by pusya on 11.12.17.
 */

public interface MainContract {
    interface View {
        void replaceFragment(Fragment fragment);
    }

    interface Presenter {
        void onExchangeClicked();
        void onHistoryClicked();
        void onAnalyticsClicked();
        void initFirstFragment(Bundle savedInstanceState);
    }
}
