package com.example.lavugio_mobile.ui.ride;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.services.utils.GeocodingHelper;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class FindTripPage1Fragment extends Fragment {
    private static final String TAG = "FindTripPage1";

    private AutoCompleteTextView etDestination;
    private AppCompatImageButton btnAddDestination;
    private TextView tvNoDestinations;
    private EditText etFavoriteRoute;
    private AppCompatImageButton btnSaveFavorite;
    private Button btnPrevious, btnNext;

    private GeocodingHelper geocodingHelper;
    private ArrayAdapter<GeocodingHelper.GeocodingResult> addressAdapter;
    private Handler searchHandler;
    private Runnable searchRunnable;

    private List<GeocodingHelper.GeocodingResult> selectedDestinations = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_trip_page_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDestination = view.findViewById(R.id.etDestination);
        btnAddDestination = view.findViewById(R.id.btnAddDestination);
        tvNoDestinations = view.findViewById(R.id.tvNoDestinations);
        etFavoriteRoute = view.findViewById(R.id.etFavoriteRoute);
        btnSaveFavorite = view.findViewById(R.id.btnSaveFavorite);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);

        geocodingHelper = new GeocodingHelper();
        searchHandler = new Handler();

        setupAutocomplete();
        setupButtons();
    }

    private void setupAutocomplete() {
        // Setup adapter for autocomplete with a mutable list
        addressAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line
        );
        etDestination.setAdapter(addressAdapter);
        etDestination.setThreshold(1); // Start showing after 1 character
        etDestination.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        // Add text watcher with debouncing
        etDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel previous search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Schedule new search with 500ms delay
                searchRunnable = () -> searchAddress(s.toString());
                searchHandler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Handle selection from dropdown
        etDestination.setOnItemClickListener((parent, view, position, id) -> {
            GeocodingHelper.GeocodingResult selected = addressAdapter.getItem(position);
            if (selected != null) {
                // Auto-add selected destination
                addDestination(selected);
                etDestination.setText("");
            }
        });
    }

    private void searchAddress(String query) {
        String trimmed = query.trim();
        if (trimmed.isEmpty()) {
            etDestination.dismissDropDown();
            return;
        }

        Log.d(TAG, "Searching for: " + trimmed);
        geocodingHelper.searchAddress(trimmed, new GeocodingHelper.GeocodingCallback() {
            @Override
            public void onSuccess(List<GeocodingHelper.GeocodingResult> results) {
                Log.d(TAG, "Got " + results.size() + " results");
                for (GeocodingHelper.GeocodingResult r : results) {
                    Log.d(TAG, "Result: " + r.getDisplayName());
                }
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    // Create a new adapter each time to bypass AutoCompleteTextView's filter
                    addressAdapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            new ArrayList<>(results)
                    );
                    etDestination.setAdapter(addressAdapter);
                    Log.d(TAG, "Adapter count: " + addressAdapter.getCount());
                    if (!results.isEmpty() && etDestination.isFocused()) {
                        etDestination.showDropDown();
                        Log.d(TAG, "Showing dropdown");
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.w(TAG, "Autocomplete search failed: " + error);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Search failed", Toast.LENGTH_SHORT).show();
                }
                etDestination.dismissDropDown();
            }
        });
    }

    private void setupButtons() {
        btnAddDestination.setOnClickListener(v -> {
            String text = etDestination.getText().toString().trim();
            if (!text.isEmpty()) {
                // Search and add first result
                geocodingHelper.searchAddress(text, new GeocodingHelper.GeocodingCallback() {
                    @Override
                    public void onSuccess(List<GeocodingHelper.GeocodingResult> results) {
                        if (!results.isEmpty()) {
                            addDestination(results.get(0));
                            etDestination.setText("");
                        } else {
                            Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Search failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnSaveFavorite.setOnClickListener(v -> {
            // TODO: Save favorite route logic
        });

        btnPrevious.setVisibility(View.GONE);

        btnNext.setOnClickListener(v -> {
            if (selectedDestinations.isEmpty()) {
                Toast.makeText(getContext(), "Please add at least one destination", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add markers to map and go to next page
            addMarkersToMap();
            ((FindRideFragment) getParentFragment()).nextPage();
        });
    }

    private void addDestination(GeocodingHelper.GeocodingResult result) {
        selectedDestinations.add(result);
        updateDestinationsDisplay();

        Toast.makeText(getContext(), "Added: " + result.getDisplayName(), Toast.LENGTH_SHORT).show();
    }

    private void updateDestinationsDisplay() {
        if (selectedDestinations.isEmpty()) {
            tvNoDestinations.setVisibility(View.VISIBLE);
            tvNoDestinations.setText("No destinations added yet");
        } else {
            tvNoDestinations.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < selectedDestinations.size(); i++) {
                sb.append((i + 1)).append(". ").append(selectedDestinations.get(i).getDisplayName());
                if (i < selectedDestinations.size() - 1) {
                    sb.append("\n");
                }
            }
            tvNoDestinations.setText(sb.toString());
        }
    }

    private void addMarkersToMap() {
        OSMMapFragment mapFragment = ((FindRideFragment) getParentFragment()).getMapFragment();

        // Clear existing waypoints
        mapFragment.clearWaypoints();

        // Add new waypoints
        for (GeocodingHelper.GeocodingResult destination : selectedDestinations) {
            GeoPoint point = new GeoPoint(destination.getLatitude(), destination.getLongitude());
            Marker marker = mapFragment.addWaypoint(point);
            marker.setTitle(destination.getDisplayName());
        }

        // Calculate route if multiple destinations
        if (selectedDestinations.size() > 1) {
            mapFragment.calculateRoute();
        }

        // Center map on first destination
        if (!selectedDestinations.isEmpty()) {
            GeocodingHelper.GeocodingResult first = selectedDestinations.get(0);
            mapFragment.centerMap(new GeoPoint(first.getLatitude(), first.getLongitude()), 14.0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        geocodingHelper.cancelRequests();
    }

    public List<GeocodingHelper.GeocodingResult> getSelectedDestinations() {
        return selectedDestinations;
    }
}