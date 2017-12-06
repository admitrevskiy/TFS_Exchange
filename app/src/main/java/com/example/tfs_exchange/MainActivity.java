package com.example.tfs_exchange;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.example.tfs_exchange.adapter.CurrencyRecyclerListAdapter;
import com.example.tfs_exchange.comparators.FavoriteComparator;
import com.example.tfs_exchange.fragments.AnalyticsFragment;
import com.example.tfs_exchange.fragments.CurrencySelectFragment;
import com.example.tfs_exchange.fragments.ExchangeFragment;
import com.example.tfs_exchange.fragments.HisroryFragment;
import com.example.tfs_exchange.fragments.HistoryFilterFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {

    private String currencyTAG = "CurrencySelectFragment";
    private String exchangeTAG = "ExchangeFragment";
    private String exchangeDialogTAG = "ExchangeDialog";
    private String historyTAG = "HistoryFragment";
    private String historyFilterTAG = "HistoryFilterFragment";
    private String analyticsTAG = "AnalyticsFragment";
    private static final String TAG = "MainActivity";

    private FragmentManager fragmentManager;

   @BindView(R.id.bottom_navigation)
   public BottomNavigationView bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fragmentManager = getSupportFragmentManager();
        ButterKnife.bind(this);

        CurrencySelectFragment currencySelectFragment = new CurrencySelectFragment();
        HisroryFragment historyFragment = new HisroryFragment();
        //HistoryFilterFragment fragment = new HistoryFilterFragment();
        AnalyticsFragment analyticsFragment = new AnalyticsFragment();
        addFragment(currencySelectFragment);

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.exchange:
                        /**
                        if (fragmentManager.findFragmentByTag(exchangeDialogTAG) != null) {
                            fragmentManager.popBackStack(exchangeDialogTAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            Log.d(exchangeDialogTAG, "popbackstack");
                        } else if (fragmentManager.findFragmentByTag(exchangeTAG) != null) {
                            fragmentManager.popBackStack(exchangeTAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            Log.d(exchangeTAG, "popbackstack");
                        } else if (fragmentManager.findFragmentByTag(currencyTAG) != null) {
                            fragmentManager.popBackStack(currencyTAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            Log.d(currencyTAG, "popbackstack");
                        } else {
                            replaceFragment(currencySelectFragment, currencyTAG);
                            Log.d(currencyTAG, "create new frag");
                        }
                        Log.d(TAG, fragmentManager.getBackStackEntryCount() + " fragments in backstack");
                         **/
                        replaceFragment(currencySelectFragment);
                        break;

                    case R.id.history:
                        /**
                        if (fragmentManager.findFragmentByTag(historyFilterTAG) != null) {
                            fragmentManager.popBackStack(historyFilterTAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            Log.d(historyFilterTAG, "popbackstack");
                        } else if (fragmentManager.findFragmentByTag(historyTAG) != null) {
                            fragmentManager.popBackStack(historyTAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            Log.d(historyTAG, "popbackstack");
                        } else {
                            replaceFragment(historyFragment, historyTAG);
                            Log.d(historyTAG, "create new frag");
                        }
                        Log.d(TAG, fragmentManager.getBackStackEntryCount() + " fragments in backstack");
                         **/
                        replaceFragment(historyFragment);
                        break;

                    case R.id.analytics:
                        /**
                        if (fragmentManager.findFragmentByTag(analyticsTAG) != null) {
                            fragmentManager.popBackStack(analyticsTAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            Log.d(analyticsTAG, "popbackstack");
                        } else {
                            replaceFragment(analyticsFragment, analyticsTAG);
                            Log.d(analyticsTAG, "create new frag");
                        }
                        Log.d(TAG, fragmentManager.getBackStackEntryCount() + " fragments in backstack");
                         **/
                        replaceFragment(analyticsFragment);
                        break;

                }
                return true;
            }
        });
    }

    private void addFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        //fragmentTransaction.addToBackStack(currencyTAG);
        fragmentTransaction.commit();
    }


    private void replaceFragment (Fragment fragment) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        //fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

}

