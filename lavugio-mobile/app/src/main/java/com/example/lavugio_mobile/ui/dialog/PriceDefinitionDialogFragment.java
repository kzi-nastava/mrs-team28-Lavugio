package com.example.lavugio_mobile.ui.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.RidePriceModel;
import com.example.lavugio_mobile.services.PriceService;

public class PriceDefinitionDialogFragment extends DialogFragment {

    private RidePriceModel vehiclePrices = new RidePriceModel();
    private String currentVehicleType = "standard";
    private String previousVehicleType = "standard";

    // Views
    private LinearLayout formContainer;
    private LinearLayout loadingContainer;
    private LinearLayout errorContainer;
    private LinearLayout successContainer;
    
    private Spinner spinnerVehicleType;
    private EditText etPricePerType;
    private EditText etPricePerKm;
    private Button btnSave;
    private Button btnRetry;
    private Button btnClose;
    private ImageButton btnCloseHeader;
    private ProgressBar progressBar;
    private TextView tvError;

    private boolean isSaving = false;

    public static PriceDefinitionDialogFragment newInstance() {
        return new PriceDefinitionDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View overlay = inflater.inflate(R.layout.dialog_overlay_wrapper, container, false);
        ViewGroup contentHolder = overlay.findViewById(R.id.dialog_content_holder);
        inflater.inflate(R.layout.dialog_price_definition, contentHolder, true);

        overlay.setOnClickListener(v -> dismiss());
        contentHolder.setOnClickListener(v -> { /* consume click */ });

        return overlay;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        formContainer = view.findViewById(R.id.price_form_container);
        loadingContainer = view.findViewById(R.id.price_loading_container);
        errorContainer = view.findViewById(R.id.price_error_container);
        successContainer = view.findViewById(R.id.price_success_container);
        
        spinnerVehicleType = view.findViewById(R.id.spinner_vehicle_type);
        etPricePerType = view.findViewById(R.id.et_price_per_type);
        etPricePerKm = view.findViewById(R.id.et_price_per_km);
        btnSave = view.findViewById(R.id.btn_save_price);
        btnRetry = view.findViewById(R.id.btn_retry_price);
        btnClose = view.findViewById(R.id.btn_close_price);
        btnCloseHeader = view.findViewById(R.id.btn_close_price_header);
        progressBar = view.findViewById(R.id.progress_bar_price);
        tvError = view.findViewById(R.id.tv_price_error);

        // Setup close buttons
        btnCloseHeader.setOnClickListener(v -> dismiss());
        btnClose.setOnClickListener(v -> dismiss());
        btnRetry.setOnClickListener(v -> loadPrices());

        // Setup spinner
        setupVehicleTypeSpinner();

        // Setup text watchers
        setupTextWatchers();

        // Save button
        btnSave.setOnClickListener(v -> savePrices());

        // Load initial prices
        loadPrices();
    }

    private void setupVehicleTypeSpinner() {
        String[] vehicleTypes = {"Standard", "Luxury", "Combi"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                vehicleTypes
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerVehicleType.setAdapter(adapter);

        spinnerVehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Save current price before switching
                if (previousVehicleType != null && !previousVehicleType.equals("kilometer")) {
                    try {
                        double currentPrice = Double.parseDouble(etPricePerType.getText().toString());
                        vehiclePrices.setPriceForType(previousVehicleType, currentPrice);
                    } catch (NumberFormatException ignored) {}
                }

                // Update current type
                String[] types = {"standard", "luxury", "combi"};
                currentVehicleType = types[position];
                
                // Load price for new type
                double newPrice = vehiclePrices.getPriceForType(currentVehicleType);
                etPricePerType.setText(String.valueOf( newPrice));
                
                previousVehicleType = currentVehicleType;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupTextWatchers() {
        // Price per type watcher
        etPricePerType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double price = Double.parseDouble(s.toString());
                    vehiclePrices.setPriceForType(currentVehicleType, price);
                } catch (NumberFormatException ignored) {}
                updateSaveButtonState();
            }
        });

        // Price per km watcher
        etPricePerKm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double price = Double.parseDouble(s.toString());
                    vehiclePrices.setKilometer(price);
                } catch (NumberFormatException ignored) {}
                updateSaveButtonState();
            }
        });
    }

    private void updateSaveButtonState() {
        boolean isValid = !etPricePerType.getText().toString().isEmpty()
                && !etPricePerKm.getText().toString().isEmpty()
                && !isSaving;
        
        try {
            double typePrice = Double.parseDouble(etPricePerType.getText().toString());
            double kmPrice = Double.parseDouble(etPricePerKm.getText().toString());
            isValid = isValid && typePrice >= 0 && kmPrice >= 0;
        } catch (NumberFormatException e) {
            isValid = false;
        }
        
        btnSave.setEnabled(isValid);
    }

    private void loadPrices() {
        showLoading();

        PriceService priceService = LavugioApp.getPriceService();
        
        priceService.getPrices(new PriceService.Callback<RidePriceModel>() {
            @Override
            public void onSuccess(RidePriceModel prices) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    vehiclePrices = prices;
                    
                    // Update UI with loaded prices
                    etPricePerKm.setText(String.valueOf(prices.getKilometer()));
                    
                    double currentPrice = prices.getPriceForType(currentVehicleType);
                    etPricePerType.setText(String.valueOf(currentPrice));
                    
                    showForm();
                });
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    android.util.Log.e("PriceDialog", "Load failed: " + message);
                    showError();
                });
            }
        });
    }

    private void savePrices() {
        if (isSaving) return;

        isSaving = true;
        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        // Update current vehicle type price
        try {
            double typePrice = Double.parseDouble(etPricePerType.getText().toString());
            double kmPrice = Double.parseDouble(etPricePerKm.getText().toString());
            
            vehiclePrices.setPriceForType(currentVehicleType, typePrice);
            vehiclePrices.setKilometer(kmPrice);
        } catch (NumberFormatException e) {
            isSaving = false;
            btnSave.setEnabled(true);
            btnSave.setText("Save Price");
            return;
        }

        PriceService priceService = LavugioApp.getPriceService();
        
        priceService.postPrices(vehiclePrices, new PriceService.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    isSaving = false;
                    showSuccess();
                });
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    android.util.Log.e("PriceDialog", "Save failed: " + message);
                    isSaving = false;
                    btnSave.setEnabled(true);
                    btnSave.setText("Save Price");
                    tvError.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void showLoading() {
        formContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        successContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.VISIBLE);
    }

    private void showError() {
        formContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.GONE);
        successContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
    }

    private void showForm() {
        loadingContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        successContainer.setVisibility(View.GONE);
        formContainer.setVisibility(View.VISIBLE);
    }

    private void showSuccess() {
        formContainer.setVisibility(View.GONE);
        loadingContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        successContainer.setVisibility(View.VISIBLE);
    }


}