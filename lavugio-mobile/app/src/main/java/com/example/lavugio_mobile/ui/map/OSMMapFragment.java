package com.example.lavugio_mobile.ui.map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.enums.DriverStatusEnum;

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
import android.view.MotionEvent;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OSMMapFragment extends Fragment {
    private boolean addDestinationMode = false;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private List<Marker> waypoints = new ArrayList<>();
    private Polyline routeOverlay;
    private MapInteractionListener listener;

    // Default start position (Belgrade)
    private static final double DEFAULT_LAT =  45.25417;
    private static final double DEFAULT_LON = 19.84250;
    private static final double DEFAULT_ZOOM = 15;

    private final List<Marker> driverMarkers = new ArrayList<>();
    private SingleTapListener tempSingleTapListener;

    public interface MapInteractionListener {
        void onMapClicked(GeoPoint point);
        void onMarkerClicked(Marker marker, GeoPoint point);
        void onRouteCalculated(Road road);
    }

    public interface SingleTapListener {
        void onSingleTap(GeoPoint point);
    }

    public void setTempSingleTapListener(SingleTapListener l) {
        this.tempSingleTapListener = l;
    }

    /**
     * Set the map interaction listener explicitly
     */
    public void setMapInteractionListener(MapInteractionListener listener) {
        this.listener = listener;
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
        // Initialize OSMDroid configuration
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getActivity().getPackageName());

        View view = inflater.inflate(R.layout.fragment_osm_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        setupMap();

        return view;
    }

    public void setAddDestinationMode(boolean mode) {
        this.addDestinationMode = mode;
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);

        // Prevent parent views from intercepting touch events
        // This allows the map to handle vertical panning
        mapView.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            try {
                if (tempSingleTapListener != null && event.getAction() == MotionEvent.ACTION_UP) {
                    GeoPoint p = (GeoPoint) mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                    tempSingleTapListener.onSingleTap(p);
                    return true;
                }
            } catch (Exception ignored) {}
            return false;
        });

        // Set initial position
        IMapController mapController = mapView.getController();
        mapController.setZoom(DEFAULT_ZOOM);
        GeoPoint startPoint = new GeoPoint(DEFAULT_LAT, DEFAULT_LON);
        mapController.setCenter(startPoint);

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
                // Note: Don't add waypoint here - let the listener handle it
                // This prevents duplicate markers when using map pick mode
                if (addDestinationMode) {
                    addDestinationMode = false;
                }

                if (listener != null) {
                    listener.onMapClicked(p);
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                //addWaypoint(p);
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

        android.util.Log.d("OSMMapFragment", "addWaypoint: marker=" + marker +
                ", waypoints size=" + waypoints.size() +
                ", overlays size=" + mapView.getOverlays().size());

        return marker;
    }

    /**
     * Remove a specific waypoint
     */
    public void removeWaypoint(Marker marker) {
        if (marker == null) {
            android.util.Log.e("OSMMapFragment", "removeWaypoint: marker is null");
            return;
        }

        boolean removedFromWaypoints = waypoints.remove(marker);
        boolean removedFromOverlays = mapView.getOverlays().remove(marker);

        android.util.Log.d("OSMMapFragment", "removeWaypoint: removedFromWaypoints=" + removedFromWaypoints +
                ", removedFromOverlays=" + removedFromOverlays +
                ", waypoints size=" + waypoints.size() +
                ", overlays size=" + mapView.getOverlays().size());

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
                OSRMRoadManager roadManager = new OSRMRoadManager(getContext(), getActivity().getPackageName());
                roadManager.setMean(OSRMRoadManager.MEAN_BY_CAR);
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

    public void clearDriverMarkers() {
        if (mapView == null) return;
        for (Marker marker : driverMarkers) {
            mapView.getOverlays().remove(marker);
        }
        driverMarkers.clear();
        mapView.invalidate();
    }

    public Marker addDriverMarker(GeoPoint point, String title, DriverStatusEnum status) {
        if (mapView == null) return null;
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);

        Drawable icon = null;

        if (status.equals(DriverStatusEnum.AVAILABLE)) {
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_car_available);
        } else if (status.equals(DriverStatusEnum.BUSY)) {
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_car_busy);
        } else if (status.equals(DriverStatusEnum.RESERVED)) {
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_car_reserved);
        }
        if (icon != null) marker.setIcon(icon);

        mapView.getOverlays().add(marker);
        driverMarkers.add(marker);
        mapView.invalidate();
        return marker;
    }

    public Marker addClientMarker(GeoPoint point){
        if (mapView == null) return null;
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Your location");

        Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_car_available);

        marker.setIcon(icon);

        mapView.getOverlays().add(marker);
        driverMarkers.add(marker);
        mapView.invalidate();
        return marker;
    }

    public interface DurationCallback {
        void onDurationCalculated(int durationMinutes, double distanceKm);
        void onDurationError(String error);
    }

    /**
     * Calculate route duration between two points using OSRM.
     */
    public void calculateDuration(GeoPoint from, GeoPoint to, DurationCallback callback) {
        if (!isAdded() || getContext() == null) {
            callback.onDurationError("Fragment not attached");
            return;
        }

        // Capture context on main thread before going to background
        Context ctx = requireContext().getApplicationContext();
        String userAgent = requireActivity().getPackageName();

        new Thread(() -> {
            try {
                ArrayList<GeoPoint> points = new ArrayList<>();
                points.add(from);
                points.add(to);

                RoadManager roadManager = new OSRMRoadManager(ctx, userAgent);
                Road road = roadManager.getRoad(points);

                if (road == null) {
                    postDurationError(callback, "Road calculation returned null");
                    return;
                }

                if (road.mStatus != Road.STATUS_OK) {
                    postDurationError(callback, "Road status: " + road.mStatus);
                    return;
                }

                int minutes = (int) Math.ceil(road.mDuration / 60.0);
                double km = road.mLength;

                android.util.Log.d("OSMMapFragment",
                        "Duration calculated: " + minutes + " min, " + km + " km");

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> callback.onDurationCalculated(minutes, km));
                }

            } catch (Exception e) {
                android.util.Log.e("OSMMapFragment", "Duration calculation failed", e);
                postDurationError(callback, e.getMessage());
            }
        }).start();
    }

    private void postDurationError(DurationCallback callback, String error) {
        android.util.Log.e("OSMMapFragment", "Duration error: " + error);
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> callback.onDurationError(error));
        }
    }
}