package com.example.lavugio_mobile.ui.admin;

import android.os.Bundle;
import android.widget.Button;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.ui.admin.history.AdminRideHistoryFragment;
import com.example.lavugio_mobile.ui.dialog.PriceDefinitionDialogFragment;
import com.example.lavugio_mobile.ui.reports.RidesReportsFragment;

public class AdministratorPanelFragment extends Fragment {
    private Button btnRegisterDriver;
    private Button btnDriverUpdateRequests;
    private Button btnBlockUser;
    private Button btnUserHistory;
    private Button btnSeeReports;
    private Button btnPanicAlerts;
    private Button btnPriceDefinition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_panel, container, false);

        // Initialize buttons
        btnRegisterDriver = view.findViewById(R.id.btnRegisterDriver);
        btnDriverUpdateRequests = view.findViewById(R.id.btnDriverUpdateRequests);
        btnBlockUser = view.findViewById(R.id.btnBlockUser);
        btnUserHistory = view.findViewById(R.id.btnUserHistory);
        btnSeeReports = view.findViewById(R.id.btnSeeReports);
        btnPanicAlerts = view.findViewById(R.id.btnPanicAlerts);
        btnPriceDefinition = view.findViewById(R.id.btnPriceDefinition);

        btnPriceDefinition.setOnClickListener(v -> {
            PriceDefinitionDialogFragment dialog = PriceDefinitionDialogFragment.newInstance();
            dialog.show(getChildFragmentManager(), "price_definition");
        });

        // Set click listeners
        btnRegisterDriver.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new RegisterDriverFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnDriverUpdateRequests.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new DriverUpdateRequestsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnBlockUser.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new BlockUserFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnUserHistory.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, AdminRideHistoryFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        });

        btnSeeReports.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new RidesReportsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnPanicAlerts.setOnClickListener(v -> {
            // TODO: Navigate to Panic Alerts screen when implemented
        });

        return view;
    }
}
