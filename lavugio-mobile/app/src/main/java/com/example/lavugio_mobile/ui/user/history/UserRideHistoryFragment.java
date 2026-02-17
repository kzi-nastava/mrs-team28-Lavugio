package com.example.lavugio_mobile.ui.user.history;

import android.app.DatePickerDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.example.lavugio_mobile.models.RideHistoryUserModel;
import com.example.lavugio_mobile.models.RideHistoryUserPagingModel;
import com.example.lavugio_mobile.services.UserService;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UserRideHistoryFragment extends Fragment implements SensorEventListener {

    private EditText startDateEditText;
    private EditText endDateEditText;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private boolean isSelectingStartDate = true;

    private RecyclerView ridesRecyclerView;
    private UserRideHistoryAdapter adapter;
    private LinearLayoutManager layoutManager;

    private MaterialButton sortOrderButton;
    private MaterialButton applyButton;
    private Spinner sortBySpinner;

    private ProgressBar topLoadingIndicator;
    private ProgressBar bottomLoadingIndicator;

    private UserService userService;

    // Shake detection
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float SHAKE_THRESHOLD = 15.0f;
    private static final int SHAKE_COOLDOWN_MS = 1000;
    private long lastShakeTime = 0;

    // Pagination state
    private int oldestLoadedPage = 0;
    private int newestLoadedPage = 0;
    private boolean hasMoreNewer = false;
    private boolean hasMoreOlder = true;
    private boolean isLoading = false;
    private boolean suppressScrollEvents = false;

    private final int pageSize = 10;
    private final int maxLoadedPages = 3;

    // Sorting state
    private String sorting = "DESC";
    private String sortBy = "START";

    // Date filters
    private String startDate = "01/01/2000";
    private String endDate = "31/12/2100";

    private List<RideHistoryUserModel> rides = new ArrayList<>();

    public UserRideHistoryFragment() {}

    public static UserRideHistoryFragment newInstance() {
        return new UserRideHistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_ride_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userService = new UserService();

        // Initialize shake sensor
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        initViews(view);
        initRecyclerView();
        initDatePicker(view);
        initSortBySpinner();
        initSortOrderButton();
        initApplyButton();

        loadInitialRides();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register shake sensor listener
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister shake sensor listener
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    // ── Shake Detection ──────────────────────────────────

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float acceleration = (float) Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;

            long currentTime = System.currentTimeMillis();
            if (acceleration > SHAKE_THRESHOLD && (currentTime - lastShakeTime > SHAKE_COOLDOWN_MS)) {
                lastShakeTime = currentTime;
                onShakeDetected();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for shake detection
    }

    private void onShakeDetected() {
        // Toggle sorting order
        sorting = sorting.equals("ASC") ? "DESC" : "ASC";
        updateSortOrderIcon();
        Toast.makeText(requireContext(), "Date sorting toggled: " + sorting, Toast.LENGTH_SHORT).show();
        scrollToTop();
    }

    // ── View Initialization ──────────────────────────────

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

        adapter = new UserRideHistoryAdapter(rides, this::openRideDetails);
        ridesRecyclerView.setAdapter(adapter);

        ridesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isLoading || suppressScrollEvents) return;

                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();

                if (firstVisibleItem == RecyclerView.NO_POSITION) return;

                // Only trigger load-newer when user is truly at the very top
                if (dy < 0 && firstVisibleItem == 0 && hasMoreNewer) {
                    loadNewer();
                }

                // Only trigger load-older when user is truly near the bottom
                if (dy > 0 && lastVisibleItem >= totalItemCount - 2 && hasMoreOlder) {
                    loadOlder();
                }
            }
        });
    }

    private void initSortBySpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.trip_history_sort_options,
                R.layout.custom_spinner
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

    // ── Data loading ─────────────────────────────────────

    private void loadInitialRides() {
        isLoading = true;
        suppressScrollEvents = true;
        hideTopLoading();
        hideBottomLoading();

        userService.getUserRideHistory(
                0, pageSize, sorting, sortBy, startDate, endDate,
                new UserService.Callback<RideHistoryUserPagingModel>() {
                    @Override
                    public void onSuccess(RideHistoryUserPagingModel response) {
                        rides.clear();
                        if (response.getUserHistory() != null) {
                            rides.addAll(response.getUserHistory());
                        }

                        oldestLoadedPage = 0;
                        newestLoadedPage = 0;
                        hasMoreOlder = !response.isReachedEnd();
                        hasMoreNewer = false;

                        adapter.setHasMoreNewer(hasMoreNewer);
                        adapter.setHasMoreOlder(hasMoreOlder);
                        adapter.notifyDataSetChanged();

                        ridesRecyclerView.post(() -> {
                            suppressScrollEvents = false;
                            isLoading = false;
                        });
                    }

                    @Override
                    public void onError(int code, String message) {
                        isLoading = false;
                        suppressScrollEvents = false;
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

        userService.getUserRideHistory(
                nextPage, pageSize, sorting, sortBy, startDate, endDate,
                new UserService.Callback<RideHistoryUserPagingModel>() {
                    @Override
                    public void onSuccess(RideHistoryUserPagingModel response) {
                        hideBottomLoading();

                        List<RideHistoryUserModel> newRides =
                                response.getUserHistory() != null
                                        ? response.getUserHistory()
                                        : new ArrayList<>();

                        int insertPosition = rides.size();
                        rides.addAll(newRides);

                        newestLoadedPage = nextPage;
                        hasMoreOlder = !response.isReachedEnd();
                        adapter.setHasMoreOlder(hasMoreOlder);

                        suppressScrollEvents = true;
                        adapter.notifyItemRangeInserted(insertPosition, newRides.size());

                        ridesRecyclerView.post(() -> {
                            trimOldPages();
                            ridesRecyclerView.post(() -> {
                                suppressScrollEvents = false;
                                isLoading = false;
                            });
                        });
                    }

                    @Override
                    public void onError(int code, String message) {
                        isLoading = false;
                        suppressScrollEvents = false;
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

        userService.getUserRideHistory(
                nextPage, pageSize, sorting, sortBy, startDate, endDate,
                new UserService.Callback<RideHistoryUserPagingModel>() {
                    @Override
                    public void onSuccess(RideHistoryUserPagingModel response) {
                        hideTopLoading();

                        List<RideHistoryUserModel> newRides =
                                response.getUserHistory() != null
                                        ? response.getUserHistory()
                                        : new ArrayList<>();

                        // Save scroll anchor before prepending
                        int firstVisible = layoutManager.findFirstVisibleItemPosition();
                        View firstVisibleView = layoutManager.findViewByPosition(firstVisible);
                        int topOffset = (firstVisibleView != null) ? firstVisibleView.getTop() : 0;

                        rides.addAll(0, newRides);

                        oldestLoadedPage = nextPage;
                        hasMoreNewer = nextPage > 0;
                        adapter.setHasMoreNewer(hasMoreNewer);

                        suppressScrollEvents = true;
                        adapter.notifyItemRangeInserted(0, newRides.size());

                        // Restore scroll: old firstVisible is now shifted by newRides.size()
                        layoutManager.scrollToPositionWithOffset(
                                firstVisible + newRides.size(), topOffset);

                        ridesRecyclerView.post(() -> {
                            trimNewPages();
                            ridesRecyclerView.post(() -> {
                                suppressScrollEvents = false;
                                isLoading = false;
                            });
                        });
                    }

                    @Override
                    public void onError(int code, String message) {
                        isLoading = false;
                        suppressScrollEvents = false;
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
        if (totalLoadedPages <= maxLoadedPages) return;

        int pagesToRemove = totalLoadedPages - maxLoadedPages;
        int itemsToRemove = pagesToRemove * pageSize;

        itemsToRemove = Math.min(itemsToRemove, rides.size() - pageSize);
        if (itemsToRemove <= 0) return;

        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleView = layoutManager.findViewByPosition(firstVisible);
        int topOffset = (firstVisibleView != null) ? firstVisibleView.getTop() : 0;

        for (int i = 0; i < itemsToRemove; i++) {
            rides.remove(0);
        }

        oldestLoadedPage += pagesToRemove;
        hasMoreNewer = true;
        adapter.setHasMoreNewer(hasMoreNewer);

        adapter.notifyItemRangeRemoved(0, itemsToRemove);

        int newPosition = firstVisible - itemsToRemove;
        if (newPosition >= 0 && newPosition < rides.size()) {
            layoutManager.scrollToPositionWithOffset(newPosition, topOffset);
        }
    }

    private void trimNewPages() {
        int totalLoadedPages = newestLoadedPage - oldestLoadedPage + 1;
        if (totalLoadedPages <= maxLoadedPages) return;

        int pagesToRemove = totalLoadedPages - maxLoadedPages;
        int itemsToRemove = pagesToRemove * pageSize;

        itemsToRemove = Math.min(itemsToRemove, rides.size() - pageSize);
        if (itemsToRemove <= 0) return;

        int removeFrom = rides.size() - itemsToRemove;

        for (int i = 0; i < itemsToRemove; i++) {
            rides.remove(rides.size() - 1);
        }

        newestLoadedPage -= pagesToRemove;
        hasMoreOlder = true;
        adapter.setHasMoreOlder(hasMoreOlder);

        adapter.notifyItemRangeRemoved(removeFrom, itemsToRemove);
    }

    // ── Navigation ───────────────────────────────────────

    private void openRideDetails(RideHistoryUserModel ride) {
        UserRideHistoryDetailedFragment fragment =
                UserRideHistoryDetailedFragment.newInstance(ride.getRideId());

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, fragment)
                .addToBackStack(null)
                .commit();
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
