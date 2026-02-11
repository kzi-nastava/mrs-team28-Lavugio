package com.example.lavugio_mobile.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.lavugio_mobile.R;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

public class MapContainerFragment extends Fragment implements OSMMapFragment.MapInteractionListener {

    private OSMMapFragment mapFragment;
    private TextView tvInfo;
    private Button btnAddWaypoint, btnCalculateRoute, btnClearRoute, btnMyLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI
        tvInfo = view.findViewById(R.id.tvInfo);
        btnAddWaypoint = view.findViewById(R.id.btnAddWaypoint);
        btnCalculateRoute = view.findViewById(R.id.btnCalculateRoute);
        btnClearRoute = view.findViewById(R.id.btnClearRoute);
        btnMyLocation = view.findViewById(R.id.btnMyLocation);

        // Load map fragment
        mapFragment = new OSMMapFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.mapFragmentContainer, mapFragment)
                .commit();

        // Setup button listeners
        setupButtons();
    }

    private void setupButtons() {
        btnAddWaypoint.setOnClickListener(v -> {
            // Add waypoint at map center
            GeoPoint center = mapFragment.getMapCenter();
            mapFragment.addWaypoint(center);
            updateInfo();
        });

        btnCalculateRoute.setOnClickListener(v -> {
            if (mapFragment.getWaypoints().size() < 2) {
                Toast.makeText(getContext(), "Add at least 2 waypoints", Toast.LENGTH_SHORT).show();
                return;
            }
            mapFragment.calculateRoute();
        });

        btnClearRoute.setOnClickListener(v -> {
            mapFragment.clearRoute();
            mapFragment.clearWaypoints();
            updateInfo();
        });

        btnMyLocation.setOnClickListener(v -> {
            mapFragment.centerOnMyLocation();
        });
    }

    private void updateInfo() {
        int waypointCount = mapFragment.getWaypoints().size();
        tvInfo.setText("Waypoints: " + waypointCount);
    }

    @Override
    public void onMapClicked(GeoPoint point) {
        tvInfo.setText(String.format("Clicked: %.4f, %.4f", point.getLatitude(), point.getLongitude()));
    }

    @Override
    public void onMarkerClicked(Marker marker, GeoPoint point) {
        Toast.makeText(getContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRouteCalculated(Road road) {
        double distance = road.mLength; // in km
        double duration = road.mDuration / 60; // in minutes
        tvInfo.setText(String.format("Route: %.2f km, %.0f min", distance, duration));
    }
}