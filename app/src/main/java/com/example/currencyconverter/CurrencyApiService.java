package com.example.currencyconverter;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class CurrencyApiService {
    public static final Map<String, String> currencyToCapital = new HashMap<>();

    static {
        currencyToCapital.put("USD", "Washington, D.C.");
        currencyToCapital.put("EUR", "Brussels");
        currencyToCapital.put("GBP", "London");
        // Dodaj inne waluty i stolice
    }

    public static LatLng getCapitalLocation(String capital) {
        // Mapowanie stolic na współrzędne geograficzne
        switch (capital) {
            case "Washington, D.C.":
                return new LatLng(38.9072, -77.0369);
            case "Brussels":
                return new LatLng(50.8503, 4.3517);
            case "London":
                return new LatLng(51.5074, -0.1278);
            default:
                return new LatLng(0, 0);
        }
    }
}
