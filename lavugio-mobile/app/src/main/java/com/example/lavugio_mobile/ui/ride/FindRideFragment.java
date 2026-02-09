package com.example.lavugio_mobile.ui.ride;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lavugio_mobile.ui.components.BottomSheetHelper;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.lavugio_mobile.R;

public class FindRideFragment extends Fragment {
    private OSMMapFragment mapFragment;
    private FrameLayout bottomSheet;
    private TextView tvBottomSheetTitle;
    private FloatingActionButton fabSettings;
    private BottomSheetHelper bottomSheetHelper;

    // Current page index
    private int currentPage = 0;
    private String[] pageTitles = {"Find a Trip", "Select Route", "Confirm Ride"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        bottomSheet = view.findViewById(R.id.bottomSheet);
        tvBottomSheetTitle = view.findViewById(R.id.tvBottomSheetTitle);
        fabSettings = view.findViewById(R.id.fabSettings);

        // Load map fragment
        mapFragment = new OSMMapFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.mapFragmentContainer, mapFragment)
                .commit();

        // Setup bottom sheet
        setupBottomSheet();

        // Load first page
        loadPage(0);

        // Settings button
        fabSettings.setOnClickListener(v -> {
            // TODO: Open settings
        });
    }

    private void setupBottomSheet() {
        bottomSheetHelper = new BottomSheetHelper(bottomSheet, new BottomSheetHelper.BottomSheetCallback() {
            @Override
            public void onStateChanged(int newState) {
                // Handle state changes if needed
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // Fully expanded
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // Collapsed (peek)
                }
            }

            @Override
            public void onSlide(float slideOffset) {
                // slideOffset: 0.0 = collapsed, 1.0 = fully expanded
                // You can animate things based on this if needed

                // Example: Fade settings button when expanding
                fabSettings.setAlpha(1.0f - (slideOffset * 0.5f));
            }
        });

        // Set peek height (collapsed state height)
        bottomSheetHelper.setPeekHeight(200);
    }

    /**
     * Load a specific page in the bottom sheet
     */
    public void loadPage(int pageIndex) {
        currentPage = pageIndex;
        tvBottomSheetTitle.setText(pageTitles[pageIndex]);

        Fragment pageFragment;
        switch (pageIndex) {
            case 0:
                pageFragment = new FindTripPage1Fragment();
                break;
            case 1:
                pageFragment = new FindTripPage1Fragment();
                //pageFragment = new FindTripPage2Fragment();
                break;
            case 2:
                pageFragment = new FindTripPage1Fragment();
                //pageFragment = new FindTripPage3Fragment();
                break;
            default:
                pageFragment = new FindTripPage1Fragment();
        }

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.bottomSheetContentContainer, pageFragment)
                .commit();
    }

    /**
     * Go to next page
     */
    public void nextPage() {
        if (currentPage < pageTitles.length - 1) {
            loadPage(currentPage + 1);

            // Expand bottom sheet when moving to next page
            if (!bottomSheetHelper.isExpanded()) {
                bottomSheetHelper.expand();
            }
        }
    }

    /**
     * Go to previous page
     */
    public void previousPage() {
        if (currentPage > 0) {
            loadPage(currentPage - 1);
        }
    }

    /**
     * Expand bottom sheet
     */
    public void expandBottomSheet() {
        bottomSheetHelper.expand();
    }

    /**
     * Collapse bottom sheet
     */
    public void collapseBottomSheet() {
        bottomSheetHelper.collapse();
    }

    /**
     * Get map fragment for interaction
     */
    public OSMMapFragment getMapFragment() {
        return mapFragment;
    }
}
