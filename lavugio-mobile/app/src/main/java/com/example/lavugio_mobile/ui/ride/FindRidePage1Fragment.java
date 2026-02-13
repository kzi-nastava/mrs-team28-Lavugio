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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.data.model.route.RideDestination;
import com.example.lavugio_mobile.data.model.user.DriverRegistrationData;
import com.example.lavugio_mobile.services.utils.GeocodingHelper;
import com.example.lavugio_mobile.ui.admin.RegisterDriverVehicleFragment;
import com.example.lavugio_mobile.ui.dialog.ErrorDialogFragment;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FindRidePage1Fragment extends Fragment {
    private static final String TAG = "FindRidePage1";

    private AutoCompleteTextView etDestination;
    private AppCompatImageButton btnAddDestination;
    private ScrollView svDestinationsList;
    private static final int MAX_VISIBLE_ITEMS = 3;
    private LinearLayout llDestinationsList;
    private TextView tvNoDestinations;
    private TextView tvSelectFavoriteRoute;
    private EditText etFavoriteRoute;
    private AppCompatImageButton btnSaveFavorite;
    private Button btnPrevious, btnNext;

    private GeocodingHelper geocodingHelper;
    private ArrayAdapter<GeocodingHelper.GeocodingResult> addressAdapter;
    private Handler searchHandler;
    private Runnable searchRunnable;

    private List<GeocodingHelper.GeocodingResult> selectedDestinations = new ArrayList<>();

    public static FindRidePage1Fragment newInstance(List<GeocodingHelper.GeocodingResult> selectedDestinations) {
        FindRidePage1Fragment fragment = new FindRidePage1Fragment();
        fragment.selectedDestinations = selectedDestinations;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_ride_page_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDestination = view.findViewById(R.id.etDestination);
        btnAddDestination = view.findViewById(R.id.btnAddDestination);
        llDestinationsList = view.findViewById(R.id.llDestinationsList);

        svDestinationsList = view.findViewById(R.id.svDestinationsList);

        tvNoDestinations = view.findViewById(R.id.tvNoDestinations);
        tvSelectFavoriteRoute = view.findViewById(R.id.tvSelectFavoriteRoute);
        etFavoriteRoute = view.findViewById(R.id.etFavoriteRoute);
        btnSaveFavorite = view.findViewById(R.id.btnSaveFavorite);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);

        geocodingHelper = new GeocodingHelper();
        searchHandler = new Handler();

        setupAutocomplete();
        setupButtons();
        setupDestinationScroll();

        if (selectedDestinations != null) {
            updateDestinationsDisplay();
            updateMapWithAllDestinations();
        }
    }

    private void setupDestinationScroll() {
        svDestinationsList.setOnTouchListener((v, event) -> {
            if (v.canScrollVertically(1) || v.canScrollVertically(-1)) {
                // Walk up the entire view hierarchy to prevent bottom sheet from intercepting
                ViewGroup parent = (ViewGroup) v.getParent();
                while (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                    if (parent.getParent() instanceof ViewGroup) {
                        parent = (ViewGroup) parent.getParent();
                    } else {
                        break;
                    }
                }
            }
            return false;
        });
    }

    private void constrainScrollViewHeight() {
        if (selectedDestinations.size() > MAX_VISIBLE_ITEMS) {
            // Measure one item to calculate max height for 3 items
            svDestinationsList.post(() -> {
                if (llDestinationsList.getChildCount() > 0) {
                    View firstItem = llDestinationsList.getChildAt(0);
                    firstItem.measure(
                            View.MeasureSpec.makeMeasureSpec(llDestinationsList.getWidth(), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    int itemHeight = firstItem.getMeasuredHeight();
                    // 3 items + spacing (8dp each) + padding (12dp top + 12dp bottom)
                    int spacingPx = (int) (8 * getResources().getDisplayMetrics().density);
                    int paddingPx = (int) (24 * getResources().getDisplayMetrics().density);
                    int maxHeight = (itemHeight * MAX_VISIBLE_ITEMS) + (spacingPx * (MAX_VISIBLE_ITEMS - 1)) + paddingPx;

                    ViewGroup.LayoutParams params = svDestinationsList.getLayoutParams();
                    params.height = maxHeight;
                    svDestinationsList.setLayoutParams(params);
                }
            });
        } else {
            ViewGroup.LayoutParams params = svDestinationsList.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            svDestinationsList.setLayoutParams(params);
        }
    }

    private void setupAutocomplete() {
        // Setup adapter for autocomplete with a mutable list
        addressAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()
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
            OSMMapFragment mapFragment = ((FindRideFragment) getParentFragment()).getMapFragment();
            mapFragment.setAddDestinationMode(true);
            FindRideFragment parent = (FindRideFragment) getParentFragment();
            if (parent != null) {
                parent.setAwaitingMapDestination(true);
            }
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
            addFavoriteRoute();
        });

        tvSelectFavoriteRoute.setOnClickListener(this::openFavoriteRoutesDialog);

        btnPrevious.setEnabled(false);
        btnPrevious.setAlpha(0.5f);
        btnNext.setOnClickListener(v -> {
            // Add markers to map and go to next page
            addMarkersToMap();
            OSMMapFragment mapFragment = ((FindRideFragment) getParentFragment()).getMapFragment();
            mapFragment.setAddDestinationMode(false);
            FindRideFragment parent = (FindRideFragment) getParentFragment();
            if (parent != null) {
                parent.setAwaitingMapDestination(false);
            }
            ((FindRideFragment) getParentFragment()).nextPage();
        });
    }

    private void addFavoriteRoute() {
        if (etFavoriteRoute.getText().toString().isEmpty()) {
            ErrorDialogFragment.newInstance("Error", "You have to enter a name of the favorite route.")
                    .show(getActivity().getSupportFragmentManager(), "error_dialog");
            return;
        }
        if (selectedDestinations.size() < 2) {
            ErrorDialogFragment.newInstance("Error", "You have to enter at least 2 destinations.")
                    .show(getActivity().getSupportFragmentManager(), "error_dialog");
            return;
        }
        etFavoriteRoute.setText("");
        Log.d("SCHEDULE", "Added favorite route" + etFavoriteRoute.getText().toString());
        // TODO: POZOVI ENDPOINT ZA DODAVANJE OMILJENE RUTE
    }

    public void addDestinationFromMap(GeoPoint point) {
        double lat = point.getLatitude();
        double lon = point.getLongitude();

        new Thread(() -> {
            String street = "";
            String houseNumber = "";
            String city = "";
            String postcode = "";
            String country = "";
            String displayName;

            try {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    street = address.getThoroughfare() != null ? address.getThoroughfare() : "";
                    houseNumber = address.getSubThoroughfare() != null ? address.getSubThoroughfare() : "";
                    city = address.getLocality() != null ? address.getLocality() : "";
                    postcode = address.getPostalCode() != null ? address.getPostalCode() : "";
                    country = address.getCountryName() != null ? address.getCountryName() : "";
                }
            } catch (Exception e) {
                Log.w(TAG, "Reverse geocoding failed", e);
            }

            if (!street.isEmpty()) {
                displayName = houseNumber.isEmpty() ? street : street + " " + houseNumber;
                if (!city.isEmpty()) {
                    displayName += ", " + city;
                }
            } else if (!city.isEmpty()) {
                displayName = city;
            } else {
                displayName = String.format(Locale.US, "Lat %.5f, Lon %.5f", lat, lon);
            }

            GeocodingHelper.GeocodingResult result = new GeocodingHelper.GeocodingResult(
                    displayName,
                    lat,
                    lon,
                    street,
                    houseNumber,
                    city,
                    postcode,
                    country
            );

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> addDestination(result));
            }
        }).start();
    }

    private void addDestination(GeocodingHelper.GeocodingResult result) {
        selectedDestinations.add(result);
        updateDestinationsDisplay();
        updateMapWithAllDestinations();

        Toast.makeText(getContext(), "Added: " + result.getDisplayName(), Toast.LENGTH_SHORT).show();
    }

    private void removeDestination(int position) {
        if (position >= 0 && position < selectedDestinations.size()) {
            GeocodingHelper.GeocodingResult removed = selectedDestinations.remove(position);
            updateDestinationsDisplay();
            updateMapWithAllDestinations();

            Toast.makeText(getContext(), "Removed: " + removed.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDestinationsDisplay() {
        llDestinationsList.removeAllViews();
        if (selectedDestinations.isEmpty()) {
            llDestinationsList.addView(tvNoDestinations);
        } else {
            for (int i = 0; i < selectedDestinations.size(); i++) {
                View destinationItem = createDestinationItem(i, selectedDestinations.get(i));
                llDestinationsList.addView(destinationItem);

                if (i < selectedDestinations.size() - 1) {
                    View spacer = new View(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            8 // 8dp spacing
                    );
                    spacer.setLayoutParams(params);
                    llDestinationsList.addView(spacer);
                }
            }
        }

        constrainScrollViewHeight();
    }

    private View createDestinationItem(int position, GeocodingHelper.GeocodingResult destination) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.scrollable_list_item, llDestinationsList, false);

        TextView tvNumber = itemView.findViewById(R.id.tvItemNumber);
        TextView tvAddress = itemView.findViewById(R.id.tvItemName);
        ImageView ivRemove = itemView.findViewById(R.id.ivRemoveItem);

        tvNumber.setText(String.valueOf(position + 1));
        tvAddress.setText(destination.getDisplayName());

        setupMarquee(tvAddress);

        ivRemove.setOnClickListener(v -> removeDestination(position));

        return itemView;
    }

    private void updateMapWithAllDestinations() {
        FindRideFragment parentFragment = (FindRideFragment) getParentFragment();

        if (parentFragment != null) {
            OSMMapFragment mapFragment = parentFragment.getMapFragment();

            if (mapFragment != null) {
                // Clear all existing waypoints
                mapFragment.clearWaypoints();
                mapFragment.clearRoute();

                // Add all destinations as markers
                for (GeocodingHelper.GeocodingResult destination : selectedDestinations) {
                    GeoPoint point = new GeoPoint(destination.getLatitude(), destination.getLongitude());
                    Marker marker = mapFragment.addWaypoint(point);
                    marker.setTitle(destination.getDisplayName());
                }

                // Calculate route if multiple destinations
                if (selectedDestinations.size() > 1) {
                    mapFragment.calculateRoute();
                }

                // Center map on latest destination
                if (!selectedDestinations.isEmpty()) {
                    GeocodingHelper.GeocodingResult latest = selectedDestinations.get(selectedDestinations.size() - 1);
                    mapFragment.centerMap(
                            new GeoPoint(latest.getLatitude(), latest.getLongitude()),
                            15.0
                    );
                }
            }
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


    private void setupMarquee(TextView textView) {
        // Initially not selected (no marquee)
        textView.setSelected(false);

        // Toggle marquee on click
        textView.setOnClickListener(v -> {
            boolean isSelected = textView.isSelected();

            if (!isSelected) {
                textView.setSelected(true);
            } else {
                textView.setSelected(false);
            }
        });
    }

    public void openFavoriteRoutesDialog(View view) {
        FavoriteRoutesDialogFragment fragment = new FavoriteRoutesDialogFragment();

        fragment.setOnRouteSelectedListener(new FavoriteRoutesDialogFragment.OnRouteSelectedListener() {
            @Override
            public void onRouteSelected(RideDestination[] destinations, String routeName) {
                // Dobijaš podatke
                //updateRouteInfo(routeName, fromAddress, toAddress);
                Log.d("SCHEDULE", "Odabrana ruta" + routeName);
                getFavoriteRouteDestinations(destinations);
            }
        });
        fragment.show(getParentFragmentManager(), "FavoriteRoutesDialogFragment");
    }

    private void getFavoriteRouteDestinations(RideDestination[] destinations) {
        this.selectedDestinations.clear();
        for (RideDestination destination : destinations) {
            this.selectedDestinations.add(new GeocodingHelper.GeocodingResult(destination.getName(),
                    destination.getCoordinates().getLatitude(),
                    destination.getCoordinates().getLongitude(),
                    destination.getStreet(),
                    destination.getHouseNumber(),
                    destination.getCity(),
                    "11000",
                    destination.getCountry()));
        }
        updateDestinationsDisplay();
        updateMapWithAllDestinations();
    }
}