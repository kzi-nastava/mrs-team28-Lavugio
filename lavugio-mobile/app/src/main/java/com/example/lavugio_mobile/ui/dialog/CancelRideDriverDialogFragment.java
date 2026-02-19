package com.example.lavugio_mobile.ui.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.services.RideService;

public class CancelRideDriverDialogFragment extends DialogFragment {

    private static final String ARG_RIDE_ID = "rideId";
    private static final int MAX_LENGTH = 256;

    private long rideId;
    private RideService rideService;
    private Runnable onSuccessListener;

    private EditText etReason;
    private TextView tvCharCount;
    private Button btnSubmit;
    private TextView tvError;
    private LinearLayout formContainer;
    private LinearLayout successContainer;

    public static CancelRideDriverDialogFragment newInstance(long rideId) {
        CancelRideDriverDialogFragment fragment = new CancelRideDriverDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnCancelSuccessListener(Runnable listener) {
        this.onSuccessListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }
        rideService = LavugioApp.getRideService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View overlay = inflater.inflate(R.layout.dialog_overlay_wrapper, container, false);
        ViewGroup contentHolder = overlay.findViewById(R.id.dialog_content_holder);
        inflater.inflate(R.layout.dialog_cancel_ride, contentHolder, true);

        overlay.setOnClickListener(v -> dismiss());
        contentHolder.setOnClickListener(v -> { /* consume click */ });

        return overlay;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etReason      = view.findViewById(R.id.et_cancel_reason);
        tvCharCount   = view.findViewById(R.id.tv_cancel_char_count);
        btnSubmit     = view.findViewById(R.id.btn_submit_cancel);
        tvError       = view.findViewById(R.id.tv_cancel_error);
        formContainer    = view.findViewById(R.id.cancel_form_container);
        successContainer = view.findViewById(R.id.cancel_success_container);

        ImageButton btnClose       = view.findViewById(R.id.btn_close_cancel);
        Button      btnCloseSuccess = view.findViewById(R.id.btn_cancel_close_success);

        btnClose.setOnClickListener(v -> dismiss());
        btnCloseSuccess.setOnClickListener(v -> dismiss());

        btnSubmit.setEnabled(false);

        etReason.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.length() > MAX_LENGTH) {
                    text = text.substring(0, MAX_LENGTH);
                    etReason.setText(text);
                    etReason.setSelection(text.length());
                }
                tvCharCount.setText(text.length() + " / " + MAX_LENGTH);
                btnSubmit.setEnabled(text.trim().length() > 0);
            }
        });

        btnSubmit.setOnClickListener(v -> submitCancellation());
    }

    private void submitCancellation() {
        String reason = etReason.getText().toString().trim();
        if (reason.isEmpty()) return;

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Cancelling...");
        tvError.setVisibility(View.GONE);

        rideService.cancelRideByDriver(rideId, reason, new RideService.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    formContainer.setVisibility(View.GONE);
                    successContainer.setVisibility(View.VISIBLE);
                    if (onSuccessListener != null) onSuccessListener.run();
                });
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Cancel Ride");
                    tvError.setVisibility(View.VISIBLE);
                });
            }
        });
    }
}
