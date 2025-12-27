package com.example.lavugio_mobile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TripHistoryDriver extends AppCompatActivity {

    private EditText startDateEditText;
    private EditText endDateEditText;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private boolean isSelectingStartDate = true;
    private LinearLayout tripsContainer;

    private List<Trip> tripsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trip_history_driver);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Navbar navbar = new Navbar(this, findViewById(R.id.main));

        tripsContainer = findViewById(R.id.tripsContainer);
        initDatePicker();

        loadTestData();
        displayTrips();
    }

    private void loadTestData() {
        tripsList = new ArrayList<>();

        // Testni podaci
        for (int i = 0; i < 10; i++) {
            tripsList.add(new Trip(
                    String.valueOf(i + 1),
                    "15.02.2005.",
                    "16.02.2005.",
                    "23:40",
                    "00:05",
                    "Petra Drapsina 25, Novi Sad",
                    "Žarka Zrenjanina 4, Novi Sad"
            ));
        }
    }

    private void displayTrips() {
        tripsContainer.removeAllViews();

        if (tripsList.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("Nema vožnji za prikaz");
            emptyView.setTextSize(16);
            emptyView.setTextColor(getResources().getColor(android.R.color.darker_gray));
            emptyView.setPadding(0, 40, 0, 0);
            emptyView.setGravity(android.view.Gravity.CENTER);
            tripsContainer.addView(emptyView);
            return;
        }

        for (Trip trip : tripsList) {
            View tripRow = createTripRow(trip);
            tripsContainer.addView(tripRow);
        }
    }

    private View createTripRow(Trip trip) {
        View row = getLayoutInflater().inflate(R.layout.trip_row_item, tripsContainer, false);

        TextView startDateText = row.findViewById(R.id.tripStartDate);
        TextView startTimeText = row.findViewById(R.id.tripStartTime);
        TextView endDateText = row.findViewById(R.id.tripEndDate);
        TextView endTimeText = row.findViewById(R.id.tripEndTime);
        TextView departureText = row.findViewById(R.id.tripDeparture);
        TextView destinationText = row.findViewById(R.id.tripDestination);

        startDateText.setText(trip.startDate);
        startTimeText.setText(trip.startTime);
        endDateText.setText(trip.endDate);
        endTimeText.setText(trip.endTime);
        departureText.setText(trip.departure);
        destinationText.setText(trip.destination);

        row.setOnClickListener(v -> openTripDetails(trip));

        return row;
    }

    private void openTripDetails(Trip trip) {
        Intent intent = new Intent(this, TripDetailsActivity.class);
        intent.putExtra("tripId", trip.id);
        intent.putExtra("startDate", trip.startDate);
        intent.putExtra("startTime", trip.startTime);
        intent.putExtra("endDate", trip.endDate);
        intent.putExtra("endTime", trip.endTime);
        intent.putExtra("departure", trip.departure);
        intent.putExtra("destination", trip.destination);
        startActivity(intent);
    }

    private void initDatePicker() {
        startDateEditText = findViewById(R.id.startDateInputField);
        endDateEditText = findViewById(R.id.endDateInputField);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        startDateEditText.setOnClickListener(v -> {
            isSelectingStartDate = true;
            showDatePickerDialog();
        });

        endDateEditText.setOnClickListener(v -> {
            isSelectingStartDate = false;
            showDatePickerDialog();
        });
    }

    private void showDatePickerDialog() {
        Calendar currentCalendar = Calendar.getInstance();

        if (isSelectingStartDate && startCalendar != null) {
            currentCalendar = startCalendar;
        } else if (!isSelectingStartDate && endCalendar != null) {
            currentCalendar = endCalendar;
        }

        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        int day = currentCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    if (isSelectingStartDate) {
                        startCalendar.set(year1, month1, dayOfMonth);
                        if (startCalendar.after(endCalendar)) {
                            endCalendar.set(year1, month1, dayOfMonth);
                            updateDateField(endDateEditText, endCalendar);
                        }
                        updateDateField(startDateEditText, startCalendar);
                    } else {
                        endCalendar.set(year1, month1, dayOfMonth);
                        if (endCalendar.before(startCalendar)) {
                            startCalendar.set(year1, month1, dayOfMonth);
                            updateDateField(startDateEditText, startCalendar);
                        }
                        updateDateField(endDateEditText, endCalendar);
                    }
                    filterTripsByDate();
                },
                year, month, day
        );

        Calendar minDate = Calendar.getInstance();
        minDate.set(2020, 0, 1);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        String title = isSelectingStartDate ? "Select start date" : "Select end date";
        datePickerDialog.setTitle(title);

        datePickerDialog.show();
    }

    private void updateDateField(EditText editText, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateText = sdf.format(calendar.getTime());
        editText.setText(dateText);
    }

    private void filterTripsByDate() {
        displayTrips();
    }

    // Trip model
    private static class Trip {
        String id;
        String startDate;
        String endDate;
        String startTime;
        String endTime;
        String departure;
        String destination;

        Trip(String id, String startDate, String endDate, String startTime,
             String endTime, String departure, String destination) {
            this.id = id;
            this.startDate = startDate;
            this.endDate = endDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.departure = departure;
            this.destination = destination;
        }
    }

    public String getStartDate() {
        if (startDateEditText != null && !startDateEditText.getText().toString().isEmpty()) {
            return startDateEditText.getText().toString();
        }
        return null;
    }

    public String getEndDate() {
        if (endDateEditText != null && !endDateEditText.getText().toString().isEmpty()) {
            return endDateEditText.getText().toString();
        }
        return null;
    }
}