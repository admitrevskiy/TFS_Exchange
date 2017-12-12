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

import com.example.tfs_exchange.Main.MainContract;
import com.example.tfs_exchange.Main.MainPresenter;
import com.example.tfs_exchange.fragments.AnalyticsFragment;
import com.example.tfs_exchange.fragments.CurrencySelectFragment;
import com.example.tfs_exchange.fragments.HisroryFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends FragmentActivity implements MainContract.View{

    private static final String TAG = "MainActivity";

    private FragmentManager fragmentManager;

    private Unbinder unbinder;

    MainContract.Presenter mPresenter;

   @BindView(R.id.bottom_navigation)
   public BottomNavigationView bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        unbinder = ButterKnife.bind(this);
        mPresenter = new MainPresenter(this);

        mPresenter.initFirstFragment(savedInstanceState);

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.exchange:
                        mPresenter.onExchangeClicked();
                        Log.d(TAG, "add CurrencySelectFragment");
                        break;
                    case R.id.history:
                        mPresenter.onHistoryClicked();
                        Log.d(TAG, "add HistoryFragment");
                        break;
                    case R.id.analytics:
                        mPresenter.onAnalyticsClicked();
                        Log.d(TAG, "add AnalyticsFragment");
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void replaceFragment (Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}

