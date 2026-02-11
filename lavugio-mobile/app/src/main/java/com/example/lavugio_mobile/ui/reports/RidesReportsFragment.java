package com.example.lavugio_mobile.ui.reports;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RidesReportsFragment extends Fragment {

    // Views
    private TextView tvDateRange;
    private EditText etUserEmail;
    private RadioGroup rgFilterType;
    private RadioButton rbAllDrivers;
    private RadioButton rbAllRegularUsers;
    private RadioButton rbOneUserOnly;
    private Button btnFilterReports;

    // Date range variables
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private boolean isStartDateSet = false;
    private boolean isEndDateSet = false;

    // Date format
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rides_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners();
    }

    private void initViews(View view) {
        tvDateRange = view.findViewById(R.id.tvDateRange);
        etUserEmail = view.findViewById(R.id.etUserEmail);
        rgFilterType = view.findViewById(R.id.rgFilterType);
        rbAllDrivers = view.findViewById(R.id.rbAllDrivers);
        rbAllRegularUsers = view.findViewById(R.id.rbAllRegularUsers);
        rbOneUserOnly = view.findViewById(R.id.rbOneUserOnly);
        btnFilterReports = view.findViewById(R.id.btnFilterReports);
    }

    private void setupListeners() {
        tvDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateRangePicker();
            }
        });

        rgFilterType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbOneUserOnly) {
                    etUserEmail.setEnabled(true);
                    etUserEmail.setFocusable(true);
                    etUserEmail.setFocusableInTouchMode(true);
                    etUserEmail.setCursorVisible(true);
                    etUserEmail.setAlpha(1f);
                } else {
                    etUserEmail.setEnabled(false);
                    etUserEmail.setFocusable(false);
                    etUserEmail.setFocusableInTouchMode(false);
                    etUserEmail.setCursorVisible(false);
                    etUserEmail.setAlpha(0.5f);
                    etUserEmail.setText("");
                }
            }
        });

        btnFilterReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFilterReportsClick();
            }
        });
    }

    private void showDateRangePicker() {
        DatePickerDialog startDatePicker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    startDate.set(Calendar.YEAR, year);
                    startDate.set(Calendar.MONTH, month);
                    startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    isStartDateSet = true;

                    DatePickerDialog endDatePicker = new DatePickerDialog(
                            requireContext(),
                            (view2, year2, month2, dayOfMonth2) -> {
                                endDate.set(Calendar.YEAR, year2);
                                endDate.set(Calendar.MONTH, month2);
                                endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth2);
                                isEndDateSet = true;

                                updateDateRangeText();
                            },
                            startDate.get(Calendar.YEAR),
                            startDate.get(Calendar.MONTH),
                            startDate.get(Calendar.DAY_OF_MONTH)
                    );
                    endDatePicker.setTitle("Select End Date");
                    endDatePicker.show();
                },
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH)
        );
        startDatePicker.setTitle("Select Start Date");
        startDatePicker.show();
    }

    private void updateDateRangeText() {
        if (isStartDateSet && isEndDateSet) {
            String dateRange = dateFormat.format(startDate.getTime()) + " - " + dateFormat.format(endDate.getTime());
            tvDateRange.setText(dateRange);
            tvDateRange.setTextColor(getResources().getColor(R.color.dark_brown));
        }
    }

    private void onFilterReportsClick() {
        if (!isStartDateSet || !isEndDateSet) {
            Toast.makeText(requireContext(), "Please select date range", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedFilterId = rgFilterType.getCheckedRadioButtonId();
        String filterType = "";
        String userEmail = null;

        if (selectedFilterId == R.id.rbAllDrivers) {
            filterType = "all_drivers";
        } else if (selectedFilterId == R.id.rbAllRegularUsers) {
            filterType = "all_regular_users";
        } else if (selectedFilterId == R.id.rbOneUserOnly) {
            filterType = "one_user";
            userEmail = etUserEmail.getText().toString().trim();

            if (TextUtils.isEmpty(userEmail)) {
                Toast.makeText(requireContext(), "Please enter user email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(requireContext(), "Please enter valid email address", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String startDateStr = dateFormat.format(startDate.getTime());
        String endDateStr = dateFormat.format(endDate.getTime());

        // TODO: Implementiraj filtriranje izveštaja
        performFiltering(startDateStr, endDateStr, filterType, userEmail);
    }

    private void performFiltering(String startDate, String endDate, String filterType, String userEmail) {
        String message = "Filtering reports:\n" +
                "Date: " + startDate + " - " + endDate + "\n" +
                "Filter: " + filterType;

        if (userEmail != null) {
            message += "\nEmail: " + userEmail;
        }

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}