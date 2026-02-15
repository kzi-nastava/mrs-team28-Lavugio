package com.example.lavugio_mobile.ui.dialog;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.RideReview;
import com.example.lavugio_mobile.services.RideService;

public class ReviewDialogFragment extends DialogFragment {

    private static final String ARG_RIDE_ID = "rideId";
    private static final int MAX_LENGTH = 256;
    private static final int COLOR_STAR_ACTIVE = Color.parseColor("#FACC15");   // yellow-400
    private static final int COLOR_STAR_INACTIVE = Color.parseColor("#D1D5DB"); // gray-300

    private long rideId;
    private int driverRating = 0;
    private int vehicleRating = 0;
    private Runnable onSuccessListener;

    // Views
    private ImageView[] driverStars;
    private ImageView[] vehicleStars;
    private EditText etComment;
    private TextView tvCharCount;
    private Button btnSend;
    private TextView tvError;
    private LinearLayout formContainer;
    private LinearLayout successContainer;
    private ImageButton btnClose;
    private Button btnCloseSuccess;

    public static ReviewDialogFragment newInstance(long rideId) {
        ReviewDialogFragment fragment = new ReviewDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnReviewSuccessListener(Runnable listener) {
        this.onSuccessListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View overlay = inflater.inflate(R.layout.dialog_overlay_wrapper, container, false);
        ViewGroup contentHolder = overlay.findViewById(R.id.dialog_content_holder);
        inflater.inflate(R.layout.dialog_review, contentHolder, true);

        // Close on overlay background click
        overlay.setOnClickListener(v -> dismiss());
        contentHolder.setOnClickListener(v -> { /* consume click */ });

        return overlay;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        etComment = view.findViewById(R.id.et_review_comment);
        tvCharCount = view.findViewById(R.id.tv_review_char_count);
        btnSend = view.findViewById(R.id.btn_send_review);
        tvError = view.findViewById(R.id.tv_review_error);
        formContainer = view.findViewById(R.id.review_form_container);
        successContainer = view.findViewById(R.id.review_success_container);
        btnClose = view.findViewById(R.id.btn_close_review);
        btnCloseSuccess = view.findViewById(R.id.btn_review_close_success);

        // Collect star ImageViews into arrays
        driverStars = new ImageView[] {
                view.findViewById(R.id.driver_star_1),
                view.findViewById(R.id.driver_star_2),
                view.findViewById(R.id.driver_star_3),
                view.findViewById(R.id.driver_star_4),
                view.findViewById(R.id.driver_star_5)
        };

        vehicleStars = new ImageView[] {
                view.findViewById(R.id.vehicle_star_1),
                view.findViewById(R.id.vehicle_star_2),
                view.findViewById(R.id.vehicle_star_3),
                view.findViewById(R.id.vehicle_star_4),
                view.findViewById(R.id.vehicle_star_5)
        };

        // Setup close buttons
        btnClose.setOnClickListener(v -> dismiss());
        btnCloseSuccess.setOnClickListener(v -> dismiss());

        // Setup star click listeners
        setupStarListeners();

        // Initial state: send disabled until both ratings are set
        btnSend.setEnabled(false);
        updateStarColors();

        // Text watcher for character count & input sanitization
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String cleaned = s.toString()
                        .replaceAll("[^a-zA-Z0-9 .,!?\\-]", "");
                if (cleaned.length() > MAX_LENGTH) {
                    cleaned = cleaned.substring(0, MAX_LENGTH);
                }
                if (!cleaned.equals(s.toString())) {
                    etComment.setText(cleaned);
                    etComment.setSelection(cleaned.length());
                }
                tvCharCount.setText(cleaned.length() + " / " + MAX_LENGTH);
            }
        });

        // Send button
        btnSend.setOnClickListener(v -> sendReview());
    }

    // ── Star rating setup ────────────────────────────────

    private void setupStarListeners() {
        for (int i = 0; i < 5; i++) {
            final int rating = i + 1;

            driverStars[i].setOnClickListener(v -> {
                driverRating = rating;
                updateStarColors();
                updateSendButtonState();
            });

            vehicleStars[i].setOnClickListener(v -> {
                vehicleRating = rating;
                updateStarColors();
                updateSendButtonState();
            });
        }
    }

    private void updateStarColors() {
        for (int i = 0; i < 5; i++) {
            driverStars[i].setColorFilter(
                    i < driverRating ? COLOR_STAR_ACTIVE : COLOR_STAR_INACTIVE,
                    PorterDuff.Mode.SRC_IN
            );
            vehicleStars[i].setColorFilter(
                    i < vehicleRating ? COLOR_STAR_ACTIVE : COLOR_STAR_INACTIVE,
                    PorterDuff.Mode.SRC_IN
            );
        }
    }

    private void updateSendButtonState() {
        btnSend.setEnabled(driverRating > 0 && vehicleRating > 0);
    }

    // ── Send review ──────────────────────────────────────

    private void sendReview() {
        if (driverRating == 0 || vehicleRating == 0) return;

        btnSend.setEnabled(false);
        btnSend.setText("Sending...");
        tvError.setVisibility(View.GONE);

        String comment = etComment.getText().toString().trim();

        RideReview review = new RideReview(rideId, driverRating, vehicleRating, comment);

        RideService rideService = LavugioApp.getRideService();

        rideService.postRideReview(rideId, review, new RideService.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    formContainer.setVisibility(View.GONE);
                    successContainer.setVisibility(View.VISIBLE);

                    if (onSuccessListener != null) {
                        onSuccessListener.run();
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    android.util.Log.e("ReviewDialog",
                            "Review failed: code=" + code + " message=" + message);
                    btnSend.setEnabled(true);
                    btnSend.setText("Confirm");
                    tvError.setVisibility(View.VISIBLE);
                });
            }
        });
    }
}