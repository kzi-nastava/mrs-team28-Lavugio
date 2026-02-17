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
import androidx.lifecycle.ViewModelProvider;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.data.model.reports.ChartData;
import com.example.lavugio_mobile.viewmodel.reports.RidesReportsViewModel;
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

    private RidesReportsViewModel viewModel;

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

        viewModel = new ViewModelProvider(this).get(RidesReportsViewModel.class);
        observeViewModel();

        // Hide admin-only filters for non-admin users
        setupFiltersVisibility(view);
    }

    private void observeViewModel() {
        viewModel.getReportData().observe(getViewLifecycleOwner(), report -> {
            if (report == null) {
                Toast.makeText(requireContext(),
                        "Failed to load report",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            setupChart(getView().findViewById(R.id.chart1), report.getCharts().get(0));
            setupChart(getView().findViewById(R.id.chart2), report.getCharts().get(1));
            setupChart(getView().findViewById(R.id.chart3), report.getCharts().get(2));
        });
    }

    private void setupFiltersVisibility(View view) {
        String userType = viewModel.getLoggedInUserType();
        boolean isAdmin = "ADMIN".equals(userType);

        // Hide email section for non-admins
        View emailTitleView = view.findViewById(R.id.tvEmailTitle);
        View emailInputContainer = view.findViewById(R.id.emailInputContainer);
        View filterByTitle = view.findViewById(R.id.tvFilterByTitle);

        if (!isAdmin) {
            if (emailTitleView != null) {
                emailTitleView.setVisibility(View.GONE);
            }
            if (emailInputContainer != null) {
                emailInputContainer.setVisibility(View.GONE);
            }
            if (filterByTitle != null) {
                filterByTitle.setVisibility(View.GONE);
            }
            rgFilterType.setVisibility(View.GONE);
        }
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
            filterType = "allDrivers";
        } else if (selectedFilterId == R.id.rbAllRegularUsers) {
            filterType = "allRegularUsers";
        } else if (selectedFilterId == R.id.rbOneUserOnly) {
            filterType = "oneUser";
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

        performFiltering(startDateStr, endDateStr, filterType, userEmail);
    }

    private void performFiltering(String startDate, String endDate, String filterType, String userEmail) {
        String message = "Filtering reports:\n" +
                "Date: " + startDate + " - " + endDate + "\n" +
                "Filter: " + filterType;

        if (userEmail != null) {
            message += "\nEmail: " + userEmail;
        }

        viewModel.generateReport(startDate, endDate, filterType, userEmail);

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
            entries.add(new Entry(i, chartData.getData().get(i).floatValue()));
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
        xAxis.setGranularityEnabled(true);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.LTGRAY);
        xAxis.setLabelCount(chartData.getLabels().size(), false);
        xAxis.setDrawLabels(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setYOffset(5f);

        // Customize Y-Axis (Left)
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextSize(10f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setDrawLabels(true);
        leftAxis.setXOffset(5f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(1f);

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
}