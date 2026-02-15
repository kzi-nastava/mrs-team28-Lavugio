package com.example.lavugio_mobile.ui.driver;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.RideHistoryDriverModel;
import com.example.lavugio_mobile.models.RideHistoryDriverPagingModel;
import com.example.lavugio_mobile.services.DriverService;
import com.example.lavugio_mobile.services.LocationService;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RideHistoryFragment extends Fragment {

    private EditText startDateEditText;
    private EditText endDateEditText;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private boolean isSelectingStartDate = true;

    private RecyclerView ridesRecyclerView;
    private RideHistoryAdapter adapter;
    private LinearLayoutManager layoutManager;

    private MaterialButton sortOrderButton;
    private MaterialButton applyButton;
    private Spinner sortBySpinner;

    // Overlay loading indicators (from XML, NOT adapter items)
    private ProgressBar topLoadingIndicator;
    private ProgressBar bottomLoadingIndicator;

    private DriverService driverService;

    // Pagination state
    private int oldestLoadedPage = 0;
    private int newestLoadedPage = 0;
    private boolean hasMoreNewer = false;
    private boolean hasMoreOlder = true;
    private boolean isLoading = false;

    private final int pageSize = 10;
    private final int maxLoadedPages = 3;

    // Sorting state
    private String sorting = "DESC";
    private String sortBy = "START";

    // Date filters
    private String startDate = "01/01/2000";
    private String endDate = "31/12/2100";

    // List of rides
    private List<RideHistoryDriverModel> rides = new ArrayList<>();

    // For scroll position preservation
    private int savedScrollPosition = 0;

    public RideHistoryFragment() {}

    public static RideHistoryFragment newInstance() {
        return new RideHistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_trip_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        driverService = LavugioApp.getDriverService();

        initViews(view);
        initRecyclerView();
        initDatePicker(view);
        initSortBySpinner();
        initSortOrderButton();
        initApplyButton();

        loadInitialRides();
    }

    private void initViews(View view) {
        ridesRecyclerView = view.findViewById(R.id.ridesRecyclerView);
        sortOrderButton = view.findViewById(R.id.sortOrderButton);
        applyButton = view.findViewById(R.id.applyButton);
        sortBySpinner = view.findViewById(R.id.sortBySpinner);
        startDateEditText = view.findViewById(R.id.startDateInputField);
        endDateEditText = view.findViewById(R.id.endDateInputField);
        topLoadingIndicator = view.findViewById(R.id.topLoadingIndicator);
        bottomLoadingIndicator = view.findViewById(R.id.bottomLoadingIndicator);
    }

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(requireContext());
        ridesRecyclerView.setLayoutManager(layoutManager);

        adapter = new RideHistoryAdapter(rides, this::openRideDetails);
        ridesRecyclerView.setAdapter(adapter);

        ridesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isLoading) return;

                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();

                if (firstVisibleItem <= 3 && hasMoreNewer) {
                    loadNewer();
                }

                if (lastVisibleItem >= totalItemCount - 3 && hasMoreOlder) {
                    loadOlder();
                }
            }
        });
    }

    private void initSortBySpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.trip_history_sort_options,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(spinnerAdapter);

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: sortBy = "START"; break;
                    case 1: sortBy = "DEPARTURE"; break;
                    case 2: sortBy = "DESTINATION"; break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initSortOrderButton() {
        updateSortOrderIcon();
        sortOrderButton.setOnClickListener(v -> {
            sorting = sorting.equals("ASC") ? "DESC" : "ASC";
            updateSortOrderIcon();
        });
    }

    private void updateSortOrderIcon() {
        if (sorting.equals("ASC")) {
            sortOrderButton.setIconResource(R.drawable.ic_arrow_long_upward);
        } else {
            sortOrderButton.setIconResource(R.drawable.ic_arrow_long_downward);
        }
    }

    private void initApplyButton() {
        applyButton.setOnClickListener(v -> scrollToTop());
    }

    private void scrollToTop() {
        ridesRecyclerView.scrollToPosition(0);
        ridesRecyclerView.post(this::loadInitialRides);
    }

    // ── Loading indicator helpers ────────────────────────

    private void showTopLoading() {
        topLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideTopLoading() {
        topLoadingIndicator.setVisibility(View.GONE);
    }

    private void showBottomLoading() {
        bottomLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideBottomLoading() {
        bottomLoadingIndicator.setVisibility(View.GONE);
    }

    /**
     * Sync all state to the adapter and notify once.
     */
    private void commitAdapterState() {
        adapter.setHasMoreNewer(hasMoreNewer);
        adapter.setHasMoreOlder(hasMoreOlder);
        adapter.notifyDataSetChanged();
    }

    // ── Data loading ─────────────────────────────────────

    private void loadInitialRides() {
        isLoading = true;
        hideTopLoading();
        hideBottomLoading();

        driverService.getDriverRideHistory(
                0, pageSize, sorting, sortBy, startDate, endDate,
                new DriverService.Callback<RideHistoryDriverPagingModel>() {
                    @Override
                    public void onSuccess(RideHistoryDriverPagingModel response) {
                        rides.clear();
                        if (response.getDriverHistory() != null) {
                            rides.addAll(response.getDriverHistory());
                        }

                        oldestLoadedPage = 0;
                        newestLoadedPage = 0;
                        hasMoreOlder = !response.isReachedEnd();
                        hasMoreNewer = false;
                        isLoading = false;

                        commitAdapterState();
                    }

                    @Override
                    public void onError(int code, String message) {
                        isLoading = false;
                        hideTopLoading();
                        hideBottomLoading();
                        Toast.makeText(requireContext(),
                                "Error loading rides: " + message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void loadOlder() {
        if (isLoading || !hasMoreOlder) return;

        int nextPage = newestLoadedPage + 1;
        isLoading = true;
        showBottomLoading();

        driverService.getDriverRideHistory(
                nextPage, pageSize, sorting, sortBy, startDate, endDate,
                new DriverService.Callback<RideHistoryDriverPagingModel>() {
                    @Override
                    public void onSuccess(RideHistoryDriverPagingModel response) {
                        if (response.getDriverHistory() != null) {
                            rides.addAll(response.getDriverHistory());
                        }

                        newestLoadedPage = nextPage;
                        hasMoreOlder = !response.isReachedEnd();

                        trimOldPages();

                        isLoading = false;
                        hideBottomLoading();

                        commitAdapterState();
                    }

                    @Override
                    public void onError(int code, String message) {
                        isLoading = false;
                        hideBottomLoading();
                        Toast.makeText(requireContext(),
                                "Error loading older rides: " + message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void loadNewer() {
        if (isLoading || !hasMoreNewer) return;

        int nextPage = oldestLoadedPage - 1;
        if (nextPage < 0) return;

        isLoading = true;
        showTopLoading();

        // Save scroll position before prepending items
        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleView = layoutManager.findViewByPosition(firstVisible);
        if (firstVisibleView != null) {
            savedScrollPosition = firstVisibleView.getTop();
        }

        driverService.getDriverRideHistory(
                nextPage, pageSize, sorting, sortBy, startDate, endDate,
                new DriverService.Callback<RideHistoryDriverPagingModel>() {
                    @Override
                    public void onSuccess(RideHistoryDriverPagingModel response) {
                        List<RideHistoryDriverModel> newRides =
                                response.getDriverHistory() != null
                                        ? response.getDriverHistory()
                                        : new ArrayList<>();

                        rides.addAll(0, newRides);

                        oldestLoadedPage = nextPage;
                        hasMoreNewer = nextPage > 0;

                        trimNewPages();

                        isLoading = false;
                        hideTopLoading();

                        commitAdapterState();

                        // Restore scroll position offset by prepended items
                        layoutManager.scrollToPositionWithOffset(
                                newRides.size(), savedScrollPosition);
                    }

                    @Override
                    public void onError(int code, String message) {
                        isLoading = false;
                        hideTopLoading();
                        Toast.makeText(requireContext(),
                                "Error loading newer rides: " + message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // ── Trimming ─────────────────────────────────────────

    private void trimOldPages() {
        int totalLoadedPages = newestLoadedPage - oldestLoadedPage + 1;
        if (totalLoadedPages > maxLoadedPages) {
            int pagesToRemove = totalLoadedPages - maxLoadedPages;
            int itemsToRemove = Math.min(pagesToRemove * pageSize, rides.size());

            for (int i = 0; i < itemsToRemove; i++) {
                rides.remove(0);
            }

            oldestLoadedPage += pagesToRemove;
            hasMoreNewer = true;
        }
    }

    private void trimNewPages() {
        int totalLoadedPages = newestLoadedPage - oldestLoadedPage + 1;
        if (totalLoadedPages > maxLoadedPages) {
            int pagesToRemove = totalLoadedPages - maxLoadedPages;
            int itemsToRemove = Math.min(pagesToRemove * pageSize, rides.size());

            for (int i = 0; i < itemsToRemove; i++) {
                rides.remove(rides.size() - 1);
            }

            newestLoadedPage -= pagesToRemove;
            hasMoreOlder = true;
        }
    }

    // ── Navigation ───────────────────────────────────────

    private void openRideDetails(RideHistoryDriverModel ride) {
        // Backend already sends pre-formatted strings — just split them
        RideDetailsFragment fragment = RideDetailsFragment.newInstance(
                String.valueOf(ride.getRideId()),
                ride.getStartDateOnly(),   // "12.02.2026"
                ride.getStartTime(),       // "22:15"
                ride.getEndDateOnly(),     // "12.02.2026"
                ride.getEndTime(),         // "22:15"
                ride.getStartAddress(),
                ride.getEndAddress()
        );

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return String.format(Locale.getDefault(), "%02d:%02d",
                dateTime.getHour(), dateTime.getMinute());
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return String.format(Locale.getDefault(), "%02d.%02d.%04d.",
                dateTime.getDayOfMonth(), dateTime.getMonthValue(), dateTime.getYear());
    }

    // ── Date picker ──────────────────────────────────────

    private void initDatePicker(View view) {
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
        Calendar currentCalendar = isSelectingStartDate ? startCalendar : endCalendar;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    if (isSelectingStartDate) {
                        startCalendar.set(year, month, dayOfMonth);
                        if (startCalendar.after(endCalendar)) {
                            endCalendar.set(year, month, dayOfMonth);
                            updateDateField(endDateEditText, endCalendar);
                        }
                        updateDateField(startDateEditText, startCalendar);
                        startDate = formatDateForApi(startCalendar);
                    } else {
                        endCalendar.set(year, month, dayOfMonth);
                        if (endCalendar.before(startCalendar)) {
                            startCalendar.set(year, month, dayOfMonth);
                            updateDateField(startDateEditText, startCalendar);
                        }
                        updateDateField(endDateEditText, endCalendar);
                        endDate = formatDateForApi(endCalendar);
                    }
                },
                currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH),
                currentCalendar.get(Calendar.DAY_OF_MONTH)
        );

        Calendar minDate = Calendar.getInstance();
        minDate.set(2020, 0, 1);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.setTitle(isSelectingStartDate ? "Select start date" : "Select end date");
        datePickerDialog.show();
    }

    private void updateDateField(EditText editText, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        editText.setText(sdf.format(calendar.getTime()));
    }

    private String formatDateForApi(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}