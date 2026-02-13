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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.DriverLocation;
import com.example.lavugio_mobile.services.ApiClient;
import com.example.lavugio_mobile.services.DriverApi;
import com.example.lavugio_mobile.ui.auth.LoginFragment;
import com.example.lavugio_mobile.ui.auth.RegisterFragment;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;

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
    private TextView tvLoginLink;

    // Map Section Views
    private FrameLayout mapSection;
    private FrameLayout mapFragmentContainer;
    private ScrollView scrollView;

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
        tvLoginLink = view.findViewById(R.id.tvLoginLink);

        // Map Section
        mapSection = view.findViewById(R.id.mapSection);
        mapFragmentContainer = view.findViewById(R.id.mapFragmentContainer);

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
        DriverApi driverApi = ApiClient.getClient().create(DriverApi.class);
        driverApi.getLocations().enqueue(new retrofit2.Callback<List<DriverLocation>>() {
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