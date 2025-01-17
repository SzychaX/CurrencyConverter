package com.example.currencyconverter;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrencyListActivity extends AppCompatActivity {

    private ListView currencyListView;
    private ArrayAdapter<String> adapter;
    private List<String> currencyData = new ArrayList<>();
    private Map<String, String> currencyMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list);

        currencyListView = findViewById(R.id.currencyListView);
        currencyMap = (Map<String, String>) getIntent().getSerializableExtra("currencyMap");

        if (currencyMap != null) {
            // Prepare list data
            List<String> currencyData = new ArrayList<>();
            for (Map.Entry<String, String> entry : currencyMap.entrySet()) {
                currencyData.add(entry.getKey() + ": " + entry.getValue() + " PLN");
            }

            // Check if data is loaded properly
            Log.d("CurrencyListActivity", "Currency Map size: " + currencyMap.size());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currencyData);
            currencyListView.setAdapter(adapter);
        } else {
            Log.e("CurrencyListActivity", "currencyMap is null");
        }
    }

}
