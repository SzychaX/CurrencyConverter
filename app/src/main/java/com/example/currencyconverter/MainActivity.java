package com.example.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    private Spinner spinnerBaseCurrency;
    private Spinner spinnerTargetCurrency;
    private EditText amountEditText;
    private Button convertButton;
    private TextView resultTextView;
    private Button mapButton;
    private Button showCurrencyListButton;
    private GoogleMap googleMap;

    private ArrayList<String> currencyList = new ArrayList<>();
    private Map<String, String> currencyMap = new HashMap<>();
    private String selectedBaseCurrency = "";
    private String selectedTargetCurrency = "";

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float SHAKE_THRESHOLD = 15.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerBaseCurrency = findViewById(R.id.currencySpinnerBase);
        spinnerTargetCurrency = findViewById(R.id.currencySpinnerTarget);
        amountEditText = findViewById(R.id.amountEditText);
        convertButton = findViewById(R.id.convertButton);
        resultTextView = findViewById(R.id.resultTextView);
        mapButton = findViewById(R.id.mapButton);
        showCurrencyListButton = findViewById(R.id.showCurrencyListButton);

        loadCurrencies();


        spinnerBaseCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedBaseCurrency = currencyList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spinnerTargetCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedTargetCurrency = currencyList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        convertButton.setOnClickListener(v -> convertCurrency());
        mapButton.setOnClickListener(v -> showCapitalOnMap());

        // Inicjalizacja mapy
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        showCurrencyListButton.setOnClickListener(v -> {
            Log.d("MainActivity", "currencyMap size: " + currencyMap.size());
            Intent intent = new Intent(MainActivity.this, CurrencyListActivity.class);
            intent.putExtra("currencyMap", (HashMap<String, String>) currencyMap);
            startActivity(intent);
        });


        // Inicjalizacja akcelerometru
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (accelerometer != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;
            if (acceleration > SHAKE_THRESHOLD) {
                resetFields();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this implementation
    }

    private void resetFields() {
        runOnUiThread(() -> {
            amountEditText.setText("");
            resultTextView.setText("");
            Toast.makeText(this, "Fields reset after shaking the phone", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCurrencies() {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.nbp.pl/api/exchangerates/tables/A?format=json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                Scanner scanner = new Scanner(reader);
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }

                JSONArray jsonArray = new JSONArray(response.toString());
                JSONArray rates = jsonArray.getJSONObject(0).getJSONArray("rates");

                currencyList.add("PLN"); // Add PLN as base currency
                for (int i = 0; i < rates.length(); i++) {
                    JSONObject currency = rates.getJSONObject(i);
                    String currencyCode = currency.getString("code");
                    currencyList.add(currencyCode);
                    currencyMap.put(currencyCode, currency.getString("mid"));
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, currencyList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBaseCurrency.setAdapter(adapter);
                    spinnerTargetCurrency.setAdapter(adapter);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void convertCurrency() {
        try {
            String baseCurrency = spinnerBaseCurrency.getSelectedItem().toString();
            String targetCurrency = spinnerTargetCurrency.getSelectedItem().toString();
            String amountStr = amountEditText.getText().toString();

            if (amountStr.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);

            if (baseCurrency.equals("PLN") && targetCurrency.equals("PLN")) {
                resultTextView.setText(String.format(Locale.getDefault(), "%.2f", amount));
                return;
            }

            double baseRate = getCurrencyRate(baseCurrency);
            double targetRate = getCurrencyRate(targetCurrency);

            if (baseRate == -1 || targetRate == -1) {
                Toast.makeText(MainActivity.this, "Currency rate not found", Toast.LENGTH_SHORT).show();
                return;
            }

            double result = amount * baseRate / targetRate;
            resultTextView.setText(String.format(Locale.getDefault(), "%.2f", result));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error during conversion", Toast.LENGTH_SHORT).show();
        }
    }

    private double getCurrencyRate(String currency) {
        if (currency.equals("PLN")) {
            return 1;
        }

        String rate = currencyMap.get(currency);
        if (rate != null) {
            return Double.parseDouble(rate);
        }
        return -1;
    }

    private void showCapitalOnMap() {
        if (selectedTargetCurrency.equals("USD")) {
            showCapitalOnMap("Washington, D.C.", 38.9072, -77.0369);
        } else if (selectedTargetCurrency.equals("EUR")) {
            showCapitalOnMap("Brussels", 50.8503, 4.3517);
        } else if (selectedTargetCurrency.equals("GBP")) {
            showCapitalOnMap("London", 51.5074, -0.1278);
        } else {
            Toast.makeText(this, "No capital for this currency", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCapitalOnMap(String capital, double lat, double lng) {
        LatLng capitalLocation = new LatLng(lat, lng);
        if (googleMap != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(capitalLocation).title("Capital of " + selectedTargetCurrency + ": " + capital));
            googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(capitalLocation, 10));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
