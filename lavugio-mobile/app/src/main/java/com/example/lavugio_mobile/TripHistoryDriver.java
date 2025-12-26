package com.example.lavugio_mobile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TripHistoryDriver extends AppCompatActivity {

    private EditText startDateEditText;
    private EditText endDateEditText;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private boolean isSelectingStartDate = true;

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

        // Inicijalizacija date pickera
        initDatePicker();
    }

    private void initDatePicker() {
        // Pronađi EditText polja
        startDateEditText = findViewById(R.id.startDateInputField);
        endDateEditText = findViewById(R.id.endDateInputField);

        // Inicijalizuj kalendare
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        // NE postavljaj današnji datum na početku
        // Ostavi hint "Start date" i "End date" vidljivim
        // Tek kad korisnik izabere datum, onda postavi tekst

        // Klik na start datum
        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectingStartDate = true;
                showDatePickerDialog();
            }
        });

        // Klik na end datum
        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectingStartDate = false;
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar currentCalendar = Calendar.getInstance();

        // Ako već postoji izabrani datum, koristi ga, inače koristi današnji
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
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if (isSelectingStartDate) {
                            startCalendar.set(year, month, dayOfMonth);
                            // Proveri da li je start datum posle end datuma
                            if (startCalendar.after(endCalendar)) {
                                endCalendar.set(year, month, dayOfMonth);
                                updateDateField(endDateEditText, endCalendar);
                            }
                            updateDateField(startDateEditText, startCalendar);
                        } else {
                            endCalendar.set(year, month, dayOfMonth);
                            // Proveri da li je end datum pre start datuma
                            if (endCalendar.before(startCalendar)) {
                                startCalendar.set(year, month, dayOfMonth);
                                updateDateField(startDateEditText, startCalendar);
                            }
                            updateDateField(endDateEditText, endCalendar);
                        }
                    }
                },
                year, month, day
        );

        // Postavi minimalni i maksimalni datum
        Calendar minDate = Calendar.getInstance();
        minDate.set(2020, 0, 1);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Postavi naslov
        String title = isSelectingStartDate ? "Izaberite početni datum" : "Izaberite krajnji datum";
        datePickerDialog.setTitle(title);

        // Prikaži dijalog
        datePickerDialog.show();
    }

    private void updateDateField(EditText editText, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateText = sdf.format(calendar.getTime());
        editText.setText(dateText);
    }

    // Metode za dobijanje datuma
    public String getStartDate() {
        if (startDateEditText != null && !startDateEditText.getText().toString().isEmpty()) {
            return startDateEditText.getText().toString();
        }
        return null; // ili prazan string ""
    }

    public String getEndDate() {
        if (endDateEditText != null && !endDateEditText.getText().toString().isEmpty()) {
            return endDateEditText.getText().toString();
        }
        return null; // ili prazan string ""
    }

    public String getStartDateFormatted(String pattern) {
        if (startCalendar != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
                return sdf.format(startCalendar.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getEndDateFormatted(String pattern) {
        if (endCalendar != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
                return sdf.format(endCalendar.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    // Metode za postavljanje datuma
    public void setStartDate(int year, int month, int day) {
        if (startCalendar != null) {
            startCalendar.set(year, month - 1, day);
            updateDateField(startDateEditText, startCalendar);
        }
    }

    public void setEndDate(int year, int month, int day) {
        if (endCalendar != null) {
            endCalendar.set(year, month - 1, day);
            updateDateField(endDateEditText, endCalendar);
        }
    }
}