package com.example.currencyconverter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {
    private List<Currency> currencyList;

    public CurrencyAdapter(List<Currency> currencyList) {
        this.currencyList = currencyList;
    }

    @Override
    public CurrencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_currency, parent, false);
        return new CurrencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CurrencyViewHolder holder, int position) {
        Currency currency = currencyList.get(position);
        holder.nameTextView.setText(currency.getName());
        holder.rateTextView.setText(String.valueOf(currency.getRate()));
    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    public static class CurrencyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView rateTextView;

        public CurrencyViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.currency_name);
            rateTextView = itemView.findViewById(R.id.currency_rate);
        }
    }
}

