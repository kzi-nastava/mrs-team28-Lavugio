package com.example.lavugio_mobile.ui.admin;

import android.widget.EditText;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.data.model.user.DriverRegistrationData;

import java.util.regex.Pattern;

public class RegisterDriverUserFragment extends Fragment {
    private EditText etEmail, etName, etSurname, etAddress, etPhone;
    private Button btnNext;

    private DriverRegistrationData registrationData;

    public static RegisterDriverUserFragment newInstance(DriverRegistrationData data) {
        RegisterDriverUserFragment fragment = new RegisterDriverUserFragment();
        fragment.registrationData = data;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_driver_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        etEmail = view.findViewById(R.id.etEmail);
        etName = view.findViewById(R.id.etName);
        etSurname = view.findViewById(R.id.etSurname);
        etAddress = view.findViewById(R.id.etAddress);
        etPhone = view.findViewById(R.id.etPhone);
        btnNext = view.findViewById(R.id.btnNext);

        // Load existing data if any
        if (registrationData != null) {
            etEmail.setText(registrationData.getEmail());
            etName.setText(registrationData.getName());
            etSurname.setText(registrationData.getSurname());
            etAddress.setText(registrationData.getAddress());
            etPhone.setText(registrationData.getPhone());
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

        etEmail.addTextChangedListener(validationWatcher);
        etName.addTextChangedListener(validationWatcher);
        etSurname.addTextChangedListener(validationWatcher);
        etAddress.addTextChangedListener(validationWatcher);
        etPhone.addTextChangedListener(validationWatcher);

        // Next button click
        btnNext.setOnClickListener(v -> {
            if (validateForm()) {
                saveData();
                ((RegisterDriverFragment) getParentFragment()).showVehicleInformation();
            }
        });

        // Initial validation
        validateForm();
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validate email
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
            isValid = false;
        } else {
            etEmail.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
        }

        // Validate name
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
            isValid = false;
        } else {
            etName.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field));
        }

        // Validate surname
        String surname = etSurname.getText().toString().trim();
        if (surname.isEmpty()) {
            etSurname.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
            isValid = false;
        } else {
            etSurname.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field));
        }

        // Validate address
        String address = etAddress.getText().toString().trim();
        if (address.isEmpty()) {
            etAddress.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
            isValid = false;
        } else {
            etAddress.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field));
        }

        // Validate phone
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty() || phone.length() < 9) {
            etPhone.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field_error));
            isValid = false;
        } else {
            etPhone.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.text_input_field));
        }

        // Enable/disable button
        btnNext.setEnabled(isValid);

        return isValid;
    }

    private void saveData() {
        if (registrationData == null) {
            registrationData = new DriverRegistrationData();
        }

        registrationData.setEmail(etEmail.getText().toString().trim());
        registrationData.setName(etName.getText().toString().trim());
        registrationData.setSurname(etSurname.getText().toString().trim());
        registrationData.setAddress(etAddress.getText().toString().trim());
        registrationData.setPhone(etPhone.getText().toString().trim());

        ((RegisterDriverFragment) getParentFragment()).setRegistrationData(registrationData);
    }
}
