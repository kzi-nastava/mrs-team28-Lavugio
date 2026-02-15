package com.example.lavugio_mobile.ui.driver.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.RideHistoryDriverDetailedModel;
import com.example.lavugio_mobile.services.DriverService;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;

import org.osmdroid.util.GeoPoint;

import java.util.List;
import java.util.stream.Collectors;

public class RideHistoryDriverDetailedFragment extends Fragment implements OSMMapFragment.MapInteractionListener {

    private static final String ARG_RIDE_ID = "rideId";
    private long rideId;
    private RideHistoryDriverDetailedModel ride;
    private DriverService driverService;

    private OSMMapFragment mapFragment;
    private RideInfoFragment tripInfoFragment;
    private PassengersFragment passengersFragment;

    public static RideHistoryDriverDetailedFragment newInstance(long rideId) {
        RideHistoryDriverDetailedFragment fragment = new RideHistoryDriverDetailedFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        driverService = LavugioApp.getDriverService();
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_ride_history_detailed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ── Load Map Fragment ───────────────────────────
        mapFragment = new OSMMapFragment();
        mapFragment.setMapInteractionListener(this);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        // ── Load Trip Info Fragment ─────────────────────
        tripInfoFragment = RideInfoFragment.newInstance();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.ride_info_history_container, tripInfoFragment)
                .commit();

        // ── Load Passengers Fragment ───────────────────
        passengersFragment = PassengersFragment.newInstance();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.passengers_container, passengersFragment)
                .commit();

        // ── Fetch ride data ────────────────────────────
        fetchRideDetails();
    }

    private void fetchRideDetails() {
        driverService.getDriverRideHistoryDetailed(rideId, new DriverService.Callback<RideHistoryDriverDetailedModel>() {
            @Override
            public void onSuccess(RideHistoryDriverDetailedModel result) {
                if (!isAdded()) return;
                ride = result;

                // Update child fragments
                if (tripInfoFragment != null) tripInfoFragment.updateInfo(ride);
                if (passengersFragment != null) passengersFragment.updatePassengers(ride.getPassengers());

                // Draw route on map
                if (mapFragment != null && ride.getCheckpoints() != null) {
                    mapFragment.clearWaypoints();
                    List<GeoPoint> routePoints = ride.getCheckpoints().stream()
                            .map(c -> new GeoPoint(c.getLatitude(), c.getLongitude()))
                            .collect(Collectors.toList());;

                    for (GeoPoint p : routePoints) {
                        mapFragment.addWaypoint(p);
                    }
                    mapFragment.calculateRoute();
                    mapFragment.centerMap(routePoints.get(0), 14.0);
                }
            }

            @Override
            public void onError(int code, String message) {
                // Handle error
            }
        });
    }

    // ── Map Callbacks ───────────────────────────────
    @Override
    public void onMapClicked(GeoPoint point) {
        // Optional: handle clicks
    }

    @Override
    public void onMarkerClicked(org.osmdroid.views.overlay.Marker marker, GeoPoint point) {
        // Optional: show marker info
    }

    @Override
    public void onRouteCalculated(org.osmdroid.bonuspack.routing.Road road) {
        // Optional: show route info
    }
}
