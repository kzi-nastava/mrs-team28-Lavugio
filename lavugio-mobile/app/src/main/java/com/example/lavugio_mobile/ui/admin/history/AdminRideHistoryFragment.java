package com.example.lavugio_mobile.ui.admin.history;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.AdminHistoryModel;
import com.example.lavugio_mobile.models.AdminHistoryPagingModel;
import com.example.lavugio_mobile.repository.admin.AdminHistoryRepository;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminRideHistoryFragment extends Fragment {

    private EditText emailEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private boolean isSelectingStartDate = true;

    private RecyclerView ridesRecyclerView;
    private AdminRideHistoryAdapter adapter;
    private LinearLayoutManager layoutManager;

    private MaterialButton sortOrderButton;
    private MaterialButton searchButton;
    private Spinner sortBySpinner;

    private ProgressBar topLoadingIndicator;
    private ProgressBar bottomLoadingIndicator;

    private AdminHistoryRepository repository;

    // Pagination state
    private int currentPage = 0;
    private boolean hasMoreOlder = true;
    private boolean isLoading = false;

    private final int pageSize = 10;

    // Sorting state
    private String sorting = "DESC";
    private String sortBy = "START";

    // Date filters
    private String startDate = "01/01/2000";
    private String endDate = "31/12/2100";

    // Email filter
    private String email = "";

    private List<AdminHistoryModel> rides = new ArrayList<>();

    public AdminRideHistoryFragment() {}

    public static AdminRideHistoryFragment newInstance() {
        return new AdminRideHistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_ride_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new AdminHistoryRepository();

        initViews(view);
        initRecyclerView();
        initDatePicker(view);
        initSortBySpinner();
        initSortOrderButton();
        initSearchButton();
        initEmailInput();
    }

    private void initViews(View view) {
        emailEditText = view.findViewById(R.id.emailInputField);
        ridesRecyclerView = view.findViewById(R.id.ridesRecyclerView);
        sortOrderButton = view.findViewById(R.id.sortOrderButton);
        searchButton = view.findViewById(R.id.searchButton);
        sortBySpinner = view.findViewById(R.id.sortBySpinner);
        startDateEditText = view.findViewById(R.id.startDateInputField);
        endDateEditText = view.findViewById(R.id.endDateInputField);
        topLoadingIndicator = view.findViewById(R.id.topLoadingIndicator);
        bottomLoadingIndicator = view.findViewById(R.id.bottomLoadingIndicator);
    }

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(requireContext());
        ridesRecyclerView.setLayoutManager(layoutManager);

        adapter = new AdminRideHistoryAdapter(rides, this::openRideDetails);
        ridesRecyclerView.setAdapter(adapter);

        ridesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isLoading) return;

                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();

                if (dy > 0 && lastVisibleItem >= totalItemCount - 2 && hasMoreOlder) {
                    loadMore();
                }
            }
        });
    }

    private void initSortBySpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.admin_history_sort_options,
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
                    case 3: sortBy = "PRICE"; break;
                    case 4: sortBy = "CANCELLED"; break;
                    case 5: sortBy = "PANIC"; break;
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

    private void initSearchButton() {
        searchButton.setOnClickListener(v -> searchByEmail());
    }

    private void initEmailInput() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                email = s.toString().trim();
            }
        });
    }

    private void searchByEmail() {
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }
        currentPage = 0;
        rides.clear();
        adapter.notifyDataSetChanged();
        loadInitialRides();
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
        if (email.isEmpty()) return;

        isLoading = true;
        showTopLoading();
        hideBottomLoading();

        repository.getUserHistory(email, 0, pageSize, sorting, sortBy, startDate, endDate,
                new AdminHistoryRepository.AdminHistoryCallback<AdminHistoryPagingModel>() {
                    @Override
                    public void onSuccess(AdminHistoryPagingModel response) {
                        hideTopLoading();
                        rides.clear();
                        if (response.getAdminHistory() != null) {
                            rides.addAll(response.getAdminHistory());
                        }

                        currentPage = 0;
                        hasMoreOlder = !response.isReachedEnd();

                        adapter.notifyDataSetChanged();
                        isLoading = false;
                    }

                    @Override
                    public void onError(int code, String message) {
                        hideTopLoading();
                        isLoading = false;
                        Toast.makeText(requireContext(),
                                "Error loading rides: " + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadMore() {
        if (isLoading || !hasMoreOlder || email.isEmpty()) return;

        int nextPage = currentPage + 1;
        isLoading = true;
        showBottomLoading();

        repository.getUserHistory(email, nextPage, pageSize, sorting, sortBy, startDate, endDate,
                new AdminHistoryRepository.AdminHistoryCallback<AdminHistoryPagingModel>() {
                    @Override
                    public void onSuccess(AdminHistoryPagingModel response) {
                        hideBottomLoading();

                        List<AdminHistoryModel> newRides = response.getAdminHistory() != null
                                ? response.getAdminHistory()
                                : new ArrayList<>();

                        int insertPosition = rides.size();
                        rides.addAll(newRides);

                        currentPage = nextPage;
                        hasMoreOlder = !response.isReachedEnd();

                        adapter.notifyItemRangeInserted(insertPosition, newRides.size());
                        isLoading = false;
                    }

                    @Override
                    public void onError(int code, String message) {
                        hideBottomLoading();
                        isLoading = false;
                        Toast.makeText(requireContext(),
                                "Error loading older rides: " + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── Navigation ───────────────────────────────────────

    private void openRideDetails(AdminHistoryModel ride) {
        AdminRideHistoryDetailedFragment fragment =
                AdminRideHistoryDetailedFragment.newInstance(ride.getRideId(), email);

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
