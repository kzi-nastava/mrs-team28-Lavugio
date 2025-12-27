package com.example.lavugio_mobile.ui.profile;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.data.model.user.Driver;
import com.example.lavugio_mobile.data.model.user.UserType;
import com.example.lavugio_mobile.data.model.vehicle.Vehicle;
import com.example.lavugio_mobile.ui.profile.views.ProfileButtonRowView;
import com.example.lavugio_mobile.ui.profile.views.ProfileHeaderView;
import com.example.lavugio_mobile.ui.profile.views.ProfileInfoRowView;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private ProfileHeaderView headerView;
    private LinearLayout infoContainer;
    private boolean isProfileEditMode = false;
    private List<ProfileInfoRowView> editableRows = new ArrayList<>();
    private Driver currentDriver;

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
        currentDriver = createMockDriver();

        // Set header data
        headerView.setName(currentDriver.getFullName());
        headerView.setUserType(getUserTypeDisplay(currentDriver.getRole()));
        headerView.setEmail(currentDriver.getEmail());

        // Clear any existing rows
        infoContainer.removeAllViews();
        editableRows.clear();

        // Add info rows based on user type
        addEditableInfoRow("Name", currentDriver.getFirstName(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        addEditableInfoRow("Surname", currentDriver.getLastName(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        addEditableInfoRow("Phone number", currentDriver.getPhoneNumber(), InputType.TYPE_CLASS_PHONE);
        addNonEditableInfoRow("Email", currentDriver.getEmail());
        addEditableInfoRow("Address", currentDriver.getAddress(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        // Add driver-specific rows if user is a driver
        if (currentDriver.getRole() == UserType.DRIVER) {
            addVehicleTitle("Vehicle Information");
            addEditableInfoRow("Make", currentDriver.getVehicle().getMake(), InputType.TYPE_CLASS_TEXT);
            addEditableInfoRow("Model", currentDriver.getVehicle().getModel(), InputType.TYPE_CLASS_TEXT);
            addNonEditableInfoRow("Active in last 24h", "4h30min");
            addEditableInfoRow("License Plate", currentDriver.getVehicle().getLicensePlate(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

            addBooleanInfoRow("Pet Friendly", currentDriver.getVehicle().isPetFriendly());
            addBooleanInfoRow("Baby Friendly", currentDriver.getVehicle().isBabyFriendly());
        }

        addButtonRow(currentDriver);
    }

    private void addEditableInfoRow(String label, String value, int inputType) {
        ProfileInfoRowView row = new ProfileInfoRowView(getContext());
        row.setData(label, value);
        row.setInputType(inputType);
        row.setEditable(true);
        row.setEditMode(isProfileEditMode);
        editableRows.add(row);
        infoContainer.addView(row);
    }

    private void addNonEditableInfoRow(String label, String value) {
        ProfileInfoRowView row = new ProfileInfoRowView(getContext());
        row.setData(label, value);
        row.setEditable(false);
        infoContainer.addView(row);
    }

    private void addInfoRow(String label, String value) {
        ProfileInfoRowView row = new ProfileInfoRowView(getContext());
        row.setLabel(label);
        row.setValue(value);
        infoContainer.addView(row);
    }

    private void addBooleanInfoRow(String label, boolean value) {
        if (value) {
            addInfoRow(label, "Yes");
        } else {
            addInfoRow(label, "No");
        }
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

    private void addButtonRow(Driver driver) {
        ProfileButtonRowView buttonRow = new ProfileButtonRowView(getContext());

        // Configure based on user role
        if (driver.getRole() == UserType.DRIVER) {
            // Show left button for drivers
            buttonRow.setLeftButtonVisible(true);
            buttonRow.setLeftButtonText("Activate");
            // Enable/disable based on verification status
            // buttonRow.setLeftButtonEnabled(driver.isVerified());
        } else {
            // Hide left button for regular users and admins
            buttonRow.setLeftButtonVisible(false);
        }

        // Change right button text based on mode
        if (isProfileEditMode) {
            buttonRow.setRightButtonText("Save");
        } else {
            buttonRow.setRightButtonText("Edit");
        }

        // Set click listener
        buttonRow.setOnButtonClickListener(new ProfileButtonRowView.OnButtonClickListener() {
            @Override
            public void onLeftButtonClick() {
                handleActivationToggle(driver);
            }

            @Override
            public void onRightButtonClick() {
                if (isProfileEditMode) {
                    saveProfileChanges(driver);
                } else {
                    handleEditProfile(driver);
                }
            }
        });

        infoContainer.addView(buttonRow);
    }

    private void handleEditProfile(Driver driver) {
        isProfileEditMode = !isProfileEditMode;

        // Toggle edit mode on all editable rows
        for (ProfileInfoRowView row : editableRows) {
            row.setEditMode(isProfileEditMode);
        }

        // Update button row
        updateButtonRow(driver);
    }

    private void updateButtonRow(Driver driver) {
        // Remove and re-add button row with updated state
        View lastView = infoContainer.getChildAt(infoContainer.getChildCount() - 1);
        if (lastView instanceof ProfileButtonRowView) {
            infoContainer.removeView(lastView);
        }

        addButtonRow(driver);
    }

    private void saveProfileChanges(Driver driver) {
        // Collect data from editable rows
        for (ProfileInfoRowView row : editableRows) {
            String label = row.getLabel();
            String value = row.getValue();

            // Update driver object based on label
            switch (label) {
                case "Name":
                    driver.setFirstName(value);
                    break;
                case "Surname":
                    driver.setLastName(value);
                    break;
                case "Phone number":
                    driver.setPhoneNumber(value);
                    break;
                case "Address":
                    driver.setAddress(value);
                    break;
                case "Vehicle":
                    if (driver.getVehicle() != null) {
                        // Parse vehicle full name (e.g., "Toyota Camry")
                        String[] parts = value.split(" ", 2);
                        if (parts.length >= 1) {
                            driver.getVehicle().setMake(parts[0]);
                        }
                        if (parts.length >= 2) {
                            driver.getVehicle().setModel(parts[1]);
                        }
                    }
                    break;
                case "License Plate":
                    if (driver.getVehicle() != null) {
                        driver.getVehicle().setLicensePlate(value);
                    }
                    break;
            }
        }

        // TODO: Send updated data to backend API
        // Example: viewModel.updateDriverProfile(driver);

        // Exit edit mode
        isProfileEditMode = false;

        // Reload data to show updated values
        loadProfileData();

        // Show confirmation
        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void handleActivationToggle(Driver driver) {
        // TODO: Implement activation toggle
        // if (!driver.isVerified()) {
        //     Toast.makeText(getContext(), "You must be verified to activate", Toast.LENGTH_SHORT).show();
        //     return;
        // }

        // Toggle availability
        // boolean newAvailability = !driver.isAvailable();
        // driver.setAvailable(newAvailability);

        // TODO: Send to backend API
        // viewModel.updateDriverAvailability(newAvailability);

        // Show confirmation
        Toast.makeText(getContext(), "Activation toggled", Toast.LENGTH_SHORT).show();
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
        driver.setAddress("Bulevar Kralja Aleksandra 30, Beograd, 11000");

        // Mock vehicle data
        Vehicle vehicle = new Vehicle();
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setColor("Silver");
        vehicle.setLicensePlate("BG-123-AB");
        vehicle.setBabyFriendly(true);
        vehicle.setPetFriendly(false);

        driver.setVehicle(vehicle);

        return driver;
    }
}