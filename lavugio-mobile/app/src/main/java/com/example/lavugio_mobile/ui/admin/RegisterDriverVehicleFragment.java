package com.example.lavugio_mobile.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.data.model.user.DriverRegistrationData;

import java.util.regex.Pattern;

public class RegisterDriverVehicleFragment extends Fragment {
    private EditText etVehicleMake, etVehicleModel, etLicensePlate, etSeats, etVehicleColor;
    private Spinner spinnerVehicleType;
    private CheckBox cbPetFriendly, cbBabyFriendly;
    private Button btnPrevious, btnFinish;

    private DriverRegistrationData registrationData;

    // License plate pattern: AB-12345-CD
    private static final Pattern LICENSE_PLATE_PATTERN = Pattern.compile("^[A-Z]{2}-\\d{5}-[A-Z]{2}$");

    public static RegisterDriverVehicleFragment newInstance(DriverRegistrationData data) {
        RegisterDriverVehicleFragment fragment = new RegisterDriverVehicleFragment();
        fragment.registrationData = data;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_driver_vehicle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        etVehicleMake = view.findViewById(R.id.etVehicleMake);
        etVehicleModel = view.findViewById(R.id.etVehicleModel);
        etLicensePlate = view.findViewById(R.id.etLicensePlate);
        etSeats = view.findViewById(R.id.etSeats);
        etVehicleColor = view.findViewById(R.id.etVehicleColor);
        spinnerVehicleType = view.findViewById(R.id.spinnerVehicleType);
        cbPetFriendly = view.findViewById(R.id.cbPetFriendly);
        cbBabyFriendly = view.findViewById(R.id.cbBabyFriendly);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnFinish = view.findViewById(R.id.btnFinish);

        // Setup spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.vehicle_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicleType.setAdapter(adapter);

        // Load existing data if any
        if (registrationData != null) {
            etVehicleMake.setText(registrationData.getVehicleMake());
            etVehicleModel.setText(registrationData.getVehicleModel());
            etLicensePlate.setText(registrationData.getLicensePlate());
            if (registrationData.getSeats() > 0) {
                etSeats.setText(String.valueOf(registrationData.getSeats()));
            }
            etVehicleColor.setText(registrationData.getVehicleColor());
            cbPetFriendly.setChecked(registrationData.isPetFriendly());
            cbBabyFriendly.setChecked(registrationData.isBabyFriendly());
        }

        // Add text watchers for validation
        TextWatcher validationWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateForm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etVehicleMake.addTextChangedListener(validationWatcher);
        etVehicleModel.addTextChangedListener(validationWatcher);
        etLicensePlate.addTextChangedListener(validationWatcher);
        etSeats.addTextChangedListener(validationWatcher);
        etVehicleColor.addTextChangedListener(validationWatcher);

        // Previous button
        btnPrevious.setOnClickListener(v -> {
            saveData();
            ((RegisterDriverFragment) getParentFragment()).showDriverInformation();
        });

        // Finish button
        btnFinish.setOnClickListener(v -> {
            if (validateForm()) {
                saveData();
                ((RegisterDriverFragment) getParentFragment()).finishRegistration();
            }
        });

        // Initial validation
        validateForm();
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validate vehicle make
        String make = etVehicleMake.getText().toString().trim();
        if (make.isEmpty()) {
            etVehicleMake.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
            isValid = false;
        } else {
            etVehicleMake.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field));
        }

        // Validate vehicle model
        String model = etVehicleModel.getText().toString().trim();
        if (model.isEmpty()) {
            etVehicleModel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
            isValid = false;
        } else {
            etVehicleModel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field));
        }

        // Validate license plate
        String plate = etLicensePlate.getText().toString().trim();
        if (plate.isEmpty() || !LICENSE_PLATE_PATTERN.matcher(plate).matches()) {
            etLicensePlate.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
            isValid = false;
        } else {
            etLicensePlate.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field));
        }

        // Validate seats
        String seatsStr = etSeats.getText().toString().trim();
        if (seatsStr.isEmpty()) {
            etSeats.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
            isValid = false;
        } else {
            try {
                int seats = Integer.parseInt(seatsStr);
                if (seats < 1 || seats > 30) {
                    etSeats.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
                    isValid = false;
                } else {
                    etSeats.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field));
                }
            } catch (NumberFormatException e) {
                etSeats.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
                isValid = false;
            }
        }

        // Validate color
        String color = etVehicleColor.getText().toString().trim();
        if (color.isEmpty()) {
            etVehicleColor.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
            isValid = false;
        } else {
            etVehicleColor.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field));
        }

        // Enable/disable button
        btnFinish.setEnabled(isValid);

        return isValid;
    }

    private void saveData() {
        if (registrationData == null) {
            registrationData = new DriverRegistrationData();
        }

        registrationData.setVehicleMake(etVehicleMake.getText().toString().trim());
        registrationData.setVehicleModel(etVehicleModel.getText().toString().trim());
        registrationData.setLicensePlate(etLicensePlate.getText().toString().trim());

        String seatsStr = etSeats.getText().toString().trim();
        if (!seatsStr.isEmpty()) {
            registrationData.setSeats(Integer.parseInt(seatsStr));
        }

        registrationData.setVehicleColor(etVehicleColor.getText().toString().trim());
        registrationData.setVehicleType(spinnerVehicleType.getSelectedItem().toString());
        registrationData.setPetFriendly(cbPetFriendly.isChecked());
        registrationData.setBabyFriendly(cbBabyFriendly.isChecked());

        ((RegisterDriverFragment) getParentFragment()).setRegistrationData(registrationData);
    }
}
