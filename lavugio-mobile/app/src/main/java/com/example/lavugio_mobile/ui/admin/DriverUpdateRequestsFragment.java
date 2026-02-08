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

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.data.model.user.DriverUpdateRequest;
import com.example.lavugio_mobile.data.model.user.DriverUpdateRequestDiff;
import com.example.lavugio_mobile.ui.dialog.ConfirmDialogFragment;
import com.example.lavugio_mobile.ui.dialog.SuccessDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class DriverUpdateRequestsFragment extends Fragment {

    private TextView tvEmptyState;
    private LinearLayout llRequestsContainer;
    private List<DriverUpdateRequestDiff> requests;

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

        // Load hardcoded data
        loadHardcodedRequests();
        displayRequests();
    }

    private void loadHardcodedRequests() {
        requests = new ArrayList<>();

        // Request 1 - Multiple changes
        DriverUpdateRequest oldData1 = new DriverUpdateRequest(
                "Cara Dušana 15",
                "Toyota",
                "Camry",
                "BG-123-AB",
                4,
                true,
                false,
                "Black",
                "SEDAN"
        );

        DriverUpdateRequest newData1 = new DriverUpdateRequest(
                "Milana Rakića 67",
                "Toyota",
                "Camry",
                "BG-123-AB",
                4,
                false,
                false,
                "Black",
                "SEDAN"
        );

        requests.add(new DriverUpdateRequestDiff(1L, oldData1, newData1, "stefan.stefanovic@gmail.com"));

        // Request 2 - Vehicle changes
        DriverUpdateRequest oldData2 = new DriverUpdateRequest(
                "Bulevar kralja Aleksandra 73",
                "Honda",
                "Accord",
                "NS-456-CD",
                5,
                false,
                true,
                "White",
                "SEDAN"
        );

        DriverUpdateRequest newData2 = new DriverUpdateRequest(
                "Bulevar kralja Aleksandra 73",
                "BMW",
                "X5",
                "BG-789-EF",
                7,
                true,
                true,
                "Blue",
                "SUV"
        );

        requests.add(new DriverUpdateRequestDiff(2L, oldData2, newData2, "marko.markovic@gmail.com"));

        // Request 3 - Address only
        DriverUpdateRequest oldData3 = new DriverUpdateRequest(
                "Knez Mihailova 12",
                "Mercedes",
                "E-Class",
                "BG-111-GH",
                5,
                false,
                false,
                "Silver",
                "SEDAN"
        );

        DriverUpdateRequest newData3 = new DriverUpdateRequest(
                "Terazije 25",
                "Mercedes",
                "E-Class",
                "BG-111-GH",
                5,
                false,
                false,
                "Silver",
                "SEDAN"
        );

        requests.add(new DriverUpdateRequestDiff(3L, oldData3, newData3, "ana.petrovic@gmail.com"));
    }

    private void displayRequests() {
        if (requests == null || requests.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            llRequestsContainer.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            llRequestsContainer.setVisibility(View.VISIBLE);

            llRequestsContainer.removeAllViews();

            for (DriverUpdateRequestDiff request : requests) {
                View requestCard = createRequestCard(request);
                llRequestsContainer.addView(requestCard);
            }
        }
    }

    private View createRequestCard(DriverUpdateRequestDiff request) {
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

        // Expand/Collapse logic
        final boolean[] isExpanded = {false};
        llCollapsed.setOnClickListener(v -> {
            if (isExpanded[0]) {
                // Collapse
                llExpanded.setVisibility(View.GONE);
                rotateIcon(ivExpandIcon, 180, 0);
            } else {
                // Expand
                llExpanded.setVisibility(View.VISIBLE);
                rotateIcon(ivExpandIcon, 0, 180);
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

    private void populateChanges(LinearLayout container, DriverUpdateRequestDiff request) {
        DriverUpdateRequest oldData = request.getOldData();
        DriverUpdateRequest newData = request.getNewData();

        // Check each field for changes
        if (!oldData.getAddress().equals(newData.getAddress())) {
            addChangedField(container, "Address:", oldData.getAddress(), newData.getAddress());
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

        if (!oldData.getVehicleColor().equals(newData.getVehicleColor())) {
            addChangedField(container, "Color:", oldData.getVehicleColor(), newData.getVehicleColor());
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

    private void handleReject(DriverUpdateRequestDiff request) {
        // TODO: Call backend API to reject
        requests.remove(request);
        displayRequests();

        SuccessDialogFragment.newInstance("Request Rejected",
                        "Update request has been rejected successfully")
                .show(getParentFragmentManager(), "success_dialog");
    }

    private void handleApprove(DriverUpdateRequestDiff request) {
        // TODO: Call backend API to approve
        requests.remove(request);
        displayRequests();

        SuccessDialogFragment.newInstance("Request Approved",
                        "Update request has been approved successfully")
                .show(getParentFragmentManager(), "success_dialog");
    }
}