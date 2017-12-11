package com.example.tfs_exchange;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.example.tfs_exchange.fragments.AnalyticsFragment;
import com.example.tfs_exchange.fragments.CurrencySelectFragment;
import com.example.tfs_exchange.fragments.HisroryFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    private FragmentManager fragmentManager;

    CurrencySelectFragment currencySelectFragment;
    HisroryFragment historyFragment;
    AnalyticsFragment analyticsFragment;

   @BindView(R.id.bottom_navigation)
   public BottomNavigationView bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        ButterKnife.bind(this);

        currencySelectFragment = new CurrencySelectFragment();
        historyFragment = new HisroryFragment();
        analyticsFragment = new AnalyticsFragment();

        if (savedInstanceState == null) {
            replaceFragment(currencySelectFragment);
            Log.d(TAG, "add CurrencySelectFragment");
        } else {
            Log.d(TAG, "restoreState");
        }

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.exchange:
                        replaceFragment(currencySelectFragment);
                        Log.d(TAG, "add CurrencySelectFragment");
                        break;
                    case R.id.history:
                        replaceFragment(historyFragment);
                        Log.d(TAG, "add HistoryFragment");
                        break;
                    case R.id.analytics:
                        replaceFragment(analyticsFragment);
                        Log.d(TAG, "add AnalyticsFragment");
                        break;
                }
                return true;
            }
        });
    }

    private void replaceFragment (Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}

