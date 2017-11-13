package com.example.tfs_exchange.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;


import com.example.tfs_exchange.Currency;
import com.example.tfs_exchange.R;

import java.util.List;


import static com.example.tfs_exchange.R.drawable.favorite_star;

/**
 * Created by pusya on 17.10.17.
 */

/**
 * Читать https://habrahabr.ru/post/237101/
 * http://www.vogella.com/tutorials/AndroidListView/article.html#adapterperformance
 * https://developer.android.com/reference/android/app/ListFragment.html
 * https://antonioleiva.com/recyclerview-listener/
 * **/

public class CurrencyRecyclerListAdapter extends RecyclerView.Adapter<CurrencyRecyclerListAdapter.ViewHolder> {

    //private DBHelper dbHelper;

    //Curr вместо Currency потому что длина тэга не больше 23 символов
    private final String TAG = "CurrRecyclerListAdapter";


    //Список валют
    private List<Currency> currencies;

    //Нажатие, долгое нажатие, выбор избранных валют
    OnItemClickListener itemClickListener;
    OnItemLongClickListener itemLongClickListener;
    OnItemClickListener favoriteClickListener;

    //Конструктор
    public CurrencyRecyclerListAdapter(List<Currency> currencies,
                                       OnItemClickListener favoriteClickListener,
                                       OnItemClickListener itemClickListener,
                                       OnItemLongClickListener itemLongClickListener) {
        this.currencies = currencies;
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
        this.favoriteClickListener = favoriteClickListener;
        Log.d(TAG, " Constructor");
    }

    public interface OnItemClickListener {

        void onItemClick(Currency currency);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(Currency currency, int id);
    }


    public CurrencyRecyclerListAdapter(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public CurrencyRecyclerListAdapter(List<Currency> currencies, OnItemClickListener favoriteClickListener) {
        this.currencies = currencies;
        this.favoriteClickListener = favoriteClickListener;
    }

    public CurrencyRecyclerListAdapter(List<Currency> currencies, OnItemClickListener favoriteClickListener, OnItemClickListener itemClickListener) {
        this.currencies = currencies;
        this.favoriteClickListener = favoriteClickListener;
        this.itemClickListener = itemClickListener;
    }

    //Создаем ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Инфлейтим xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_item, parent, false);

        //Создаем и возвращаем viewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        Log.d(TAG, " onCreateViewHolder");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Currency currency = currencies.get(position);

        holder.currencyName.setText(currency.getName());

        int iconResourceId = 0;

        //Выбираем иконку - избранная валюта или нет.
        if (!currency.isFavorite()) {
            iconResourceId = R.drawable.default_star;
        } else {
            iconResourceId = favorite_star;
        }
        holder.favoriteButton.setImageResource(iconResourceId);
        holder.bind(currencies.get(position), favoriteClickListener, itemClickListener, itemLongClickListener, position);
        Log.d(TAG, " onBindViewHolder");
    }

    //Размер списка
    @Override
    public int getItemCount() {
        return currencies.size();
    }

    //Реализация класса ViewHolder - хранит ссылки на виджеты
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView currencyName;
        ImageButton favoriteButton;


        public ViewHolder(View itemView) {
            super(itemView);
            currencyName = itemView.findViewById(R.id.currencyItemTextView);
            favoriteButton = itemView.findViewById(R.id.selectFavoriteCurrencyButton);
            Log.d(TAG, " ViewHolder constructor" );
        }

        public void bind(final Currency currency, final OnItemClickListener favoriteListener, final OnItemClickListener itemClickListener, final OnItemLongClickListener onItemLongClickListener, final int id) {
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteListener.onItemClick(currency);
                }
            });
            currencyName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(currency);
                }
            });
            currencyName.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemLongClickListener.onItemLongClick(currency, id);
                    return false;
                }
            });
            Log.d(TAG, " bind " + currency.getName());
        }
    }

}