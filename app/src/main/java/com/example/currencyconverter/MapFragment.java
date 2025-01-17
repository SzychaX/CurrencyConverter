package com.example.currencyconverter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapView;
    private Button showCapitalButton;

    // Mapowanie waluty na współrzędne stolic
    private Map<String, LatLng> capitalCitiesCoordinates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicjalizacja mapowania walut na współrzędne stolic
        capitalCitiesCoordinates = new HashMap<>();
        capitalCitiesCoordinates.put("USD", new LatLng(38.9072, -77.0369)); // Washington, D.C.
        capitalCitiesCoordinates.put("EUR", new LatLng(50.8503, 4.3517));  // Brussels
        capitalCitiesCoordinates.put("GBP", new LatLng(51.5074, -0.1278));  // London
        capitalCitiesCoordinates.put("PLN", new LatLng(52.2298, 21.0118));  // Warsaw
        capitalCitiesCoordinates.put("JPY", new LatLng(35.6762, 139.6503)); // Tokyo
        capitalCitiesCoordinates.put("CHF", new LatLng(46.9481, 7.4474));   // Bern
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflatujemy widok fragmentu (layout XML)
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        // Ustawiamy MapView
        mapView = rootView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Inicjalizujemy przycisk
        showCapitalButton = rootView.findViewById(R.id.showCapitalButton);

        // Ustawiamy listener dla przycisku
        showCapitalButton.setOnClickListener(v -> {
            // Zmienna waluty, dla której chcesz wyświetlić stolicę
            String selectedCurrency = "USD"; // Możesz to dynamicznie zmieniać np. na podstawie wyboru użytkownika
            showCapitalOnMap(selectedCurrency);
        });

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    // Funkcja do wyświetlania stolicy na mapie
    private void showCapitalOnMap(String currency) {
        LatLng capitalCoordinates = capitalCitiesCoordinates.get(currency.toUpperCase());

        if (capitalCoordinates != null) {
            // Dodajemy marker na mapie w odpowiednich współrzędnych
            mMap.addMarker(new MarkerOptions().position(capitalCoordinates).title("Capital: " + currency));
            mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(capitalCoordinates, 10));
        } else {
            // Jeśli nie ma stolicy dla danej waluty
            // Możesz dodać komunikat lub inne działanie
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
}
