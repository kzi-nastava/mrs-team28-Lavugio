package com.example.lavugio_mobile.ui.profile;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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
import com.example.lavugio_mobile.models.user.UserProfileData;
import com.example.lavugio_mobile.services.ApiClient;
import com.example.lavugio_mobile.services.auth.AuthService;
import com.example.lavugio_mobile.services.user.UserApi;
import com.example.lavugio_mobile.ui.profile.views.ProfileButtonRowView;
import com.example.lavugio_mobile.ui.profile.views.ProfileHeaderView;
import com.example.lavugio_mobile.ui.profile.views.ProfileInfoRowView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private ProfileHeaderView headerView;
    private LinearLayout infoContainer;
    private boolean isProfileEditMode = false;
    private List<ProfileInfoRowView> editableRows = new ArrayList<>();
    private AuthService authService;

    private UserApi userApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        headerView = view.findViewById(R.id.profile_header);
        infoContainer = view.findViewById(R.id.info_container);

        this.authService = AuthService.getInstance();
        userApi = ApiClient.getInstance().create(UserApi.class);

        // Load profile data
        loadProfileData();
        loadProfilePhoto();
    }

    private void loadProfileData() {
        userApi.getProfile().enqueue(new Callback<UserProfileData>() {
            @Override
            public void onResponse(Call<UserProfileData> call, Response<UserProfileData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileData profileData = response.body();
                    fillData(profileData);
                } else if (response.code() == 401) {
                    Toast.makeText(requireContext(), "Unauthorized access", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Unexpected error occured", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileData> call, Throwable throwable) {
                Toast.makeText(requireContext(), "Unexpected error occured: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fillData(UserProfileData profileData) {
        headerView.setName(profileData.getName() + " " + profileData.getSurname());
        headerView.setUserType(this.getUserTypeDisplay());
        headerView.setEmail(profileData.getEmail());

        infoContainer.removeAllViews();
        editableRows.clear();

        addEditableInfoRow("Name", profileData.getName(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        addEditableInfoRow("Surname", profileData.getSurname(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        addEditableInfoRow("Phone number", profileData.getPhoneNumber(), InputType.TYPE_CLASS_PHONE);
        addNonEditableInfoRow("Email", profileData.getEmail());
        addEditableInfoRow("Address", profileData.getAddress(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        // Add driver-specific rows if user is a driver
        if (Objects.equals(authService.getUserRole(), "DRIVER")) {
            addVehicleTitle("Vehicle Information");
            addEditableInfoRow("Make", profileData.getVehicleMake(), InputType.TYPE_CLASS_TEXT);
            addEditableInfoRow("Model", profileData.getVehicleModel(), InputType.TYPE_CLASS_TEXT);
            addDropdownInfoRow("Vehicle Type", profileData.getVehicleType(), new String[]{"Standard", "Luxury", "Combi"});
            addEditableInfoRow("License Plate", profileData.getVehicleLicensePlate(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            addEditableInfoRow("Passenger Seats", profileData.getVehicleSeats().toString(), InputType.TYPE_CLASS_NUMBER);
            addEditableBooleanInfoRow("Pet Friendly", profileData.getVehiclePetFriendly());
            addEditableBooleanInfoRow("Baby Friendly", profileData.getVehicleBabyFriendly());
            addNonEditableInfoRow("Active in last 24h", "4h30min");
        }

        addButtonRow();
    }

    private void loadProfilePhoto() {
        userApi.getProfilePhoto().enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {

                if (response.isSuccessful() && response.body() != null) {

                    byte[] bytes;
                    try {
                        bytes = response.body().bytes();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    requireActivity().runOnUiThread(() -> {
                        headerView.setProfileBitmap(bitmap);
                    });

                } else {
                    loadDefaultAvatar();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Failed to load image",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDefaultAvatar() {
        String encodedName = Uri.encode(authService.getStoredUser().getName());
        String avatarUrl = "https://ui-avatars.com/api/?name="
                + encodedName
                + "&background=606C38&color=fff&size=200&format=png";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(avatarUrl)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onResponse(okhttp3.Call call,
                                   okhttp3.Response response) throws IOException {

                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        return;
                    }

                    byte[] bytes = response.body().bytes();
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    if (bitmap != null && getActivity() != null) {
                        requireActivity().runOnUiThread(() -> headerView.setProfileBitmap(bitmap));
                    }
                } finally {
                    response.close();
                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }
        });
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

    private void addDropdownInfoRow(String label, String value, String[] options) {
        ProfileInfoRowView row = new ProfileInfoRowView(getContext());
        row.setData(label, value);
        row.setDropdownOptions(options);
        row.setEditable(true);
        row.setEditMode(isProfileEditMode);
        editableRows.add(row);
        infoContainer.addView(row);
    }

    private void addEditableBooleanInfoRow(String label, boolean value) {
        ProfileInfoRowView row = new ProfileInfoRowView(getContext());
        row.setLabel(label);
        row.setCheckboxMode(value);
        row.setEditable(true);
        row.setEditMode(isProfileEditMode);
        editableRows.add(row);
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

    private String getUserTypeDisplay() {
        String role = authService.getUserRole();
        switch (role) {
            case "DRIVER":
                return "Driver";
            case "ADMIN":
                return "Administrator";
            case "PASSENGER":
            default:
                return "Regular User";
        }
    }

    private void addButtonRow() {
        ProfileButtonRowView buttonRow = new ProfileButtonRowView(getContext());
        String role = authService.getUserRole();
        // Configure based on user role

        // Change right button text based on mode
        if (isProfileEditMode) {
            buttonRow.setRightButtonText("Save");
            buttonRow.setLeftButtonVisible(true);
            buttonRow.setLeftButtonText("Change Password");
        } else {
            if (Objects.equals(role, "DRIVER")) {
                buttonRow.setLeftButtonVisible(true);
                buttonRow.setLeftButtonText("Activate");
            } else {
                // Hide left button for regular users and admins
                buttonRow.setLeftButtonVisible(false);
            }
            buttonRow.setRightButtonText("Edit");
        }

        // Set click listener
        buttonRow.setOnButtonClickListener(new ProfileButtonRowView.OnButtonClickListener() {
            @Override
            public void onLeftButtonClick() {
                if (isProfileEditMode) {
                    changePassword();
                } else {
                    handleActivationToggle();
                }

            }

            @Override
            public void onRightButtonClick() {
                if (isProfileEditMode) {
                    saveProfileChanges();
                } else {
                    handleEditProfile();
                }
            }
        });

        infoContainer.addView(buttonRow);
    }

    private void handleEditProfile() {
        isProfileEditMode = !isProfileEditMode;

        // Toggle edit mode on all editable rows
        for (ProfileInfoRowView row : editableRows) {
            row.setEditMode(isProfileEditMode);
        }

        // Update button row
        updateButtonRow();
    }

    private void updateButtonRow() {
        View lastView = infoContainer.getChildAt(infoContainer.getChildCount() - 1);
        if (lastView instanceof ProfileButtonRowView) {
            infoContainer.removeView(lastView);
        }

        addButtonRow();
    }

    private void saveProfileChanges() {
        // Collect data from editable rows
        /*for (ProfileInfoRowView row : editableRows) {
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
                case "Pet Friendly":
                    // For boolean fields, use getBooleanValue() to get the checkbox state
                    if (driver.getVehicle() != null) {
                        boolean petFriendly = row.getBooleanValue();
                        driver.getVehicle().setPetFriendly(petFriendly);
                    }
                    break;
                case "Baby Friendly":
                    // For boolean fields, use getBooleanValue() to get the checkbox state
                    if (driver.getVehicle() != null) {
                        boolean babyFriendly = row.getBooleanValue();
                        driver.getVehicle().setBabyFriendly(babyFriendly);
                    }
                    break;
            }
        }*/

        // TODO: Send updated data to backend API
        // Example: viewModel.updateDriverProfile(driver);

        // Exit edit mode
        isProfileEditMode = false;

        // Reload data to show updated values
        loadProfileData();

        // Show confirmation
        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void handleActivationToggle() {
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

    private void changePassword() {
        Toast.makeText(getContext(), "Change password", Toast.LENGTH_SHORT).show();
    }
}