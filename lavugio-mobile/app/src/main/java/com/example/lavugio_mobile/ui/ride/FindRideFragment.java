package com.example.lavugio_mobile.ui.ride;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.lavugio_mobile.data.model.ride.RidePreferences;
import com.example.lavugio_mobile.data.model.utils.ResultState;
import com.example.lavugio_mobile.models.RideRequestDTO;
import com.example.lavugio_mobile.models.ride.RideDestinationDTO;
import com.example.lavugio_mobile.models.ride.StopBaseDTO;
import com.example.lavugio_mobile.services.utils.GeocodingHelper;
import com.example.lavugio_mobile.ui.components.BottomSheetHelper;
import com.example.lavugio_mobile.ui.dialog.ErrorDialogFragment;
import com.example.lavugio_mobile.ui.dialog.SuccessDialogFragment;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import com.example.lavugio_mobile.viewmodel.ride.FindRideViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.example.lavugio_mobile.R;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FindRideFragment extends Fragment implements OSMMapFragment.MapInteractionListener {
    private OSMMapFragment mapFragment;
    private FrameLayout bottomSheet;
    private TextView tvBottomSheetTitle;
    private BottomSheetHelper bottomSheetHelper;
    private boolean awaitingMapDestination;

    // Current page index
    private int currentPage = 0;
    private String[] pageTitles = {"Find a Ride", "Preferences", "Review & Confirm"};

    private List<GeocodingHelper.GeocodingResult> selectedDestinations;
    private RidePreferences selectedPreferences;

    private double currentRouteDistanceKm = 0.0;
    private double currentRouteDurationS = 0.0;
    private FindRideViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_ride, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FindRideViewModel.class);

        selectedDestinations = new ArrayList<>();
        selectedPreferences = new RidePreferences();

        // Initialize views
        bottomSheet = view.findViewById(R.id.bottomSheet);
        tvBottomSheetTitle = view.findViewById(R.id.tvBottomSheetTitle);

        // Load map fragment
        mapFragment = new OSMMapFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.mapFragmentContainer, mapFragment)
                .commit();

        setupBottomSheet();

        loadPage(0);

    }

    private void setupBottomSheet() {
        bottomSheetHelper = new BottomSheetHelper(bottomSheet, new BottomSheetHelper.BottomSheetCallback() {
            @Override
            public void onStateChanged(int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                }
            }

            @Override
            public void onSlide(float slideOffset) {

            }
        });

        // Convert 90dp to pixels
        int peekHeightPx = (int) (90 * getResources().getDisplayMetrics().density);
        bottomSheetHelper.setPeekHeight(peekHeightPx);
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
                pageFragment = FindRidePage1Fragment.newInstance(selectedDestinations);
                ((FindRidePage1Fragment) pageFragment).setViewModel(viewModel);
                break;
            case 1:
                pageFragment = FindRidePage2Fragment.newInstance(selectedPreferences);
                ((FindRidePage2Fragment) pageFragment).setViewModel(viewModel);
                break;
            case 2:
                pageFragment = FindRidePage3Fragment.newInstance(selectedDestinations, selectedPreferences, currentRouteDistanceKm, currentRouteDurationS);
                ((FindRidePage3Fragment) pageFragment).setViewModel(viewModel);
                break;
            default:
                pageFragment = new FindRidePage1Fragment();
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

    /*
     * Get map fragment for interaction
     */
    public OSMMapFragment getMapFragment() {
        return mapFragment;
    }

    public void setAwaitingMapDestination(boolean awaiting) {
        awaitingMapDestination = awaiting;
    }

    @Override
    public void onMapClicked(GeoPoint point) {
        if (!awaitingMapDestination || currentPage != 0) {
            return;
        }

        Fragment pageFragment = getChildFragmentManager().findFragmentById(R.id.bottomSheetContentContainer);
        if (pageFragment instanceof FindRidePage1Fragment) {
            ((FindRidePage1Fragment) pageFragment).addDestinationFromMap(point);
            awaitingMapDestination = false;
        }
    }

    @Override
    public void onMarkerClicked(org.osmdroid.views.overlay.Marker marker, GeoPoint point) {
        // No-op for now.
    }

    @Override
    public void onRouteCalculated(Road road) {
        this.currentRouteDistanceKm = road.mLength;
        this.currentRouteDurationS = road.mDuration;
    }

    public void setSelectedDestinations(List<GeocodingHelper.GeocodingResult> selectedDestinations) {
        this.selectedDestinations = selectedDestinations;
    }

    public void setSelectedPreferences(RidePreferences selectedPreferences) {
        this.selectedPreferences = selectedPreferences;
    }

    public void resetRideEstimation() {
        this.currentRouteDistanceKm = 0.0;
        this.currentRouteDurationS = 0.0;
    }

    public void orderRide(String rideType, String selectedTime, double ridePrice) {
        RideRequestDTO requestDTO = mapToRideRequestDTO(rideType, selectedTime, ridePrice);
        viewModel.findRide(requestDTO).observe(getViewLifecycleOwner(), result -> {
            if (result instanceof ResultState.Success) {
                SuccessDialogFragment.newInstance("Ride Ordered", "Your ride has been successfully ordered!")
                        .show(getParentFragmentManager(), "success_dialog");
                new android.os.Handler().postDelayed(() -> {
                    getParentFragmentManager().popBackStack();
                }, 3000);
            } else if (result instanceof ResultState.Error) {
                String message =
                        ((ResultState.Error) result).getMessage();

                ErrorDialogFragment
                        .newInstance("Error", message)
                        .show(getActivity().getSupportFragmentManager(),
                                "error_dialog");
            }
        });
    }

    private RideRequestDTO mapToRideRequestDTO(String rideType, String selectedTime, double ridePrice) {
        RideRequestDTO requestDTO = new RideRequestDTO();
        if (rideType.equals("ride_now")) {
            requestDTO.setScheduled(false);
        } else if (rideType.equals("schedule_ride")) {
            requestDTO.setScheduled(true);

        }
        Log.d("FindRideFragment", "Selected time: " + selectedTime);

        java.time.LocalDateTime localDateTime = null;
        if (selectedTime != null && !selectedTime.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ENGLISH);
                LocalTime selectedLocalTime = LocalTime.parse(selectedTime, formatter);
                LocalDateTime now = LocalDateTime.now();
                localDateTime = now.withHour(selectedLocalTime.getHour()).withMinute(selectedLocalTime.getMinute()).withSecond(0).withNano(0);

                // If the selected time is in the past, it must be tomorrow
                if (localDateTime.isBefore(now)) {
                    localDateTime = localDateTime.plusDays(1);
                }
            } catch (Exception e) {
                Log.e("FindRideFragment", "Error parsing selected time: " + e.getMessage());
            }
        }

        requestDTO.setScheduledTime(localDateTime);
        requestDTO.setBabyFriendly(selectedPreferences.isBabyFriendly());
        requestDTO.setBabyFriendly(selectedPreferences.isBabyFriendly());
        requestDTO.setPassengerEmails(selectedPreferences.getPassengerEmails());
        requestDTO.setDistance((float) currentRouteDistanceKm);
        requestDTO.setEstimatedDurationSeconds((int) currentRouteDurationS);
        requestDTO.setPrice((int) ridePrice);
        requestDTO.setVehicleType(selectedPreferences.getVehicleType());

        List<RideDestinationDTO> destinationDTOs = new ArrayList<>();
        int i = 0;
        for (GeocodingHelper.GeocodingResult dest : selectedDestinations) {
            RideDestinationDTO destDTO = new RideDestinationDTO();
            destDTO.setCity(dest.getCity());
            destDTO.setStreetName(dest.getStreet());
            try {
                destDTO.setZipCode(Integer.parseInt(dest.getPostcode()));
            } catch (NumberFormatException e) {
                destDTO.setZipCode(0);
            }
            destDTO.setCountry(dest.getCountry());
            destDTO.setZipCode(Integer.parseInt(dest.getPostcode()));
            try {
                destDTO.setStreetNumber(Integer.parseInt(dest.getHouseNumber()));
            } catch (NumberFormatException e) {
                destDTO.setStreetNumber(0);
            }
            StopBaseDTO stopBaseDTO = new StopBaseDTO();
            stopBaseDTO.setLatitude(dest.getLatitude());
            stopBaseDTO.setLongitude(dest.getLongitude());
            stopBaseDTO.setOrderIndex(i);
            destDTO.setLocation(stopBaseDTO);
            destinationDTOs.add(destDTO);
            i++;
        }
        requestDTO.setDestinations(destinationDTOs);

        return requestDTO;
    }
}
