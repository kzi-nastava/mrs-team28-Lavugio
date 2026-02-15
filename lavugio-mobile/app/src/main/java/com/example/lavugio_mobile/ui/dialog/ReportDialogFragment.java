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
import com.example.lavugio_mobile.models.RideReport;
import com.example.lavugio_mobile.services.RideService;

public class ReportDialogFragment extends DialogFragment {

    private static final String ARG_RIDE_ID = "rideId";
    private static final int MAX_LENGTH = 256;

    private long rideId;
    private RideService rideService;
    private Runnable onSuccessListener;

    private EditText etComment;
    private TextView tvCharCount;
    private Button btnSend;
    private TextView tvError;
    private LinearLayout formContainer;
    private LinearLayout successContainer;
    private ImageButton btnClose;

    public static ReportDialogFragment newInstance(long rideId) {
        ReportDialogFragment fragment = new ReportDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnReportSuccessListener(Runnable listener) {
        this.onSuccessListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }

        // Get the shared instance from LavugioApp — already initialized with WebSocket
        rideService = LavugioApp.getRideService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View overlay = inflater.inflate(R.layout.dialog_overlay_wrapper, container, false);
        ViewGroup contentHolder = overlay.findViewById(R.id.dialog_content_holder);
        inflater.inflate(R.layout.dialog_report, contentHolder, true);

        overlay.setOnClickListener(v -> dismiss());
        contentHolder.setOnClickListener(v -> { /* consume click */ });

        return overlay;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etComment = view.findViewById(R.id.et_report_comment);
        tvCharCount = view.findViewById(R.id.tv_report_char_count);
        btnSend = view.findViewById(R.id.btn_send_report);
        tvError = view.findViewById(R.id.tv_report_error);
        formContainer = view.findViewById(R.id.report_form_container);
        successContainer = view.findViewById(R.id.report_success_container);
        btnClose = view.findViewById(R.id.btn_close_report);

        Button btnCloseSuccess = view.findViewById(R.id.btn_report_close_success);

        btnClose.setOnClickListener(v -> dismiss());
        btnCloseSuccess.setOnClickListener(v -> dismiss());

        btnSend.setEnabled(false);

        etComment.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String cleaned = s.toString().replaceAll("[^a-zA-Z0-9 .,!?\\-]", "");
                if (cleaned.length() > MAX_LENGTH) cleaned = cleaned.substring(0, MAX_LENGTH);
                if (!cleaned.equals(s.toString())) {
                    etComment.setText(cleaned);
                    etComment.setSelection(cleaned.length());
                }
                tvCharCount.setText(cleaned.length() + " / " + MAX_LENGTH);
                btnSend.setEnabled(cleaned.length() > 0);
            }
        });

        btnSend.setOnClickListener(v -> sendReport());
    }

    private void sendReport() {
        String comment = etComment.getText().toString().trim();
        if (comment.isEmpty()) return;

        btnSend.setEnabled(false);
        btnSend.setText("Sending...");
        tvError.setVisibility(View.GONE);

        RideReport report = new RideReport(rideId, comment, 0);

        rideService.postRideReport(rideId, report, new RideService.Callback<RideReport>() {
            @Override
            public void onSuccess(RideReport result) {
                if (!isAdded()) return;
                formContainer.setVisibility(View.GONE);
                successContainer.setVisibility(View.VISIBLE);
                if (onSuccessListener != null) onSuccessListener.run();
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;
                btnSend.setEnabled(true);
                btnSend.setText("Report");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }
}