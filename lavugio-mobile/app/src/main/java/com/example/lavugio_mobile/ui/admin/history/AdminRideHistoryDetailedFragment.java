package com.example.lavugio_mobile.ui.admin.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lavugio_mobile.BuildConfig;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.AdminHistoryDetailedModel;
import com.example.lavugio_mobile.models.Coordinates;
import com.example.lavugio_mobile.repository.admin.AdminHistoryRepository;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;
import com.google.android.material.button.MaterialButton;
import org.osmdroid.util.GeoPoint;

public class AdminRideHistoryDetailedFragment extends Fragment {

    private static final String ARG_RIDE_ID = "ride_id";
    private static final String ARG_EMAIL = "email";

    private Long rideId;
    private String email;
    private AdminHistoryDetailedModel ride;
    private AdminHistoryRepository repository;

    private OSMMapFragment mapFragment;
    private TextView startTextView;
    private TextView endTextView;
    private TextView departureTextView;
    private TextView destinationTextView;
    private TextView priceTextView;
    private TextView cancelledTextView;
    private TextView panicTextView;
    private MaterialButton backButton;

    // Driver info views
    private androidx.cardview.widget.CardView driverInfoContainer;
    private ImageView driverPhotoImageView;
    private TextView driverNameTextView;
    private TextView driverEmailTextView;
    private TextView driverPhoneTextView;
    private TextView vehicleMakeTextView;
    private TextView vehicleLicensePlateTextView;
    private TextView vehicleColorTextView;

    // Passengers views
    private androidx.cardview.widget.CardView passengersContainer;
    private LinearLayout passengersListContainer;
    private TextView noPassengersTextView;

    // Review views
    private androidx.cardview.widget.CardView reviewContainer;
    private LinearLayout reviewContentContainer;
    private TextView noReviewTextView;
    private TextView driverRatingTextView;
    private TextView carRatingTextView;
    private TextView reviewCommentTextView;
    private LinearLayout driverStarsContainer;
    private LinearLayout carStarsContainer;

    // Reports views
    private androidx.cardview.widget.CardView reportsContainer;
    private LinearLayout reportsListContainer;
    private TextView noReportsTextView;

    public AdminRideHistoryDetailedFragment() {}

    public static AdminRideHistoryDetailedFragment newInstance(Long rideId, String email) {
        AdminRideHistoryDetailedFragment fragment = new AdminRideHistoryDetailedFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
            email = getArguments().getString(ARG_EMAIL);
        }
        repository = new AdminHistoryRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_ride_history_detailed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            initViews(view);
            setupMap();
            loadRideDetails();

            if (backButton != null) {
                backButton.setOnClickListener(v -> goBack());
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            // Still try to set up back button so user can go back
            try {
                backButton = view.findViewById(R.id.backButton);
                if (backButton != null) {
                    backButton.setOnClickListener(v -> goBack());
                }
            } catch (Exception ex) {
                // Ignore
            }
        }
    }

    private void initViews(View view) {
        startTextView = view.findViewById(R.id.startTextView);
        endTextView = view.findViewById(R.id.endTextView);
        departureTextView = view.findViewById(R.id.departureTextView);
        destinationTextView = view.findViewById(R.id.destinationTextView);
        priceTextView = view.findViewById(R.id.priceTextView);
        cancelledTextView = view.findViewById(R.id.cancelledTextView);
        panicTextView = view.findViewById(R.id.panicTextView);
        backButton = view.findViewById(R.id.backButton);

        // Driver info
        driverInfoContainer = view.findViewById(R.id.driverInfoContainer);
        driverPhotoImageView = view.findViewById(R.id.driverPhotoImageView);
        driverNameTextView = view.findViewById(R.id.driverNameTextView);
        driverEmailTextView = view.findViewById(R.id.driverEmailTextView);
        driverPhoneTextView = view.findViewById(R.id.driverPhoneTextView);
        vehicleMakeTextView = view.findViewById(R.id.vehicleMakeTextView);
        vehicleLicensePlateTextView = view.findViewById(R.id.vehicleLicensePlateTextView);
        vehicleColorTextView = view.findViewById(R.id.vehicleColorTextView);

        // Passengers
        passengersContainer = view.findViewById(R.id.passengersContainer);
        passengersListContainer = view.findViewById(R.id.passengersListContainer);
        noPassengersTextView = view.findViewById(R.id.noPassengersTextView);

        // Review
        reviewContainer = view.findViewById(R.id.reviewContainer);
        reviewContentContainer = view.findViewById(R.id.reviewContentContainer);
        noReviewTextView = view.findViewById(R.id.noReviewTextView);
        driverRatingTextView = view.findViewById(R.id.driverRatingTextView);
        carRatingTextView = view.findViewById(R.id.carRatingTextView);
        reviewCommentTextView = view.findViewById(R.id.reviewCommentTextView);
        driverStarsContainer = view.findViewById(R.id.driverStarsContainer);
        carStarsContainer = view.findViewById(R.id.carStarsContainer);

        // Reports
        reportsContainer = view.findViewById(R.id.reportsContainer);
        reportsListContainer = view.findViewById(R.id.reportsListContainer);
        noReportsTextView = view.findViewById(R.id.noReportsTextView);
    }

    private void setupMap() {
        try {
            mapFragment = (OSMMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
            if (mapFragment == null) {
                mapFragment = new OSMMapFragment();
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mapFragment, mapFragment)
                        .commit();
            }
        } catch (Exception e) {
            // Map initialization failed - set to null but don't hide container
            mapFragment = null;
            e.printStackTrace();
        }
    }

    private void loadRideDetails() {
        repository.getRideDetails(rideId, new AdminHistoryRepository.AdminHistoryCallback<AdminHistoryDetailedModel>() {
            @Override
            public void onSuccess(AdminHistoryDetailedModel data) {
                ride = data;
                displayRideDetails();
            }

            @Override
            public void onError(int code, String message) {
                Toast.makeText(requireContext(), "Error loading ride details: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRideDetails() {
        if (ride == null) {
            Toast.makeText(requireContext(), "No ride data available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Basic info
            startTextView.setText(ride.getStart() != null ? ride.getStart() : "N/A");
            endTextView.setText(ride.getEnd() != null ? ride.getEnd() : "N/A");
            departureTextView.setText(ride.getDeparture() != null ? ride.getDeparture() : "N/A");
            destinationTextView.setText(ride.getDestination() != null ? ride.getDestination() : "N/A");
            priceTextView.setText(String.format("%.2f RSD", ride.getPrice()));

        // Cancelled status
        if (ride.isCancelled()) {
            cancelledTextView.setText(ride.getCancelledBy() != null
                    ? "Yes (" + ride.getCancelledBy() + ")"
                    : "Yes");
            cancelledTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
        } else {
            cancelledTextView.setText("No");
            cancelledTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
        }

        // Panic status
        if (ride.isPanic()) {
            panicTextView.setText("YES");
            panicTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark, null));
        } else {
            panicTextView.setText("No");
            panicTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark, null));
        }

        // Driver info
        displayDriverInfo();

        // Passengers
        displayPassengers();

        // Review
        displayReview();

            // Reports
            displayReports();

            // Map - display route with waypoints
            if (ride.getCheckpoints() != null && ride.getCheckpoints().length > 0 && mapFragment != null) {
                displayRouteOnMap(ride.getCheckpoints());
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error displaying details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void displayRouteOnMap(Coordinates[] checkpoints) {
        if (mapFragment == null || checkpoints == null || checkpoints.length == 0) {
            return;
        }

        try {
            // Post the map operations to run after the fragment is fully initialized
            getView().post(() -> {
                try {
                    // Clear existing waypoints
                    mapFragment.clearWaypoints();

                    // Add waypoints for each checkpoint
                    for (Coordinates coord : checkpoints) {
                        GeoPoint point = new GeoPoint(coord.getLatitude(), coord.getLongitude());
                        mapFragment.addWaypoint(point);
                    }

                    // Center map on first checkpoint
                    if (checkpoints.length > 0) {
                        GeoPoint firstPoint = new GeoPoint(checkpoints[0].getLatitude(), checkpoints[0].getLongitude());
                        mapFragment.centerMap(firstPoint, 13.0);
                    }

                    // Calculate and display the route
                    if (checkpoints.length > 1) {
                        mapFragment.calculateRoute();
                    }
                } catch (Exception e) {
                    // Silently fail - map is optional
                }
            });
        } catch (Exception e) {
            // Silently fail - map is optional
        }
    }

    private void displayDriverInfo() {
        if (ride.getDriverId() != null) {
            driverInfoContainer.setVisibility(View.VISIBLE);

            String driverName = (ride.getDriverName() != null ? ride.getDriverName() : "") + " " +
                               (ride.getDriverLastName() != null ? ride.getDriverLastName() : "");
            driverNameTextView.setText(driverName.trim());
            driverEmailTextView.setText(ride.getDriverEmail() != null ? ride.getDriverEmail() : "N/A");
            driverPhoneTextView.setText(ride.getDriverPhoneNumber() != null ? ride.getDriverPhoneNumber() : "N/A");

            String vehicleInfo = (ride.getVehicleMake() != null ? ride.getVehicleMake() : "") + " " +
                                (ride.getVehicleModel() != null ? ride.getVehicleModel() : "");
            vehicleMakeTextView.setText(vehicleInfo.trim());
            vehicleLicensePlateTextView.setText(ride.getVehicleLicensePlate() != null ? ride.getVehicleLicensePlate() : "N/A");
            vehicleColorTextView.setText(ride.getVehicleColor() != null ? ride.getVehicleColor() : "N/A");

            // Load driver photo
            if (ride.getDriverPhotoPath() != null && !ride.getDriverPhotoPath().isEmpty()) {
                String photoUrl = "http://" + BuildConfig.SERVER_IP + ":8080" + ride.getDriverPhotoPath();
                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .circleCrop()
                        .into(driverPhotoImageView);
            } else {
                driverPhotoImageView.setImageResource(R.drawable.ic_profile_placeholder);
            }
        } else {
            driverInfoContainer.setVisibility(View.GONE);
        }
    }

    private void displayPassengers() {
        try {
            if (ride.getPassengers() != null && !ride.getPassengers().isEmpty()) {
                passengersListContainer.setVisibility(View.VISIBLE);
                noPassengersTextView.setVisibility(View.GONE);
                passengersListContainer.removeAllViews();

                for (AdminHistoryDetailedModel.PassengerInfo passenger : ride.getPassengers()) {
                    View passengerView = LayoutInflater.from(requireContext())
                            .inflate(R.layout.item_passenger_info, passengersListContainer, false);

                    TextView nameTextView = passengerView.findViewById(R.id.passengerNameTextView);
                    TextView emailTextView = passengerView.findViewById(R.id.passengerEmailTextView);

                    String fullName = (passenger.getPassengerName() != null ? passenger.getPassengerName() : "") + " " +
                                     (passenger.getPassengerLastName() != null ? passenger.getPassengerLastName() : "");
                    nameTextView.setText(fullName.trim());
                    emailTextView.setText(passenger.getPassengerEmail() != null ? passenger.getPassengerEmail() : "N/A");

                    passengersListContainer.addView(passengerView);
                }
            } else {
                passengersListContainer.setVisibility(View.GONE);
                noPassengersTextView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            passengersListContainer.setVisibility(View.GONE);
            noPassengersTextView.setVisibility(View.VISIBLE);
        }
    }

    private void displayReview() {
        try {
            if (ride.isHasReview()) {
                reviewContentContainer.setVisibility(View.VISIBLE);
                noReviewTextView.setVisibility(View.GONE);

                // Driver rating
                if (ride.getDriverRating() != null) {
                    driverRatingTextView.setText(ride.getDriverRating() + "/5");
                    displayStars(driverStarsContainer, ride.getDriverRating());
                }

                // Car rating
                if (ride.getCarRating() != null) {
                    carRatingTextView.setText(ride.getCarRating() + "/5");
                    displayStars(carStarsContainer, ride.getCarRating());
                }

                // Comment
                if (ride.getReviewComment() != null && !ride.getReviewComment().isEmpty()) {
                    reviewCommentTextView.setVisibility(View.VISIBLE);
                    reviewCommentTextView.setText("\"" + ride.getReviewComment() + "\"");
                } else {
                    reviewCommentTextView.setVisibility(View.GONE);
                }
            } else {
                reviewContentContainer.setVisibility(View.GONE);
                noReviewTextView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            reviewContentContainer.setVisibility(View.GONE);
            noReviewTextView.setVisibility(View.VISIBLE);
        }
    }

    private void displayStars(LinearLayout container, int rating) {
        container.removeAllViews();
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(48, 48);
            params.setMargins(4, 0, 4, 0);
            star.setLayoutParams(params);

            if (i < rating) {
                star.setImageResource(R.drawable.ic_star_filled);
            } else {
                star.setImageResource(R.drawable.ic_star_outline);
            }
            container.addView(star);
        }
    }

    private void displayReports() {
        try {
            if (ride.getReports() != null && !ride.getReports().isEmpty()) {
                reportsListContainer.setVisibility(View.VISIBLE);
                noReportsTextView.setVisibility(View.GONE);
                reportsListContainer.removeAllViews();

                for (AdminHistoryDetailedModel.ReportInfo report : ride.getReports()) {
                    View reportView = LayoutInflater.from(requireContext())
                            .inflate(R.layout.item_report_info, reportsListContainer, false);

                    TextView reporterTextView = reportView.findViewById(R.id.reporterNameTextView);
                    TextView messageTextView = reportView.findViewById(R.id.reportMessageTextView);

                    reporterTextView.setText(report.getReporterName() != null ? report.getReporterName() : "Unknown");
                    messageTextView.setText(report.getReportMessage() != null ? report.getReportMessage() : "");

                    reportsListContainer.addView(reportView);
                }
            } else {
                reportsListContainer.setVisibility(View.GONE);
                noReportsTextView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            reportsListContainer.setVisibility(View.GONE);
            noReportsTextView.setVisibility(View.VISIBLE);
        }
    }

    private void goBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
