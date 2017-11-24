package com.example.tfs_exchange.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tfs_exchange.model.Exchange;
import com.example.tfs_exchange.R;

import java.text.SimpleDateFormat;
import java.util.List;

/** Разобраться как подключить ButterKnife.
 * Проблема возникает в onBindViewHolder
 *
 * upd: Нужно делать @BindView в классе ViewHolder!
 */
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by pusya on 10.11.17.
 */

public class HistoryRecyclerListAdapter extends RecyclerView.Adapter<HistoryRecyclerListAdapter.ViewHolder> {


    private final String TAG = "HistRecyclerListAdapter";
    private List<Exchange> exchanges;

    public HistoryRecyclerListAdapter(List<Exchange> exchanges) {
        this.exchanges = exchanges;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Инфлейтим xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exchange_item, parent, false);

        //Создаем и возвращаем viewHolder
        HistoryRecyclerListAdapter.ViewHolder viewHolder = new HistoryRecyclerListAdapter.ViewHolder(view);
        Log.d(TAG, " onCreateViewHolder");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Exchange exchange = exchanges.get(position);
        holder.currencyFromTextView.setText(exchange.getCurrencyFrom());
        holder.currencyToTextView.setText(exchange.getCurrencyTo());
        holder.amountFromTextView.setText(String.valueOf(exchange.getAmountFrom()));
        holder.amountToTextView.setText(String.valueOf(exchange.getAmountTo()));
        holder.dateTextView.setText(exchange.getDate() + "\n" + exchange.getTime());
        Log.d(TAG, " onBindViewHolder");
    }

    @Override
    public int getItemCount() {
        return exchanges.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.currencyFromTextView)
        TextView currencyFromTextView;

        @BindView(R.id.currencyToTextView)
        TextView currencyToTextView;

        @BindView(R.id.amountFromTextView)
        TextView amountFromTextView;

        @BindView(R.id.amountToTextView)
        TextView amountToTextView;

        @BindView(R.id.dateTextView)
        TextView dateTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Log.d(TAG, " ViewHolder constructor" );
        }
    }
}
