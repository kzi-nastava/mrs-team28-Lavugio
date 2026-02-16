package com.example.lavugio_mobile.ui;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.DriverLocation;
import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.api.DriverApi;
import com.example.lavugio_mobile.ui.auth.LoginFragment;
import com.example.lavugio_mobile.ui.auth.RegisterFragment;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;
import com.example.lavugio_mobile.services.utils.GeocodingHelper;
import androidx.appcompat.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;
import org.osmdroid.util.GeoPoint;

import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Guest Home Page Fragment
 * Displays a hero section with call-to-action buttons and a map.
 */
public class GuestHomePageFragment extends Fragment {

    // Hero Section Views
    private FrameLayout heroSection;
    private Button btnSeeRoutes;
    private Button btnRegister;
    private Button btnScrollToTop;
    private TextView tvLoginLink;

    // Map Section Views
    private FrameLayout mapSection;
    private FrameLayout mapFragmentContainer;
    private ScrollView scrollView;
    private Button btnRoute;

    // Map Fragment
    private OSMMapFragment mapFragment;



    private final List<Marker> driverMarkers = new ArrayList<>();

    private final int LOCATION_REFRESH_INTERVAL = 3000;
    private final Handler handler = new Handler();
    private Runnable locationRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guest_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupHeroSection();
        setupMap();
        setupRouteButton();
        setupScrollBehavior();
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void initViews(View view) {
        // Hero Section
        heroSection = view.findViewById(R.id.heroSection);
        scrollView = view.findViewById(R.id.scrollView);
        btnSeeRoutes = view.findViewById(R.id.btnSeeRoutes);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnScrollToTop = view.findViewById(R.id.btnScrollToTop);
        tvLoginLink = view.findViewById(R.id.tvLoginLink);

        // Map Section
        mapSection = view.findViewById(R.id.mapSection);
        mapFragmentContainer = view.findViewById(R.id.mapFragmentContainer);
        btnRoute = view.findViewById(R.id.btnRoute);

        // Set hero section and map section height to full screen height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        ViewGroup.LayoutParams heroParams = heroSection.getLayoutParams();
        heroParams.height = screenHeight;
        heroSection.setLayoutParams(heroParams);

        // Set map section to full screen height
        ViewGroup.LayoutParams mapParams = mapSection.getLayoutParams();
        mapParams.height = screenHeight;
        mapSection.setLayoutParams(mapParams);

        // Also set mapFragmentContainer height
        ViewGroup.LayoutParams containerParams = mapFragmentContainer.getLayoutParams();
        containerParams.height = screenHeight;
        mapFragmentContainer.setLayoutParams(containerParams);

        // Setup scroll listener for scroll to top button
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > 100) { // Show after scrolling 100 pixels
                btnScrollToTop.setVisibility(View.VISIBLE);
            } else {
                btnScrollToTop.setVisibility(View.GONE);
            }
        });
    }

    private void setupHeroSection() {
        // See Routes button - scroll to map section
        btnSeeRoutes.setOnClickListener(v -> {
            scrollView.post(() -> scrollView.smoothScrollTo(0, mapSection.getTop()));
        });

        // Register button
        btnRegister.setOnClickListener(v -> navigateToRegister());

        // Login link with underline
        tvLoginLink.setPaintFlags(tvLoginLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvLoginLink.setOnClickListener(v -> navigateToLogin());

        // Scroll to top button
        btnScrollToTop.setOnClickListener(v -> scrollView.smoothScrollTo(0, 0));
        btnScrollToTop.bringToFront();
    }

    private void setupMap() {
        android.util.Log.d("GuestHomePage", "Setting up map fragment...");
        android.util.Log.d("GuestHomePage", "mapSection height: " + mapSection.getLayoutParams().height);
        android.util.Log.d("GuestHomePage", "mapFragmentContainer height: " + mapFragmentContainer.getLayoutParams().height);

        // Create and add the map fragment
        mapFragment = new OSMMapFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.mapFragmentContainer, mapFragment)
                .commit();

        android.util.Log.d("GuestHomePage", "Map fragment transaction committed");
    }

    private void setupRouteButton() {
        if (btnRoute == null) return;
        btnRoute.setOnClickListener(v -> showRouteDialog(null, null));
    }

    private void showRouteDialog(String prefillOrigin, String prefillDestination) {
        if (getContext() == null) return;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_route_input, null);

        AutoCompleteTextView etOrigin = dialogView.findViewById(R.id.etOrigin);
        AutoCompleteTextView etDestination = dialogView.findViewById(R.id.etDestination);
        TextView tvRouteEstimate = dialogView.findViewById(R.id.tvRouteEstimate);
        Button btnShowRoute = dialogView.findViewById(R.id.btnShowRoute);
        Button btnPickOrigin = dialogView.findViewById(R.id.btnPickOrigin);
        Button btnPickDestination = dialogView.findViewById(R.id.btnPickDestination);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setNegativeButton("Close", (d, which) -> d.dismiss())
                .create();

        GeocodingHelper geocodingHelper = new GeocodingHelper();

        ArrayAdapter<GeocodingHelper.GeocodingResult> originAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        ArrayAdapter<GeocodingHelper.GeocodingResult> destAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());

        etOrigin.setAdapter(originAdapter);
        etDestination.setAdapter(destAdapter);

        // Prefill if provided
        if (prefillOrigin != null) etOrigin.setText(prefillOrigin);
        if (prefillDestination != null) etDestination.setText(prefillDestination);

        Handler searchHandler = new Handler();
        final Runnable[] originRunnable = new Runnable[1];
        final Runnable[] destRunnable = new Runnable[1];

        etOrigin.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (originRunnable[0] != null) searchHandler.removeCallbacks(originRunnable[0]);
                originRunnable[0] = () -> {
                    String q = s.toString().trim();
                    if (q.isEmpty()) return;
                    geocodingHelper.searchAddress(q, new GeocodingHelper.GeocodingCallback() {
                        @Override
                        public void onSuccess(List<GeocodingHelper.GeocodingResult> results) {
                            if (getActivity() == null) return;
                            getActivity().runOnUiThread(() -> {
                                ArrayAdapter<GeocodingHelper.GeocodingResult> a = new ArrayAdapter<>(
                                        getContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>(results));
                                originAdapter.clear();
                                originAdapter.addAll(results);
                                etOrigin.setAdapter(originAdapter);
                                if (!results.isEmpty() && etOrigin.isFocused()) etOrigin.showDropDown();
                            });
                        }

                        @Override
                        public void onError(String error) {
                        }
                    });
                };
                searchHandler.postDelayed(originRunnable[0], 400);
            }
        });

        etDestination.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (destRunnable[0] != null) searchHandler.removeCallbacks(destRunnable[0]);
                destRunnable[0] = () -> {
                    String q = s.toString().trim();
                    if (q.isEmpty()) return;
                    geocodingHelper.searchAddress(q, new GeocodingHelper.GeocodingCallback() {
                        @Override
                        public void onSuccess(List<GeocodingHelper.GeocodingResult> results) {
                            if (getActivity() == null) return;
                            getActivity().runOnUiThread(() -> {
                                destAdapter.clear();
                                destAdapter.addAll(results);
                                etDestination.setAdapter(destAdapter);
                                if (!results.isEmpty() && etDestination.isFocused()) etDestination.showDropDown();
                            });
                        }

                        @Override
                        public void onError(String error) {
                        }
                    });
                };
                searchHandler.postDelayed(destRunnable[0], 400);
            }
        });

        etOrigin.setOnItemClickListener((parent, view, position, id) -> {
            GeocodingHelper.GeocodingResult sel = originAdapter.getItem(position);
            if (sel != null) etOrigin.setText(sel.getDisplayName());
        });

        etDestination.setOnItemClickListener((parent, view, position, id) -> {
            GeocodingHelper.GeocodingResult sel = destAdapter.getItem(position);
            if (sel != null) etDestination.setText(sel.getDisplayName());
        });

        // Pick origin on map: dismiss dialog, wait for a single tap, then reverse-geocode and reopen dialog with origin
        btnPickOrigin.setOnClickListener(v -> {
            final String storedPrefillDestination = etDestination.getText().toString().trim();
            dialog.dismiss();
            if (mapFragment == null) return;
            mapFragment.setTempSingleTapListener(point -> {
                GeocodingHelper gh = new GeocodingHelper();
                gh.reverseGeocode(point.getLatitude(), point.getLongitude(), new GeocodingHelper.GeocodingCallback() {
                    @Override
                    public void onSuccess(List<GeocodingHelper.GeocodingResult> results) {
                        final String resolvedOrigin = (results != null && !results.isEmpty()) ? results.get(0).getDisplayName() : "";
                        if (getActivity() != null) getActivity().runOnUiThread(() -> {
                            mapFragment.setTempSingleTapListener(null);
                            showRouteDialog(resolvedOrigin.isEmpty() ? null : resolvedOrigin, storedPrefillDestination.isEmpty() ? null : storedPrefillDestination);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        if (getActivity() != null) getActivity().runOnUiThread(() -> {
                            mapFragment.setTempSingleTapListener(null);
                            showRouteDialog(null, storedPrefillDestination.isEmpty() ? null : storedPrefillDestination);
                        });
                    }
                });
            });
        });

        // Pick destination on map: dismiss dialog, wait for a single tap, then reverse-geocode and reopen dialog with destination
        btnPickDestination.setOnClickListener(v -> {
            final String storedPrefillOrigin = etOrigin.getText().toString().trim();
            dialog.dismiss();
            if (mapFragment == null) return;
            mapFragment.setTempSingleTapListener(point -> {
                GeocodingHelper gh = new GeocodingHelper();
                gh.reverseGeocode(point.getLatitude(), point.getLongitude(), new GeocodingHelper.GeocodingCallback() {
                    @Override
                    public void onSuccess(List<GeocodingHelper.GeocodingResult> results) {
                        final String resolvedDest = (results != null && !results.isEmpty()) ? results.get(0).getDisplayName() : "";
                        if (getActivity() != null) getActivity().runOnUiThread(() -> {
                            mapFragment.setTempSingleTapListener(null);
                            showRouteDialog(storedPrefillOrigin.isEmpty() ? null : storedPrefillOrigin, resolvedDest.isEmpty() ? null : resolvedDest);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        if (getActivity() != null) getActivity().runOnUiThread(() -> {
                            mapFragment.setTempSingleTapListener(null);
                            showRouteDialog(storedPrefillOrigin.isEmpty() ? null : storedPrefillOrigin, null);
                        });
                    }
                });
            });
        });

        btnShowRoute.setOnClickListener(v -> {
            String originText = etOrigin.getText().toString().trim();
            String destText = etDestination.getText().toString().trim();
            if (originText.isEmpty() || destText.isEmpty()) {
                Toast.makeText(getContext(), "Please enter both addresses", Toast.LENGTH_SHORT).show();
                return;
            }

            btnShowRoute.setEnabled(false);
            tvRouteEstimate.setText("Searching...");

            // Resolve origin then destination (use first result)
            geocodingHelper.searchAddress(originText, new GeocodingHelper.GeocodingCallback() {
                @Override
                public void onSuccess(List<GeocodingHelper.GeocodingResult> results) {
                    if (results.isEmpty()) {
                        if (getActivity() != null) getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Origin not found", Toast.LENGTH_SHORT).show());
                        btnShowRoute.setEnabled(true);
                        tvRouteEstimate.setText("");
                        return;
                    }
                    GeocodingHelper.GeocodingResult o = results.get(0);
                    GeoPoint from = new GeoPoint(o.getLatitude(), o.getLongitude());

                    geocodingHelper.searchAddress(destText, new GeocodingHelper.GeocodingCallback() {
                        @Override
                        public void onSuccess(List<GeocodingHelper.GeocodingResult> dResults) {
                            if (dResults.isEmpty()) {
                                if (getActivity() != null) getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Destination not found", Toast.LENGTH_SHORT).show());
                                btnShowRoute.setEnabled(true);
                                tvRouteEstimate.setText("");
                                return;
                            }
                            GeocodingHelper.GeocodingResult d = dResults.get(0);
                            GeoPoint to = new GeoPoint(d.getLatitude(), d.getLongitude());

                            if (mapFragment == null) {
                                if (getActivity() != null) getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Map not ready", Toast.LENGTH_SHORT).show());
                                btnShowRoute.setEnabled(true);
                                return;
                            }

                            // Clear previous
                            mapFragment.clearWaypoints();
                            mapFragment.clearRoute();

                            // Add waypoints and calculate route
                            mapFragment.addWaypoint(from);
                            mapFragment.addWaypoint(to);
                            mapFragment.calculateRoute();

                            mapFragment.calculateDuration(from, to, new OSMMapFragment.DurationCallback() {
                                @Override
                                public void onDurationCalculated(int durationMinutes, double distanceKm) {
                                    if (getActivity() != null) getActivity().runOnUiThread(() -> {
                                        String txt = "Estimated: " + durationMinutes + " min (" + String.format("%.1f", distanceKm) + " km)";
                                        tvRouteEstimate.setText(txt);
                                        btnShowRoute.setEnabled(true);
                                    });
                                }

                                @Override
                                public void onDurationError(String error) {
                                    if (getActivity() != null) getActivity().runOnUiThread(() -> {
                                        tvRouteEstimate.setText("Error: " + error);
                                        btnShowRoute.setEnabled(true);
                                    });
                                }
                            });
                        }

                        @Override
                        public void onError(String error) {
                            if (getActivity() != null) getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Destination geocoding error: " + error, Toast.LENGTH_SHORT).show());
                            btnShowRoute.setEnabled(true);
                            tvRouteEstimate.setText("");
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    if (getActivity() != null) getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Origin geocoding error: " + error, Toast.LENGTH_SHORT).show());
                    btnShowRoute.setEnabled(true);
                    tvRouteEstimate.setText("");
                }
            });
        });

        dialog.show();

        // Cleanup: when dialog closes, disable pick mode and remove listener
        dialog.setOnDismissListener(d -> {
            if (mapFragment != null) {
                mapFragment.setAddDestinationMode(false);
                mapFragment.setMapInteractionListener(null);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupScrollBehavior() {
        // Disable parent scroll when interacting with map
        mapFragmentContainer.setOnTouchListener((v, event) -> {
            scrollView.requestDisallowInterceptTouchEvent(true);
            return false;
        });
    }

    private void navigateToRegister() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToLogin() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }



    private void startLocationUpdates() {
        locationRunnable = new Runnable() {
            @Override
            public void run() {
                fetchLocations();
                handler.postDelayed(this, LOCATION_REFRESH_INTERVAL);
            }
        };
        handler.post(locationRunnable);
    }

    private void stopLocationUpdates() {
        if (locationRunnable != null) {
            handler.removeCallbacks(locationRunnable);
        }
    }
    private void fetchLocations(){
        android.util.Log.d("GuestHomePage", "Fetching driver locations...");
        DriverApi driverApi = ApiClient.getInstance().create(DriverApi.class);
        driverApi.getDriverLocations().enqueue(new retrofit2.Callback<List<DriverLocation>>() {
            @Override
            public void onResponse(@NonNull Call<List<DriverLocation>> call, retrofit2.Response<List<DriverLocation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DriverLocation> locations = response.body();
                    android.util.Log.d("GuestHomePage", "Received " + locations.size() + " driver locations");
                    updateDriverMarkers(locations);
                } else {
                    android.util.Log.e("GuestHomePage", "Error response: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DriverLocation>> call, @NonNull Throwable t) {
                android.util.Log.e("GuestHomePage", "API call failed: " + t.getMessage(), t);
            }
        });
    }

    private void updateDriverMarkers(List<DriverLocation> locations) {
        if (mapFragment == null) {
            android.util.Log.e("GuestHomePage", "mapFragment is null!");
            return;
        }

        android.util.Log.d("GuestHomePage", "Updating " + locations.size() + " driver markers");
        mapFragment.clearDriverMarkers();

        for (DriverLocation loc : locations) {
            android.util.Log.d("GuestHomePage", "Adding marker at: " + loc.getLocation().getLatitude() + ", " + loc.getLocation().getLongitude());
            mapFragment.addDriverMarker(
                    new org.osmdroid.util.GeoPoint(
                            loc.getLocation().getLatitude(),
                            loc.getLocation().getLongitude()
                    ),
                    "Driver ID: " + loc.getId() + " (" + loc.getStatus() + ")",
                    loc.getStatus()
            );
        }
    }

}