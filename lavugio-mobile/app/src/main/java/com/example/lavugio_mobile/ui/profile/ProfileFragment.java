package com.example.lavugio_mobile.ui.profile;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.data.model.user.Driver;
import com.example.lavugio_mobile.data.model.user.UserType;
import com.example.lavugio_mobile.data.model.vehicle.Vehicle;
import com.example.lavugio_mobile.ui.profile.views.ProfileHeaderView;
import com.example.lavugio_mobile.ui.profile.views.InfoRowView;

public class ProfileFragment extends Fragment {
    private ProfileHeaderView headerView;
    private LinearLayout infoContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        headerView = view.findViewById(R.id.profile_header);
        infoContainer = view.findViewById(R.id.info_container);

        // Load profile data
        loadProfileData();

        return view;
    }

    private void loadProfileData() {
        // For now, using mock data
        // Later this will come from ViewModel and backend
        Driver mockDriver = createMockDriver();

        // Set header data
        headerView.setName(mockDriver.getFullName());
        headerView.setUserType(getUserTypeDisplay(mockDriver.getRole()));
        headerView.setEmail(mockDriver.getEmail());

        // Clear any existing rows
        infoContainer.removeAllViews();

        // Add info rows based on user type
        addInfoRow("Name", mockDriver.getFirstName());
        addInfoRow("Surname", mockDriver.getLastName());
        addInfoRow("Address", mockDriver.getLastName());
        addInfoRow("Phone number", mockDriver.getPhoneNumber());
        addInfoRow("Email", mockDriver.getEmail());

        // Add driver-specific rows if user is a driver
        if (mockDriver.getRole() == UserType.DRIVER) {
            addVehicleTitle("Vehicle Information");
            addInfoRow("Make", mockDriver.getVehicle().getMake());
            addInfoRow("Model", mockDriver.getVehicle().getModel());
            addInfoRow("License Plate", mockDriver.getVehicle().getLicensePlate());
        }
    }

    private void addInfoRow(String label, String value) {
        InfoRowView row = new InfoRowView(getContext());
        row.setLabel(label);
        row.setValue(value);
        infoContainer.addView(row);
    }

    private void addVehicleTitle(String title) {
        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setTextSize(26);
        titleView.setTextColor(Color.parseColor("#606C38"));
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        // Set margins
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Set top and bottom margins (in pixels)
        // Convert dp to pixels for proper scaling
        int topMarginDp = 16; // 16dp top margin
        int bottomMarginDp = 4; // 8dp bottom margin

        float density = getResources().getDisplayMetrics().density;
        int topMarginPx = (int) (topMarginDp * density);
        int bottomMarginPx = (int) (bottomMarginDp * density);

        params.setMargins(0, topMarginPx, 0, bottomMarginPx);
        titleView.setLayoutParams(params);

        infoContainer.addView(titleView);
    }

    private String getUserTypeDisplay(UserType role) {
        switch (role) {
            case DRIVER:
                return "Driver";
            case ADMINISTRATOR:
                return "Administrator";
            case REGULAR_USER:
            default:
                return "Regular User";
        }
    }

    private Driver createMockDriver() {
        // Create mock driver data for UI testing
        Driver driver = new Driver();
        driver.setId(1L);
        driver.setFirstName("Pera");
        driver.setLastName("Perić");
        driver.setEmail("bm230294d@student.etf.bg.ac.rs");
        driver.setPhoneNumber("069123456");
        driver.setLicenseNumber("DL123456789");

        // Mock vehicle data
        Vehicle vehicle = new Vehicle();
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setColor("Silver");
        vehicle.setLicensePlate("BG-123-AB");

        driver.setVehicle(vehicle);

        return driver;
    }
}