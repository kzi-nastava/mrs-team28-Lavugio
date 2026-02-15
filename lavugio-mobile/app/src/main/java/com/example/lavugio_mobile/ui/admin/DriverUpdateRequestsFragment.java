package com.example.lavugio_mobile.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.data.model.user.DriverUpdateRequest;
import com.example.lavugio_mobile.data.model.user.DriverUpdateRequestDiff;
import com.example.lavugio_mobile.models.user.DriverEditProfileRequestDTO;
import com.example.lavugio_mobile.models.user.DriverUpdateRequestDiffDTO;
import com.example.lavugio_mobile.ui.dialog.ConfirmDialogFragment;
import com.example.lavugio_mobile.ui.dialog.ErrorDialogFragment;
import com.example.lavugio_mobile.ui.dialog.SuccessDialogFragment;
import com.example.lavugio_mobile.viewmodel.admin.DriverUpdateRequestsViewModel;
import com.example.lavugio_mobile.viewmodel.user.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;

public class DriverUpdateRequestsFragment extends Fragment {

    private TextView tvEmptyState;
    private LinearLayout llRequestsContainer;
    private DriverUpdateRequestsViewModel viewModel;
    private List<DriverUpdateRequestDiffDTO> requests;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_update_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        llRequestsContainer = view.findViewById(R.id.llRequestsContainer);
        viewModel = new ViewModelProvider(this).get(DriverUpdateRequestsViewModel.class);

        viewModel.getDriverUpdateRequests().observe(getViewLifecycleOwner(), requests -> {
            this.requests = requests;
            displayRequests();
        });

        viewModel.fetchDriverUpdateRequests();
    }

    private void displayRequests() {
        if (requests == null || requests.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            llRequestsContainer.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            llRequestsContainer.setVisibility(View.VISIBLE);

            llRequestsContainer.removeAllViews();

            for (DriverUpdateRequestDiffDTO request : requests) {
                View requestCard = createRequestCard(request);
                llRequestsContainer.addView(requestCard);
            }
        }
    }

    private View createRequestCard(DriverUpdateRequestDiffDTO request) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_driver_update_request, llRequestsContainer, false);

        TextView tvEmail = cardView.findViewById(R.id.tvEmail);
        ImageView ivExpandIcon = cardView.findViewById(R.id.ivExpandIcon);
        LinearLayout llCollapsed = cardView.findViewById(R.id.llCollapsed);
        LinearLayout llExpanded = cardView.findViewById(R.id.llExpanded);
        LinearLayout llChangesContainer = cardView.findViewById(R.id.llChangesContainer);
        Button btnReject = cardView.findViewById(R.id.btnReject);
        Button btnApprove = cardView.findViewById(R.id.btnApprove);

        tvEmail.setText(request.getEmail());

        // Populate changes
        populateChanges(llChangesContainer, request);
        setMarqueeEnabled(llChangesContainer, false);

        // Expand/Collapse logic
        final boolean[] isExpanded = {false};
        llCollapsed.setOnClickListener(v -> {
            if (isExpanded[0]) {
                // Collapse
                llExpanded.setVisibility(View.GONE);
                rotateIcon(ivExpandIcon, 180, 0);
                setMarqueeEnabled(llChangesContainer, false);
            } else {
                // Expand
                llExpanded.setVisibility(View.VISIBLE);
                rotateIcon(ivExpandIcon, 0, 180);
                setMarqueeEnabled(llChangesContainer, true);
            }
            isExpanded[0] = !isExpanded[0];
        });

        // Reject button
        btnReject.setOnClickListener(v -> {
            ConfirmDialogFragment.newInstance(
                    "Reject Request",
                    "Are you sure you want to reject this update request?",
                    new ConfirmDialogFragment.ConfirmDialogListener() {
                        @Override
                        public void onConfirm() {
                            handleReject(request);
                        }

                        @Override
                        public void onCancel() {
                            // Do nothing
                        }
                    }
            ).show(getParentFragmentManager(), "confirm_reject");
        });

        // Approve button
        btnApprove.setOnClickListener(v -> {
            ConfirmDialogFragment.newInstance(
                    "Approve Request",
                    "Are you sure you want to approve this update request?",
                    new ConfirmDialogFragment.ConfirmDialogListener() {
                        @Override
                        public void onConfirm() {
                            handleApprove(request);
                        }

                        @Override
                        public void onCancel() {
                            // Do nothing
                        }
                    }
            ).show(getParentFragmentManager(), "confirm_approve");
        });

        return cardView;
    }

    private void populateChanges(LinearLayout container, DriverUpdateRequestDiffDTO request) {
        DriverEditProfileRequestDTO oldData = request.getOldData();
        DriverEditProfileRequestDTO newData = request.getNewData();

        // Check each field for changes
        if (!oldData.getProfile().getName().equals(newData.getProfile().getName())) {
            addChangedField(container, "Name:", oldData.getProfile().getName(), newData.getProfile().getName());
        }

        if (!oldData.getProfile().getSurname().equals(newData.getProfile().getSurname())) {
            addChangedField(container, "Surname:", oldData.getProfile().getSurname(), newData.getProfile().getSurname());
        }

        if (!oldData.getProfile().getPhoneNumber().equals(newData.getProfile().getPhoneNumber())) {
            addChangedField(container, "Phone Number:", oldData.getProfile().getPhoneNumber(), newData.getProfile().getPhoneNumber());
        }

        if (!oldData.getProfile().getAddress().equals(newData.getProfile().getAddress())) {
            addChangedField(container, "Address:", oldData.getProfile().getAddress(), newData.getProfile().getAddress());
        }

        if (!oldData.getVehicleMake().equals(newData.getVehicleMake())) {
            addChangedField(container, "Vehicle Make:", oldData.getVehicleMake(), newData.getVehicleMake());
        }

        if (!oldData.getVehicleModel().equals(newData.getVehicleModel())) {
            addChangedField(container, "Vehicle Model:", oldData.getVehicleModel(), newData.getVehicleModel());
        }

        if (!oldData.getVehicleLicensePlate().equals(newData.getVehicleLicensePlate())) {
            addChangedField(container, "License Plate:", oldData.getVehicleLicensePlate(), newData.getVehicleLicensePlate());
        }

        if (oldData.getVehicleSeats() != newData.getVehicleSeats()) {
            addChangedField(container, "Seats:", String.valueOf(oldData.getVehicleSeats()), String.valueOf(newData.getVehicleSeats()));
        }

        if (oldData.isVehiclePetFriendly() != newData.isVehiclePetFriendly()) {
            addChangedField(container, "Pet Friendly:",
                    oldData.isVehiclePetFriendly() ? "Yes" : "No",
                    newData.isVehiclePetFriendly() ? "Yes" : "No");
        }

        if (oldData.isVehicleBabyFriendly() != newData.isVehicleBabyFriendly()) {
            addChangedField(container, "Baby Friendly:",
                    oldData.isVehicleBabyFriendly() ? "Yes" : "No",
                    newData.isVehicleBabyFriendly() ? "Yes" : "No");
        }

        if (!oldData.getVehicleType().equals(newData.getVehicleType())) {
            addChangedField(container, "Vehicle Type:", oldData.getVehicleType(), newData.getVehicleType());
        }
    }

    private void addChangedField(LinearLayout container, String label, String oldValue, String newValue) {
        View fieldView = LayoutInflater.from(getContext()).inflate(R.layout.item_changed_field, container, false);

        TextView tvFieldLabel = fieldView.findViewById(R.id.tvFieldLabel);
        TextView tvOldValue = fieldView.findViewById(R.id.tvOldValue);
        TextView tvNewValue = fieldView.findViewById(R.id.tvNewValue);

        tvFieldLabel.setText(label);
        tvOldValue.setText(oldValue);
        tvNewValue.setText(newValue);

        container.addView(fieldView);
    }

    private void setMarqueeEnabled(LinearLayout container, boolean enabled) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            TextView tvOldValue = child.findViewById(R.id.tvOldValue);
            TextView tvNewValue = child.findViewById(R.id.tvNewValue);

            if (tvOldValue != null) {
                tvOldValue.setSelected(enabled);
            }

            if (tvNewValue != null) {
                tvNewValue.setSelected(enabled);
            }
        }
    }

    private void rotateIcon(ImageView icon, float fromDegrees, float toDegrees) {
        RotateAnimation rotate = new RotateAnimation(
                fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        icon.startAnimation(rotate);
    }

    private void handleReject(DriverUpdateRequestDiffDTO request) {
        viewModel.rejectEditRequst(request.getRequestId()).observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                requests.remove(request);
                displayRequests();
                SuccessDialogFragment.newInstance("Request Rejected",
                                "Update request has been rejected successfully")
                        .show(getParentFragmentManager(), "success_dialog");
            } else if (success != null) {
                ErrorDialogFragment.newInstance("Error", "Could not reject the edit request.")
                        .show(getParentFragmentManager(), "error_dialog");
            }
        });
    }

    private void handleApprove(DriverUpdateRequestDiffDTO request) {
        viewModel.approveEditRequest(request.getRequestId()).observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                requests.remove(request);
                displayRequests();
                SuccessDialogFragment.newInstance("Request Approved",
                                "Update request has been approved successfully")
                        .show(getParentFragmentManager(), "success_dialog");
            } else if (success != null) {
                ErrorDialogFragment.newInstance("Error", "Could not approve the edit request.")
                        .show(getParentFragmentManager(), "error_dialog");
            }
        });
    }
}