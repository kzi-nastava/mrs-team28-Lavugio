package com.example.lavugio_mobile.ui.ride;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.data.model.ride.RidePreferences;
import com.example.lavugio_mobile.services.utils.GeocodingHelper;

import java.util.ArrayList;
import java.util.List;

public class FindRidePage3Fragment extends Fragment {
    private static final String TAG = "FindRidePage3";
    private static final int MAX_VISIBLE_ITEMS = 2;

    private Button btnPrevious, btnFinish;
    private View viewPetCheckbox, viewBabyCheckbox;

    private ScrollView svPage3, svDestinationsList, svPassengersList;
    private LinearLayout llDestinationsList, llPassengersList;
    private TextView tvNoDestinations, tvNoPassengers;
    private Spinner spinnerVehicleType;

    private List<GeocodingHelper.GeocodingResult> selectedDestinations;
    private RidePreferences ridePreferences;

    public static FindRidePage3Fragment newInstance(List<GeocodingHelper.GeocodingResult> selectedDestinations, RidePreferences ridePreferences) {
        FindRidePage3Fragment fragment = new FindRidePage3Fragment();
        fragment.setSelectedDestinations(selectedDestinations);
        fragment.setRidePreferences(ridePreferences);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_ride_page_3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnFinish = view.findViewById(R.id.btnFinish);
        viewPetCheckbox = view.findViewById(R.id.viewPetCheckbox);
        viewBabyCheckbox = view.findViewById(R.id.viewBabyCheckbox);

        svPage3 = view.findViewById(R.id.svPage3);
        svDestinationsList = view.findViewById(R.id.svDestinationsList);
        svPassengersList = view.findViewById(R.id.svPassengersList);
        llDestinationsList = view.findViewById(R.id.llDestinationsList);
        llPassengersList = view.findViewById(R.id.llPassengersList);
        tvNoDestinations = view.findViewById(R.id.tvNoDestinations);
        tvNoPassengers = view.findViewById(R.id.tvNoPassengers);
        spinnerVehicleType = view.findViewById(R.id.spinnerVehicleType);

        setupButtons();
        setupScrollInterception();
        loadData();
    }

    private void setupButtons() {
        btnPrevious.setOnClickListener(v -> {
            ((FindRideFragment) getParentFragment()).previousPage();
        });

        if (this.selectedDestinations == null || this.selectedDestinations.size() < 2) {
            btnFinish.setEnabled(false);
            btnFinish.setAlpha(0.5f);
        } else {
            btnFinish.setAlpha(1);
            btnFinish.setEnabled(true);
            btnFinish.setOnClickListener(v -> {
                openScheduleDialog();
            });
        }

    }

    private void setupScrollInterception() {
        // Main page scroll - walk up hierarchy to prevent bottom sheet from intercepting
        setupScrollViewInterception(svPage3);
        setupScrollViewInterception(svDestinationsList);
        setupScrollViewInterception(svPassengersList);
    }

    private void setupScrollViewInterception(ScrollView scrollView) {
        scrollView.setOnTouchListener((v, event) -> {
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

    private void loadData() {
        loadPreferences();
        loadDestinations();
        loadPassengers();
    }

    private void loadPreferences() {
        if (ridePreferences != null) {
            viewPetCheckbox.setBackgroundResource(ridePreferences.isPetFriendly() ?
                    R.drawable.checkbox_checked : R.drawable.checkbox_unchecked);
            viewBabyCheckbox.setBackgroundResource(ridePreferences.isBabyFriendly() ?
                    R.drawable.checkbox_checked : R.drawable.checkbox_unchecked);

            // Setup vehicle type spinner (read-only)
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    getContext(),
                    R.array.vehicle_types,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerVehicleType.setAdapter(adapter);

            if (ridePreferences.getVehicleType() != null) {
                setSpinnerSelection(spinnerVehicleType, ridePreferences.getVehicleType().toString());
            }

            spinnerVehicleType.setEnabled(false);
            spinnerVehicleType.setClickable(false);
        }
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

    private void loadDestinations() {
        llDestinationsList.removeAllViews();

        if (selectedDestinations == null || selectedDestinations.isEmpty()) {
            tvNoDestinations.setVisibility(View.VISIBLE);
            llDestinationsList.setVisibility(View.GONE);
        } else {
            tvNoDestinations.setVisibility(View.GONE);
            llDestinationsList.setVisibility(View.VISIBLE);

            for (int i = 0; i < selectedDestinations.size(); i++) {
                View item = createListItem(i, selectedDestinations.get(i).getDisplayName());
                llDestinationsList.addView(item);

                if (i < selectedDestinations.size() - 1) {
                    addSpacer(llDestinationsList);
                }
            }
        }

        constrainScrollViewHeight(svDestinationsList, llDestinationsList,
                selectedDestinations != null ? selectedDestinations.size() : 0);
    }

    private void loadPassengers() {
        llPassengersList.removeAllViews();

        List<String> emails = ridePreferences != null ? ridePreferences.getPassengerEmails() : null;

        if (emails == null || emails.isEmpty()) {
            tvNoPassengers.setVisibility(View.VISIBLE);
            llPassengersList.setVisibility(View.GONE);
        } else {
            tvNoPassengers.setVisibility(View.GONE);
            llPassengersList.setVisibility(View.VISIBLE);

            for (int i = 0; i < emails.size(); i++) {
                View item = createListItem(i, emails.get(i));
                llPassengersList.addView(item);

                if (i < emails.size() - 1) {
                    addSpacer(llPassengersList);
                }
            }
        }

        constrainScrollViewHeight(svPassengersList, llPassengersList,
                emails != null ? emails.size() : 0);
    }

    private View createListItem(int position, String text) {
        View itemView = LayoutInflater.from(getContext()).inflate(
                R.layout.scrollable_list_item, llDestinationsList, false);

        TextView tvNumber = itemView.findViewById(R.id.tvItemNumber);
        TextView tvName = itemView.findViewById(R.id.tvItemName);
        ImageView ivRemove = itemView.findViewById(R.id.ivRemoveItem);

        tvNumber.setText(String.valueOf(position + 1));
        tvName.setText(text);

        // Hide remove button — page 3 is read-only
        ivRemove.setVisibility(View.GONE);

        return itemView;
    }

    private void addSpacer(LinearLayout container) {
        View spacer = new View(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 8);
        spacer.setLayoutParams(params);
        container.addView(spacer);
    }

    private void constrainScrollViewHeight(ScrollView scrollView, LinearLayout list, int itemCount) {
        if (itemCount > MAX_VISIBLE_ITEMS) {
            scrollView.post(() -> {
                if (list.getChildCount() > 0) {
                    View firstItem = list.getChildAt(0);
                    firstItem.measure(
                            View.MeasureSpec.makeMeasureSpec(list.getWidth(), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    int itemHeight = firstItem.getMeasuredHeight();
                    int spacingPx = (int) (8 * getResources().getDisplayMetrics().density);
                    int paddingPx = (int) (24 * getResources().getDisplayMetrics().density);
                    int maxHeight = (itemHeight * MAX_VISIBLE_ITEMS)
                            + (spacingPx * (MAX_VISIBLE_ITEMS - 1)) + paddingPx;

                    ViewGroup.LayoutParams params = scrollView.getLayoutParams();
                    params.height = maxHeight;
                    scrollView.setLayoutParams(params);
                }
            });
        } else {
            ViewGroup.LayoutParams params = scrollView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            scrollView.setLayoutParams(params);
        }
    }

    private void openScheduleDialog() {
        FindRideScheduleFragment fragment = new FindRideScheduleFragment();

        fragment.setOnRideScheduledListener(new FindRideScheduleFragment.OnRideScheduledListener() {
            @Override
            public void onRideScheduled(String rideType, String selectedTime) {
                Log.d("SCHEDULE", "Ride type: " + rideType);
                Log.d("SCHEDULE", "Selected time: " + selectedTime);

                orderRide(rideType, selectedTime);
            }
        });

        fragment.show(getParentFragmentManager(), "ScheduleRideFragment");
    }

    private void orderRide(String rideType, String selectedTime) {
        Log.d("SCHEDULE", "Ordered Ride type: " + rideType);
        Log.d("SCHEDULE", "Ordered Selected time: " + selectedTime);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setupMarquee(TextView textView) {
        // Initially not selected
        textView.setSelected(false);

        // Toggle marquee on click
        textView.setOnClickListener(v -> {
            textView.setSelected(!textView.isSelected());
        });
    }

    private void setRidePreferences(RidePreferences ridePreferences) {
        this.ridePreferences = ridePreferences;
    }

    private void setSelectedDestinations(List<GeocodingHelper.GeocodingResult> selectedDestinations) {
        this.selectedDestinations = selectedDestinations;
    }
}
