package com.example.lavugio_mobile.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class OSMMapFragment extends Fragment {

    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private List<Marker> waypoints = new ArrayList<>();
    private Polyline routeOverlay;
    private MapInteractionListener listener;

    // Default start position (Belgrade)
    private static final double DEFAULT_LAT =  45.25417;
    private static final double DEFAULT_LON = 19.84250;
    private static final double DEFAULT_ZOOM = 15;

    public interface MapInteractionListener {
        void onMapClicked(GeoPoint point);
        void onMarkerClicked(Marker marker, GeoPoint point);
        void onRouteCalculated(Road road);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Parent fragment može implementirati listener
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof MapInteractionListener) {
            listener = (MapInteractionListener) parentFragment;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // IMPORTANT: Initialize OSMDroid configuration
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getActivity().getPackageName());

        View view = inflater.inflate(R.layout.fragment_osm_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        setupMap();

        return view;
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);

        // Set initial position
        IMapController mapController = mapView.getController();
        mapController.setZoom(DEFAULT_ZOOM);
        GeoPoint startPoint = new GeoPoint(DEFAULT_LAT, DEFAULT_LON);
        mapController.setCenter(startPoint);

        // Add my location overlay
        setupMyLocationOverlay();

        // Add map click listener
        setupMapClickListener();
    }

    private void setupMyLocationOverlay() {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);
    }

    private void setupMapClickListener() {
        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (listener != null) {
                    listener.onMapClicked(p);
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                addWaypoint(p);
                return true;
            }
        };

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        mapView.getOverlays().add(0, mapEventsOverlay);
    }

    /**
     * Add a waypoint marker to the map
     */
    public Marker addWaypoint(GeoPoint point) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Waypoint " + (waypoints.size() + 1));

        // Custom icon (optional)
        Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_location_marker);
        if (icon != null) {
            marker.setIcon(icon);
        }

        marker.setOnMarkerClickListener((clickedMarker, mapView) -> {
            if (listener != null) {
                listener.onMarkerClicked(clickedMarker, clickedMarker.getPosition());
            }
            return false;
        });

        waypoints.add(marker);
        mapView.getOverlays().add(marker);
        mapView.invalidate();

        return marker;
    }

    /**
     * Remove a specific waypoint
     */
    public void removeWaypoint(Marker marker) {
        waypoints.remove(marker);
        mapView.getOverlays().remove(marker);
        mapView.invalidate();
    }

    /**
     * Clear all waypoints
     */
    public void clearWaypoints() {
        for (Marker marker : waypoints) {
            mapView.getOverlays().remove(marker);
        }
        waypoints.clear();
        mapView.invalidate();
    }

    /**
     * Calculate and draw route through all waypoints
     */
    public void calculateRoute() {
        if (waypoints.size() < 2) {
            return;
        }

        new Thread(() -> {
            try {
                // Create waypoints list
                ArrayList<GeoPoint> routePoints = new ArrayList<>();
                for (Marker marker : waypoints) {
                    routePoints.add(marker.getPosition());
                }

                // Get road using OSRM
                RoadManager roadManager = new OSRMRoadManager(getContext(), getActivity().getPackageName());
                Road road = roadManager.getRoad(routePoints);

                // Update UI on main thread
                getActivity().runOnUiThread(() -> {
                    if (road.mStatus == Road.STATUS_OK) {
                        drawRoute(road);
                        if (listener != null) {
                            listener.onRouteCalculated(road);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Draw route on map
     */
    private void drawRoute(Road road) {
        // Remove old route if exists
        if (routeOverlay != null) {
            mapView.getOverlays().remove(routeOverlay);
        }

        // Create new route overlay
        routeOverlay = RoadManager.buildRoadOverlay(road);
        routeOverlay.getOutlinePaint().setColor(ContextCompat.getColor(getContext(), R.color.lavugio_light_green));
        routeOverlay.getOutlinePaint().setStrokeWidth(12f);

        mapView.getOverlays().add(routeOverlay);
        mapView.invalidate();
    }

    /**
     * Clear route from map
     */
    public void clearRoute() {
        if (routeOverlay != null) {
            mapView.getOverlays().remove(routeOverlay);
            routeOverlay = null;
            mapView.invalidate();
        }
    }

    /**
     * Center map on specific location
     */
    public void centerMap(GeoPoint point, double zoom) {
        IMapController mapController = mapView.getController();
        mapController.setZoom(zoom);
        mapController.setCenter(point);
    }

    /**
     * Get current map center
     */
    public GeoPoint getMapCenter() {
        return (GeoPoint) mapView.getMapCenter();
    }

    /**
     * Get all waypoints
     */
    public List<Marker> getWaypoints() {
        return new ArrayList<>(waypoints);
    }

    /**
     * Enable/disable my location
     */
    public void setMyLocationEnabled(boolean enabled) {
        if (enabled) {
            myLocationOverlay.enableMyLocation();
            myLocationOverlay.enableFollowLocation();
        } else {
            myLocationOverlay.disableMyLocation();
            myLocationOverlay.disableFollowLocation();
        }
    }

    /**
     * Center map on my location
     */
    public void centerOnMyLocation() {
        GeoPoint myLocation = myLocationOverlay.getMyLocation();
        if (myLocation != null) {
            centerMap(myLocation, 15.0);
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
    public void onDestroyView() {
        super.onDestroyView();
        // Cleanup to avoid memory leaks
        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
        }
        if (mapView != null) {
            mapView.onDetach();
        }
    }
}