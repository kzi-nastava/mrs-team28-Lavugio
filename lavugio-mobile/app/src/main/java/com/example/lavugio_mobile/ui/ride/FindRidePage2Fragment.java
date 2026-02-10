package com.example.lavugio_mobile.ui.ride;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class FindRidePage2Fragment extends Fragment {
    private static final String TAG = "FindRidePage2";
    private Button btnPrevious, btnNext;

    private View viewPetCheckbox, viewBabyCheckbox;
    private LinearLayout llPetFriendly, llBabyFriendly;
    private boolean isPetFriendly = false;
    private boolean isBabyFriendly = false;

    private EditText etPassengerEmail;
    private ImageButton btnAddPassenger;
    private ScrollView svPassengersList;
    private LinearLayout llPassengersList;
    private TextView tvNoPassengers;

    private List<String> passengerEmails = new ArrayList<>();

    private Spinner spinnerVehicleType;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_ride_page_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etPassengerEmail = view.findViewById(R.id.etPassengerEmail);
        btnAddPassenger = view.findViewById(R.id.btnAddPassenger);
        svPassengersList = view.findViewById(R.id.svPassengersList);
        llPassengersList = view.findViewById(R.id.llPassengersList);
        tvNoPassengers = view.findViewById(R.id.tvNoPassengers);

        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);


        viewPetCheckbox = view.findViewById(R.id.viewPetCheckbox);
        viewBabyCheckbox = view.findViewById(R.id.viewBabyCheckbox);
        spinnerVehicleType = view.findViewById(R.id.spinnerVehicleType);
        llPetFriendly = view.findViewById(R.id.llPetFriendly);
        llBabyFriendly = view.findViewById(R.id.llBabyFriendly);

        setupButtons();
        setupPassengerInput();
        setupVehicleSpinner();
    }

    private void setupButtons() {
        btnPrevious.setOnClickListener(v -> {
            ((FindRideFragment) getParentFragment()).previousPage();
        });

        btnNext.setOnClickListener(v -> {
            ((FindRideFragment) getParentFragment()).nextPage();
        });

        llPetFriendly.setOnClickListener(v -> {
            isPetFriendly = !isPetFriendly;
            viewPetCheckbox.setBackgroundResource(isPetFriendly ?
                    R.drawable.checkbox_checked : R.drawable.checkbox_unchecked);
        });

        llBabyFriendly.setOnClickListener(v -> {
            isBabyFriendly = !isBabyFriendly;
            viewBabyCheckbox.setBackgroundResource(isBabyFriendly ?
                    R.drawable.checkbox_checked : R.drawable.checkbox_unchecked);
        });
    }

    private void setupPassengerInput() {
        btnAddPassenger.setOnClickListener(v -> {
            String email = etPassengerEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                //Toast.makeText(getContext(), "Please enter an email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                //Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (passengerEmails.contains(email)) {
                //Toast.makeText(getContext(), "Email already added", Toast.LENGTH_SHORT).show();
                return;
            }

            addPassenger(email);
            etPassengerEmail.setText("");
        });
    }

    private void setupVehicleSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.vehicle_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicleType.setAdapter(adapter);
    }

    private void addPassenger(String email) {
        passengerEmails.add(email);
        updatePassengersDisplay();

        Toast.makeText(getContext(), "Passenger added", Toast.LENGTH_SHORT).show();
    }

    private void removePassenger(int position) {
        if (position >= 0 && position < passengerEmails.size()) {
            String removed = passengerEmails.remove(position);
            updatePassengersDisplay();

            Toast.makeText(getContext(), "Removed: " + removed, Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePassengersDisplay() {
        // Clear all passenger views
        llPassengersList.removeAllViews();

        if (passengerEmails.isEmpty()) {
            // Show empty state
            tvNoPassengers.setVisibility(View.VISIBLE);
            llPassengersList.setVisibility(View.GONE);
        } else {
            // Hide empty state
            tvNoPassengers.setVisibility(View.GONE);
            llPassengersList.setVisibility(View.VISIBLE);

            // Add each passenger as a separate item
            for (int i = 0; i < passengerEmails.size(); i++) {
                View passengerItem = createPassengerItem(i, passengerEmails.get(i));
                llPassengersList.addView(passengerItem);

                // Add spacing between items
                if (i < passengerEmails.size() - 1) {
                    View spacer = new View(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            8
                    );
                    spacer.setLayoutParams(params);
                    llPassengersList.addView(spacer);
                }
            }
        }
    }

    private View createPassengerItem(int position, String email) {
        View itemView = LayoutInflater.from(getContext()).inflate(
                R.layout.scrollable_list_item,
                llPassengersList,
                false
        );

        TextView tvNumber = itemView.findViewById(R.id.tvItemNumber);
        TextView tvEmail = itemView.findViewById(R.id.tvItemName);
        ImageView ivRemove = itemView.findViewById(R.id.ivRemoveItem);

        tvNumber.setText(String.valueOf(position + 1));
        tvEmail.setText(email);

        // Setup marquee on click
        setupMarquee(tvEmail);

        // Remove passenger on click
        ivRemove.setOnClickListener(v -> removePassenger(position));

        return itemView;
    }

    private void setupMarquee(TextView textView) {
        // Initially not selected
        textView.setSelected(false);

        // Toggle marquee on click
        textView.setOnClickListener(v -> {
            textView.setSelected(!textView.isSelected());
        });
    }

    public List<String> getPassengerEmails() {
        return new ArrayList<>(passengerEmails);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
