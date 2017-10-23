package com.example.tfs_exchange.adapter;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.tfs_exchange.Currency;
import com.example.tfs_exchange.DBHelper;
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
 * **/

public class CurrencyRecyclerListAdapter extends RecyclerView.Adapter<CurrencyRecyclerListAdapter.ViewHolder> {

    private DBHelper dbHelper;


    //Список валют
    private List<Currency> currencies;

    //Нажатие, долгое нажатие, выбор избранных валют
    AdapterView.OnItemClickListener itemClickListener;
    AdapterView.OnItemLongClickListener itemLongClickListener;
    AdapterView.OnItemClickListener favoriteClickListener;

    //Конструктор
    public CurrencyRecyclerListAdapter(List<Currency> currencies,
                                       AdapterView.OnItemClickListener itemClickListener,
                                       AdapterView.OnItemLongClickListener itemLongClickListener,
                                       AdapterView.OnItemClickListener favoriteClickListener) {
        this.currencies = currencies;
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
        this.favoriteClickListener = favoriteClickListener;
    }

    public CurrencyRecyclerListAdapter(List<Currency> currencies) {
        this.currencies = currencies;
    }

    //Создаем ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Инфлейтим xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_item, parent, false);

        //Создаем и возвращаем viewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Currency currency = currencies.get(position);
        int iconResourceId = 0;

        //Выбираем иконку - избранная валюта или нет.
        if (!currency.isFavorite()) {
            iconResourceId = R.drawable.default_star;
        } else {
            iconResourceId = favorite_star;
        }
        holder.currencyName.setText(currency.getName());
        holder.favoriteButton.setImageResource(iconResourceId);
        holder.favoriteButtonListener.setCurrency(currency);
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
        private FavoriteButtonListener favoriteButtonListener;

        public ViewHolder(View itemView) {
            super(itemView);

            currencyName = itemView.findViewById(R.id.currencyItemTextView);
            favoriteButton = itemView.findViewById(R.id.selectFavoriteCurrencyButton);
            favoriteButtonListener = new FavoriteButtonListener();
            favoriteButton.setOnClickListener(favoriteButtonListener);
        }
    }

    private class FavoriteButtonListener implements View.OnClickListener {
        private Currency currency;

        @Override
        public void onClick(View view) {
            dbHelper = new DBHelper(view.getContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            int changeFavorite;
            if (currency.isFavorite())
            {
                replaceToNotFavorite(currency);
                changeFavorite = 0;
            } else {
                replaceToFavorite(currency);
                changeFavorite = 1;
            }
            cv.put("FAVORITE", changeFavorite);
            db.update("currency_name", cv, "currency_base = ?", new String[] {currency.getName()});

        }

        public void setCurrency(Currency currency) {
            this.currency = currency;
        }
    }

    private void replaceToFavorite (Currency currency) {

        int position = currencies.indexOf(currency);
        currency.setFavorite(true);
        notifyItemChanged(position);
    }

    private void replaceToNotFavorite (Currency currency) {
        int position = currencies.indexOf(currency);
        currency.setFavorite(false);
        notifyItemChanged(position);
    }
}