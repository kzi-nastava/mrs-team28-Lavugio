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
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.data.model.ride.RidePreferences;
import com.example.lavugio_mobile.data.model.vehicle.VehicleType;

import java.util.ArrayList;
import java.util.List;

public class FindRidePage2Fragment extends Fragment {
    private static final String TAG = "FindRidePage2";
    private static final String ARG_RIDE_PREFERENCES = "ride_preferences";

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

    private static final int MAX_VISIBLE_PASSENGERS = 2;
    private List<String> passengerEmails = new ArrayList<>();

    private RidePreferences ridePreferences;
    private Spinner spinnerVehicleType;

    public static FindRidePage2Fragment newInstance(RidePreferences ridePreferences) {
        FindRidePage2Fragment fragment = new FindRidePage2Fragment();
        fragment.ridePreferences = ridePreferences;
        return fragment;
    }

    private void setRidePreferences(RidePreferences ridePreferences) {
        this.isPetFriendly = ridePreferences.isPetFriendly();
        this.isBabyFriendly = ridePreferences.isBabyFriendly();
        this.passengerEmails = ridePreferences.getPassengerEmails();
        setSpinnerSelection(this.spinnerVehicleType, ridePreferences.getVehicleType().toString());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_ride_page_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
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

        // Setup
        setupButtons();
        setupPassengerInput();
        setupPassengerScroll();
        setupVehicleSpinner();

        // Load preferences after all views are initialized
        if (ridePreferences != null) {
            loadPreferences();
        }
    }

    private void setupButtons() {
        btnPrevious.setOnClickListener(v -> {
            savePreferencesToParent();
            ((FindRideFragment) getParentFragment()).previousPage();
        });

        btnNext.setOnClickListener(v -> {
            savePreferencesToParent();
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

    private void setupPassengerScroll() {
        svPassengersList.setOnTouchListener((v, event) -> {
            if (v.canScrollVertically(1) || v.canScrollVertically(-1)) {
                ViewGroup parent = (ViewGroup) v.getParent();
                while (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                    if (parent.getParent() instanceof ViewGroup) {
                        parent = (ViewGroup) parent.getParent();
                    } else {
                        break;
                    }
                }
            }
            return false;
        });
    }

    private void constrainPassengersScrollViewHeight() {
        if (passengerEmails.size() > MAX_VISIBLE_PASSENGERS) {
            svPassengersList.post(() -> {
                if (llPassengersList.getChildCount() > 0) {
                    View firstItem = llPassengersList.getChildAt(0);
                    firstItem.measure(
                            View.MeasureSpec.makeMeasureSpec(llPassengersList.getWidth(), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    int itemHeight = firstItem.getMeasuredHeight();
                    int spacingPx = (int) (8 * getResources().getDisplayMetrics().density);
                    int paddingPx = (int) (24 * getResources().getDisplayMetrics().density);
                    int maxHeight = (itemHeight * MAX_VISIBLE_PASSENGERS) + (spacingPx * (MAX_VISIBLE_PASSENGERS - 1)) + paddingPx;

                    ViewGroup.LayoutParams params = svPassengersList.getLayoutParams();
                    params.height = maxHeight;
                    svPassengersList.setLayoutParams(params);
                }
            });
        } else {
            ViewGroup.LayoutParams params = svPassengersList.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            svPassengersList.setLayoutParams(params);
        }
    }

    private void setupPassengerInput() {
        btnAddPassenger.setOnClickListener(v -> {
            String email = etPassengerEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return;
            }

            if (passengerEmails.contains(email)) {
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

    /**
     * Load preferences and display them on the UI
     */
    private void loadPreferences() {
        isPetFriendly = ridePreferences.isPetFriendly();
        isBabyFriendly = ridePreferences.isBabyFriendly();
        
        // Create a copy of the list to avoid shared reference issues
        List<String> emails = ridePreferences.getPassengerEmails();
        passengerEmails = emails != null ? new ArrayList<>(emails) : new ArrayList<>();

        viewPetCheckbox.setBackgroundResource(isPetFriendly ?
                R.drawable.checkbox_checked : R.drawable.checkbox_unchecked);
        viewBabyCheckbox.setBackgroundResource(isBabyFriendly ?
                R.drawable.checkbox_checked : R.drawable.checkbox_unchecked);

        // Set passenger emails
        updatePassengersDisplay();

        // Set vehicle type spinner
        if (ridePreferences.getVehicleType() != null) {
            setSpinnerSelection(spinnerVehicleType, ridePreferences.getVehicleType().toString());
        }
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

        constrainPassengersScrollViewHeight();
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

    private void setSpinnerSelection(Spinner spinner, String value) {
        SpinnerAdapter adapter = spinner.getAdapter();
        if (adapter == null) return;

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    /**
     * Save current preferences to parent fragment
     */
    private void savePreferencesToParent() {
        FindRideFragment parent = (FindRideFragment) getParentFragment();
        if (parent != null) {
            parent.setSelectedPreferences(getCurrentPreferences());
        }
    }

    /**
     * Get current preferences from UI
     */
    public RidePreferences getCurrentPreferences() {
        RidePreferences prefs = new RidePreferences();
        prefs.setPetFriendly(isPetFriendly);
        prefs.setBabyFriendly(isBabyFriendly);
        prefs.setPassengerEmails(new ArrayList<>(passengerEmails));

        String selectedVehicle = spinnerVehicleType.getSelectedItem().toString().toUpperCase();
        prefs.setVehicleType(VehicleType.valueOf(selectedVehicle));

        return prefs;
    }

    public List<String> getPassengerEmails() {
        return new ArrayList<>(passengerEmails);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}