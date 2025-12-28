package com.example.lavugio_mobile.ui.driver;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lavugio_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class TripDetailsActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_trip_details);

        try {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            initViews();
            loadTripData();
            loadPassengers();

            if (backButton != null) {
                backButton.setOnClickListener(v -> finish());
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void initViews() {
        tripBeginText = findViewById(R.id.tripBeginText);
        tripEndText = findViewById(R.id.tripEndText);
        tripDepartureText = findViewById(R.id.tripDepartureText);
        tripDestinationText = findViewById(R.id.tripDestinationText);
        tripPriceText = findViewById(R.id.tripPriceText);
        cancelledIcon = findViewById(R.id.cancelledIcon);
        panicIcon = findViewById(R.id.panicIcon);
        backButton = findViewById(R.id.backButton);
        passengersContainer = findViewById(R.id.passengersContainer);
    }

    private void loadTripData() {
        try {
            String startDate = getIntent().getStringExtra("startDate");
            String startTime = getIntent().getStringExtra("startTime");
            String endDate = getIntent().getStringExtra("endDate");
            String endTime = getIntent().getStringExtra("endTime");
            String departure = getIntent().getStringExtra("departure");
            String destination = getIntent().getStringExtra("destination");

            String begin = (startDate != null ? startDate : "N/A") + " " + (startTime != null ? startTime : "");
            String end = (endDate != null ? endDate : "N/A") + " " + (endTime != null ? endTime : "");

            if (tripBeginText != null) tripBeginText.setText(begin.trim());
            if (tripEndText != null) tripEndText.setText(end.trim());
            if (tripDepartureText != null) tripDepartureText.setText(departure != null ? departure : "N/A");
            if (tripDestinationText != null) tripDestinationText.setText(destination != null ? destination : "N/A");
            if (tripPriceText != null) tripPriceText.setText("850.00 RSD"); // Hardcoded za sada

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

            // Dodaj separator (border-bottom)
            View separator = new View(this);
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