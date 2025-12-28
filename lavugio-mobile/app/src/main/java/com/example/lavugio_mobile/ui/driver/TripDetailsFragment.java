package com.example.lavugio_mobile.ui.driver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class TripDetailsFragment extends Fragment {

    private TextView tripBeginText;
    private TextView tripEndText;
    private TextView tripDepartureText;
    private TextView tripDestinationText;
    private TextView tripPriceText;
    private ImageView cancelledIcon;
    private ImageView panicIcon;
    private ImageButton backButton;
    private LinearLayout passengersContainer;

    private List<String> passengersList;

    // Factory metoda za kreiranje fragmenta sa argumentima
    public static TripDetailsFragment newInstance(String tripId, String startDate, String startTime,
                                                  String endDate, String endTime,
                                                  String departure, String destination) {
        TripDetailsFragment fragment = new TripDetailsFragment();
        Bundle args = new Bundle();
        args.putString("tripId", tripId);
        args.putString("startDate", startDate);
        args.putString("startTime", startTime);
        args.putString("endDate", endDate);
        args.putString("endTime", endTime);
        args.putString("departure", departure);
        args.putString("destination", destination);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout za fragment
        return inflater.inflate(R.layout.fragment_driver_trip_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            initViews(view);
            loadTripData();
            loadPassengers();

            if (backButton != null) {
                backButton.setOnClickListener(v -> {
                    // Vrati se nazad
                    requireActivity().onBackPressed();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            requireActivity().onBackPressed();
        }
    }

    private void initViews(View view) {
        tripBeginText = view.findViewById(R.id.tripBeginText);
        tripEndText = view.findViewById(R.id.tripEndText);
        tripDepartureText = view.findViewById(R.id.tripDepartureText);
        tripDestinationText = view.findViewById(R.id.tripDestinationText);
        tripPriceText = view.findViewById(R.id.tripPriceText);
        cancelledIcon = view.findViewById(R.id.cancelledIcon);
        panicIcon = view.findViewById(R.id.panicIcon);
        passengersContainer = view.findViewById(R.id.passengersContainer);
    }

    private void loadTripData() {
        try {
            // Uzmi argumente umesto getIntent()
            Bundle args = getArguments();
            if (args == null) return;

            String startDate = args.getString("startDate");
            String startTime = args.getString("startTime");
            String endDate = args.getString("endDate");
            String endTime = args.getString("endTime");
            String departure = args.getString("departure");
            String destination = args.getString("destination");

            String begin = (startDate != null ? startDate : "N/A") + " " + (startTime != null ? startTime : "");
            String end = (endDate != null ? endDate : "N/A") + " " + (endTime != null ? endTime : "");

            if (tripBeginText != null) tripBeginText.setText(begin.trim());
            if (tripEndText != null) tripEndText.setText(end.trim());
            if (tripDepartureText != null) tripDepartureText.setText(departure != null ? departure : "N/A");
            if (tripDestinationText != null) tripDestinationText.setText(destination != null ? destination : "N/A");
            if (tripPriceText != null) tripPriceText.setText("850.00 RSD");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPassengers() {
        passengersList = new ArrayList<>();
        passengersList.add("Pera Zdera");
        passengersList.add("Mika Mikić");
        passengersList.add("Jovan Jovanović");
        passengersList.add("Ana Anić");
        passengersList.add("Marko Marković");
        passengersList.add("Jovana Jović");
        passengersList.add("Nikola Nikolić");
        passengersList.add("Milica Milić");

        displayPassengers();
    }

    private void displayPassengers() {
        if (passengersContainer == null) return;

        passengersContainer.removeAllViews();

        for (String passengerName : passengersList) {
            View passengerView = createPassengerView(passengerName);
            passengersContainer.addView(passengerView);

            // Dodaj separator
            View separator = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
            );
            separator.setLayoutParams(params);
            separator.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
            passengersContainer.addView(separator);
        }
    }

    private View createPassengerView(String passengerName) {
        View view = getLayoutInflater().inflate(R.layout.passenger_item, passengersContainer, false);

        TextView nameText = view.findViewById(R.id.passengerName);
        if (nameText != null) {
            nameText.setText(passengerName);
        }

        return view;
    }
}