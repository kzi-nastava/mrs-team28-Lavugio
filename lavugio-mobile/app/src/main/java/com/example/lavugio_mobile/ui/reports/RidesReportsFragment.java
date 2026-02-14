package com.example.lavugio_mobile.ui.reports;

import android.app.DatePickerDialog;
import android.graphics.Color;
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
import com.example.lavugio_mobile.data.model.reports.ChartData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        List<ChartData> chartsData = getHardcodedChartData();

        // Setup Chart 1
        setupChart(
                view.findViewById(R.id.chart1),
                chartsData.get(0)
        );

        // Setup Chart 2
        setupChart(
                view.findViewById(R.id.chart2),
                chartsData.get(1)
        );

        // Setup Chart 3
        setupChart(
                view.findViewById(R.id.chart3),
                chartsData.get(2)
        );
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

    private void setupChart(View chartContainer, ChartData chartData) {
        // Find views
        TextView tvTitle = chartContainer.findViewById(R.id.tvChartTitle);
        LineChart lineChart = chartContainer.findViewById(R.id.lineChart);
        TextView tvSum = chartContainer.findViewById(R.id.tvSum);
        TextView tvAverage = chartContainer.findViewById(R.id.tvAverage);

        // Set title
        tvTitle.setText(chartData.getTitle());

        // Set sum and average
        tvSum.setText(String.format("Sum: %.2f", chartData.getSum()));
        tvAverage.setText(String.format("Average: %.2f", chartData.getAverage()));

        // Create chart entries
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < chartData.getData().size(); i++) {
            entries.add(new Entry(i, chartData.getData().get(i)));
        }

        // Create dataset
        LineDataSet dataSet = new LineDataSet(entries, chartData.getYAxisLabel());
        dataSet.setColor(Color.parseColor("#B87333"));
        dataSet.setCircleColor(Color.parseColor("#B87333"));
        dataSet.setCircleRadius(5f);
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);

        // Create line data
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Customize X-Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(chartData.getLabels()));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);  // DODAJ
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);  // Promeni u BLACK
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.LTGRAY);
        xAxis.setLabelCount(chartData.getLabels().size(), false);  // PROMENI - dodaj false
        xAxis.setDrawLabels(true);
        xAxis.setAvoidFirstLastClipping(true);  // DODAJ
        xAxis.setYOffset(5f);  // DODAJ - space between axis and labels

        // Customize Y-Axis (Left)
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextSize(10f);
        leftAxis.setTextColor(Color.BLACK);  // Promeni u BLACK
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setDrawLabels(true);
        leftAxis.setXOffset(5f);  // DODAJ - space between axis and labels
        leftAxis.setGranularityEnabled(true);  // DODAJ
        leftAxis.setGranularity(1f);  // DODAJ

        // Disable right Y-Axis
        lineChart.getAxisRight().setEnabled(false);

        // Chart settings
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawBorders(false);
        lineChart.setExtraOffsets(15f, 15f, 15f, 25f);

        // Refresh chart
        lineChart.invalidate();
    }

    private List<ChartData> getHardcodedChartData() {
        List<ChartData> charts = new ArrayList<>();

        // Chart 1: Rides Per Day
        charts.add(new ChartData(
                "Rides Per Day",
                "Date",
                "Rides",
                Arrays.asList("07/10", "08/10", "09/10", "10/10", "11/10", "12/10"),
                Arrays.asList(2.5f, 6f, 14f, 19f, 5.5f, 0.5f),
                47.5f,
                7.92f
        ));

        // Chart 2: Mileage Covered Per Day
        charts.add(new ChartData(
                "Total Mileage",
                "Date",
                "Mileage (km)",
                Arrays.asList("07/10", "08/10", "09/10", "10/10", "11/10", "12/10"),
                Arrays.asList(2.5f, 6f, 14f, 19f, 5.5f, 0.5f),
                47.31f,
                7.88f
        ));

        // Chart 3: Daily Financial Report
        charts.add(new ChartData(
                "Daily Revenue",
                "Date",
                "Revenue (RSD)",
                Arrays.asList("07/10", "08/10", "09/10", "10/10", "11/10", "12/10"),
                Arrays.asList(2500f, 3600f, 7400f, 9500f, 5500f, 1200f),
                29700f,
                4950f
        ));

        return charts;
    }
}